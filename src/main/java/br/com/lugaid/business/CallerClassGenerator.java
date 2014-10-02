package br.com.lugaid.business;

import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.sap.conn.jco.JCoParameterList;

import static br.com.lugaid.helper.StringHelper.smallizeFirstChar;

public class CallerClassGenerator {
	private static Logger logger = LoggerFactory
			.getLogger(CallerClassGenerator.class);

	private String functionMod;
	private String mainClass;
	private String callerClassName;
	private String importClassName;
	private String exportClassName;
	private String changingClassName;
	private String tableClassName;
	private JCoParameterList importParmList;
	private JCoParameterList exportParmList;
	private JCoParameterList changingParmList;
	private JCoParameterList tableParmList;

	public CallerClassGenerator(String functionMod, String mainClass,
			String callerClassName, String importClassName,
			String exportClassName, String changingClassName,
			String tableClassName, JCoParameterList importParmList,
			JCoParameterList exportParmList, JCoParameterList changingParmList,
			JCoParameterList tableParmList) {
		this.functionMod = functionMod;
		this.mainClass = mainClass;
		this.callerClassName = callerClassName;
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

	public String getCallerClassName() {
		return callerClassName;
	}

	public void setCallerClassName(String callerClassName) {
		this.callerClassName = callerClassName;
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

	public String buildCallerParameter() {
		StringBuffer sb = new StringBuffer();

		if (hasImportParameters()) {
			sb.append(String.format("%s %s,", getImportClassName(),
					importObjectName()));
		}

		if (hasExportParameters()) {
			sb.append(String.format("%s %s,", getExportClassName(),
					exportObjectName()));
		}

		if (hasChangingParameters()) {
			sb.append(String.format("%s %s,", getChangingClassName(),
					changingObjectName()));
		}

		if (hasTableParameters()) {
			sb.append(String.format("%s %s,", getTableClassName(),
					tableObjectName()));
		}
		
		Splitter splitter = Splitter.on(',').omitEmptyStrings().trimResults();
	    Joiner joiner = Joiner.on(',').skipNulls();

		return joiner.join(splitter.split(sb.toString()));
	}

	public boolean writeClassFile(FileWriter fileWriter) {
		logger.info("Writting Caller class.");

		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile("templates/CallerTemplate.mustache");
		try {
			mustache.execute(fileWriter, this).flush();
			return true;
		} catch (IOException e) {
			logger.error("Error writting Caller class.");
			logger.debug("Stack trace ", e);
			return false;
		}
	}
}
