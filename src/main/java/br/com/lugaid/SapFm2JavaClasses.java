package br.com.lugaid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

import br.com.lugaid.business.SapFmParm2JavaClassConverter;

/**
 * Class that create .java files for SAP Function Mocules
 * 
 * @author Emerson Rancoletta
 * @version 1.0
 */
public class SapFm2JavaClasses {
	private static Logger logger = LoggerFactory
			.getLogger(SapFm2JavaClasses.class);
	private static final String DESTINATION_NAME = "SAP_CONNECTION";

	private String mainClass;
	private String functionMod;
	private Path path;
	private JCoDestination destination;
	private JCoFunction function;

	/**
	 * Constructor
	 * 
	 * @param mainClass Name of the main class
	 * @param functionMod SAP Function Module name
	 * @param path Path to write .java files
	 */
	public SapFm2JavaClasses(String mainClass, String functionMod, Path path) {
		this.mainClass = mainClass;
		this.functionMod = functionMod;
		this.path = path;
	}

	/**
	 * Generate .java files
	 */
	public void generateClasses() {
		logger.info("Starting generation classes for {} FM.", functionMod);

		defineJCoDestination();

		defineJCoFunction();

		generateImportParamClass();

		generateExportParamClass();

		generateChangingParamClass();

		generateTableParamClass();
	}

	/**
	 * Connect to SAP via JCoDestination, must exists file SAP_CONNECTION.jcoDestination
	 * into start directory.
	 */
	private void defineJCoDestination() {
		try {
			logger.info("Starting connection to SAP.");

			JCoDestination destination = JCoDestinationManager
					.getDestination(DESTINATION_NAME);

			logger.info("Connection succesfully started to SAP.");
			logger.info("==============================================================");
			logger.info("|                   SAP connection attributes");
			logger.info("| Client: {}", destination.getClient());
			logger.info("| User: {}", destination.getUser());
			logger.info("| Language: {}", destination.getLanguage());
			logger.info("| Sysnr: {}", destination.getSystemNumber());
			logger.info("| Host: {}", destination.getApplicationServerHost());
			logger.info("| GatewayService: {}", destination.getGatewayService());
			logger.info("| RepositoryDestination: {}", DESTINATION_NAME);
			logger.info("==============================================================");

			this.destination = destination;
		} catch (JCoException e) {
			logger.error("Error on get SAP server {}.", DESTINATION_NAME);
			logger.debug("Stack trace ", e);
			throw new IllegalStateException(
					"SAP destination error exiting system.", e);
		}
	}

	/**
	 * Get Function Module meta-data.
	 */
	private void defineJCoFunction() {
		try {
			logger.info("Get function module information for {}.", functionMod);

			this.function = destination.getRepository()
					.getFunction(functionMod);
		} catch (JCoException e) {
			logger.error("Error on get SAP Function Module {}.", functionMod);
			logger.debug("Stack trace ", e);
			throw new IllegalStateException("Exiting system.", e);
		}
	}

	/**
	 * Generate .java file for Import parameters of function module.
	 */
	private void generateImportParamClass() {
		JCoParameterList parmList = function.getImportParameterList();

		generateParamClass("Import", parmList);
	}
	
	/**
	 * Generate .java file for Export parameters of function module.
	 */
	private void generateExportParamClass() {
		JCoParameterList parmList = function.getExportParameterList();

		generateParamClass("Export", parmList);
	}
	
	/**
	 * Generate .java file for Changing parameters of function module.
	 */
	private void generateChangingParamClass() {
		JCoParameterList parmList = function.getChangingParameterList();

		generateParamClass("Changing", parmList);
	}

	/**
	 * Generate .java file for Table parameters of function module.
	 */
	private void generateTableParamClass() {
		JCoParameterList parmList = function.getTableParameterList();

		generateParamClass("Table", parmList);
	}
	
	/**
	 * Generic generator of .java file for parameters of function module.
	 */
	private void generateParamClass(String typeParam, JCoParameterList parmList) {
		String className = mainClass.concat(typeParam);

		logger.info("Starting generation class for {} of {} FM.", className,
				functionMod);

		if (parmList != null && parmList.getFieldCount() > 0) {
			writeClassFile(className, new SapFmParm2JavaClassConverter(
					className, parmList).getClassString());
		} else {
			logger.info(
					"{} parameter list for FM {} is blank, class will not be generated.",
					typeParam, functionMod);
		}
	}

	/**
	 * Write class into disk.
	 * 
	 * @param className Name of the class
	 * @param fileContent Content of the class
	 */
	private void writeClassFile(String className, String fileContent) {
		Path pathFile = FileSystems.getDefault().getPath(path.toString(),
				className.concat(".java"));
		logger.info("Writting file {}.", pathFile.toString());

		FileOutputStream fop = null;
		File file = pathFile.toFile();

		try {
			fop = new FileOutputStream(file);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = fileContent.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		} catch (IOException e) {
			logger.error("Error on write file {}.", pathFile.toString());
			logger.debug("Stack trace ", e);
			throw new IllegalStateException("Exiting system.", e);
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				logger.error("Error on close file {}.", pathFile.toString());
				logger.debug("Stack trace ", e);
				throw new IllegalStateException("Exiting system.", e);
			}
		}
	}
}
