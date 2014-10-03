package br.com.lugaid.business;

import java.io.FileWriter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sap.conn.jco.JCoParameterList;
import static br.com.lugaid.helper.StringHelper.smallizeFirstChar;

/**
 * Class to generate Handler class file
 * 
 * @author Emerson Rancoletta
 * @version = 1.0
 */
public class HandlerClassGenerator {
	private static Logger logger = LoggerFactory
			.getLogger(HandlerClassGenerator.class);

	private String functionMod;
	private String mainClass;
	private String handlerClassName;
	private String importClassName;
	private String exportClassName;
	private String changingClassName;
	private String tableClassName;
	private JCoParameterList importParmList;
	private JCoParameterList exportParmList;
	private JCoParameterList changingParmList;
	private JCoParameterList tableParmList;

	public HandlerClassGenerator(String functionMod, String mainClass,
			String handlerClassName, String importClassName,
			String exportClassName, String changingClassName,
			String tableClassName, JCoParameterList importParmList,
			JCoParameterList exportParmList, JCoParameterList changingParmList,
			JCoParameterList tableParmList) {
		this.functionMod = functionMod;
		this.mainClass = mainClass;
		this.handlerClassName = handlerClassName;
		this.importClassName = importClassName;
		this.exportClassName = exportClassName;
		this.changingClassName = changingClassName;
		this.tableClassName = tableClassName;
		this.importParmList = importParmList;
		this.exportParmList = exportParmList;
		this.changingParmList = changingParmList;
		this.tableParmList = tableParmList;
	}

	public String getFunctionMod() {
		return functionMod;
	}

	public void setFunctionMod(String functionMod) {
		this.functionMod = functionMod;
	}

	public String getMainClass() {
		return mainClass;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public String getHandlerClassName() {
		return handlerClassName;
	}

	public void setHandlerClassName(String handlerClassName) {
		this.handlerClassName = handlerClassName;
	}

	public String getImportClassName() {
		return importClassName;
	}

	public void setImportClassName(String importClassName) {
		this.importClassName = importClassName;
	}

	public String getExportClassName() {
		return exportClassName;
	}

	public void setExportClassName(String exportClassName) {
		this.exportClassName = exportClassName;
	}

	public String getChangingClassName() {
		return changingClassName;
	}

	public void setChangingClassName(String changingClassName) {
		this.changingClassName = changingClassName;
	}

	public String getTableClassName() {
		return tableClassName;
	}

	public void setTableClassName(String tableClassName) {
		this.tableClassName = tableClassName;
	}

	public JCoParameterList getImportParmList() {
		return importParmList;
	}

	public void setImportParmList(JCoParameterList importParmList) {
		this.importParmList = importParmList;
	}

	public JCoParameterList getExportParmList() {
		return exportParmList;
	}

	public void setExportParmList(JCoParameterList exportParmList) {
		this.exportParmList = exportParmList;
	}

	public JCoParameterList getChangingParmList() {
		return changingParmList;
	}

	public void setChangingParmList(JCoParameterList changingParmList) {
		this.changingParmList = changingParmList;
	}

	public JCoParameterList getTableParmList() {
		return tableParmList;
	}

	public void setTableParmList(JCoParameterList tableParmList) {
		this.tableParmList = tableParmList;
	}

	public boolean hasImportParameters() {
		return importParmList != null && importParmList.getFieldCount() > 0;
	}

	public boolean hasExportParameters() {
		return exportParmList != null && exportParmList.getFieldCount() > 0;
	}

	public boolean hasChangingParameters() {
		return changingParmList != null && changingParmList.getFieldCount() > 0;
	}

	public boolean hasTableParameters() {
		return tableParmList != null && tableParmList.getFieldCount() > 0;
	}

	public String importObjectName() {
		return smallizeFirstChar(importClassName);
	}

	public String exportObjectName() {
		return smallizeFirstChar(exportClassName);
	}

	public String changingObjectName() {
		return smallizeFirstChar(changingClassName);
	}

	public String tableObjectName() {
		return smallizeFirstChar(tableClassName);
	}

	public boolean writeClassFile(FileWriter fileWriter) {
		logger.info("Writting Handler class.");

		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile("templates/HandlerTemplate.mustache");
		try {
			mustache.execute(fileWriter, this).flush();
			return true;
		} catch (IOException e) {
			logger.error("Error writting Handler class.");
			logger.debug("Stack trace ", e);
			return false;
		}
	}
}
