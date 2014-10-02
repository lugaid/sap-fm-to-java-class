package br.com.lugaid.business;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class ParameterClassGenerator {
	private static Logger logger = LoggerFactory
			.getLogger(HandlerClassGenerator.class);
	private String paramClassName;
	private List<Sap2JavaField> fields;
	private Sap2JavaField parentField;

	public ParameterClassGenerator(String paramClassName,
			List<Sap2JavaField> fields, Sap2JavaField parentField) {
		this(paramClassName, fields);
		this.parentField = parentField;
	}

	public ParameterClassGenerator(String paramClassName,
			List<Sap2JavaField> fields) {
		this.paramClassName = paramClassName;
		this.fields = fields;
	}

	public String getParamClassName() {
		return paramClassName;
	}

	public void setParamClassName(String paramClassName) {
		this.paramClassName = paramClassName;
	}

	public List<Sap2JavaField> getFields() {
		return fields;
	}

	public void setFields(List<Sap2JavaField> fields) {
		this.fields = fields;
	}

	public Sap2JavaField getParentField() {
		return parentField;
	}

	public void setParentField(Sap2JavaField parentField) {
		this.parentField = parentField;
	}

	public boolean writeClassFile(FileWriter fileWriter) {
		logger.info("Writting Handler class.");

		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile("templates/ParameterTemplate.mustache");
		try {
			mustache.execute(fileWriter, this).flush();
			return true;
		} catch (IOException e) {
			logger.error("Error writting Handler class.");
			logger.debug("Stack trace ", e);
			return false;
		}
	}

	public String subClasses() {
		StringBuffer sb = new StringBuffer();

		for (Sap2JavaField field : fields) {
			if (!field.isFinalLevel()) {
				ParameterClassGenerator subClass = new ParameterClassGenerator(
						field.getJavaClassName(), field.getListSubField(),
						field);
				
				sb.append(subClass.getClassContent());
			}
		}

		return sb.toString();
	}

	private String getClassContent() {
		logger.info("Building sub class {}.", paramClassName);

		StringWriter sw = new StringWriter();
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile("templates/ParameterTemplateClass.mustache");
		try {
			mustache.execute(sw, this).flush();
			return sw.toString();
		} catch (IOException e) {
			logger.error("Error to build class {}.", paramClassName);
			logger.debug("Stack trace ", e);
			return "";
		}
	}
}
