package br.com.lugaid;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

import br.com.lugaid.business.CallerClassGenerator;
import br.com.lugaid.business.HandlerClassGenerator;
import br.com.lugaid.business.ParameterClassGenerator;
import br.com.lugaid.business.Sap2JavaField;

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

	private String functionMod;
	private String mainClass;
	private Path path;

	private String importClassName;
	private String exportClassName;
	private String changingClassName;
	private String tableClassName;
	private JCoParameterList importParmList;
	private JCoParameterList exportParmList;
	private JCoParameterList changingParmList;
	private JCoParameterList tableParmList;
	private String handlerClassName;
	private String callerClassName;

	private JCoDestination destination;
	private JCoFunction function;

	/**
	 * Constructor
	 * 
	 * @param mainClass
	 *            Name of the main class
	 * @param functionMod
	 *            SAP Function Module name
	 * @param path
	 *            Path to write .java files
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

		generateHandlerClass();

		generateCallerClass();
	}

	/**
	 * Connect to SAP via JCoDestination, must exists file
	 * SAP_CONNECTION.jcoDestination into start directory.
	 */
	private void defineJCoDestination() {
		try {
			logger.info("Starting connection to SAP.");

			logger.info("Setting JCo loggin level to 10.");

			JCo.setTrace(10, "logs/");

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
			logger.info("| RepositoryDestination: {}", DESTINATION_NAME);
			logger.info("==============================================================");
			logger.info("Setting JCo loggin level to 10.");

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
		importClassName = mainClass.concat("Import");

		importParmList = function.getImportParameterList();

		generateParamClass(importClassName, importParmList);
	}

	/**
	 * Generate .java file for Export parameters of function module.
	 */
	private void generateExportParamClass() {
		exportClassName = mainClass.concat("Export");

		exportParmList = function.getExportParameterList();

		generateParamClass(exportClassName, exportParmList);
	}

	/**
	 * Generate .java file for Changing parameters of function module.
	 */
	private void generateChangingParamClass() {
		changingClassName = mainClass.concat("Changing");

		changingParmList = function.getChangingParameterList();

		generateParamClass(changingClassName, changingParmList);
	}

	/**
	 * Generate .java file for Table parameters of function module.
	 */
	private void generateTableParamClass() {
		tableClassName = mainClass.concat("Table");

		tableParmList = function.getTableParameterList();

		generateParamClass(tableClassName, tableParmList);
	}

	/**
	 * Generate .java file for Handler class.
	 */
	private void generateHandlerClass() {
		handlerClassName = mainClass.concat("Handler");

		Path pathFile = FileSystems.getDefault().getPath(path.toString(),
				handlerClassName.concat(".java"));

		logger.info("Handler class path {}.", pathFile.toString());

		try {
			FileWriter fileWriter = new FileWriter(pathFile.toFile());

			HandlerClassGenerator handlerClassGenerator = new HandlerClassGenerator(
					functionMod, mainClass, handlerClassName, importClassName,
					exportClassName, changingClassName, tableClassName,
					importParmList, exportParmList, changingParmList,
					tableParmList);

			handlerClassGenerator.writeClassFile(fileWriter);
		} catch (IOException e) {
			logger.error("Error writting Caller class.");
			logger.debug("Stack trace ", e);
		}
	}

	/**
	 * Generate .java file for Caller class.
	 */
	private void generateCallerClass() {
		callerClassName = mainClass.concat("Caller");

		Path pathFile = FileSystems.getDefault().getPath(path.toString(),
				callerClassName.concat(".java"));

		logger.info("Caller class path {}.", pathFile.toString());

		try {
			FileWriter fileWriter = new FileWriter(pathFile.toFile());

			CallerClassGenerator callerClassGenerator = new CallerClassGenerator(
					functionMod, mainClass, callerClassName, importClassName,
					exportClassName, changingClassName, tableClassName,
					importParmList, exportParmList, changingParmList,
					tableParmList);

			callerClassGenerator.writeClassFile(fileWriter);
		} catch (IOException e) {
			logger.error("Error writting Caller class.");
			logger.debug("Stack trace ", e);
		}
	}

	/**
	 * Generic generator of .java file for parameters of function module.
	 */
	private void generateParamClass(String className, JCoParameterList parmList) {
		if (parmList != null && parmList.getFieldCount() > 0) {
			logger.info("Starting generation class {} for {} FM.", className,
					functionMod);

			Path pathFile = FileSystems.getDefault().getPath(path.toString(),
					className.concat(".java"));

			try {
				FileWriter fileWriter = new FileWriter(pathFile.toFile());

				ParameterClassGenerator parameterClassGenerator = new ParameterClassGenerator(
						className, Sap2JavaField.mapTypes(parmList
								.getFieldIterator()));

				parameterClassGenerator.writeClassFile(fileWriter);
			} catch (IOException e) {
				logger.error("Error writting Caller class.");
				logger.debug("Stack trace ", e);
			}
		} else {
			logger.info(
					"Parameter list for FM {} is blank, class {} will not be generated.",
					functionMod, className);
		}
	}
}
