package br.com.lugaid.business;

import java.util.List;

import br.com.lugaid.helper.StringHelper;

import com.sap.conn.jco.JCoParameterList;

public class SapFm2JavaHandlerClassConverter {
	private String className;
	private String importClassName;
	private JCoParameterList parmList;

	/**
	 * Constructor
	 * 
	 * @param mainClass
	 *            Class name
	 * @param importClassName
	 *            Class name for import parameters
	 * @param parmList
	 *            SAP parameter list
	 */
	public SapFm2JavaHandlerClassConverter(String className,
			String importClassName, JCoParameterList parmList) {
		this.className = className;
		this.importClassName = importClassName;
		this.parmList = parmList;
	}

	/**
	 * Return class content
	 * 
	 * @return Class content
	 */
	public String getClassString() {
		return generateClass().toString();
	}

	private StringBuilder generateClass() {
		List<Sap2JavaField> fields = Sap2JavaField.mapTypes(parmList
				.getFieldIterator());
		StringBuilder sb = new StringBuilder();
		
		sb.append("import com.sap.conn.jco.server.JCoServerContext;\n");
		sb.append("import com.sap.conn.jco.server.JCoServerFunctionHandler;\n");
		sb.append("import com.sap.conn.jco.AbapClassException;\n");
		sb.append("import com.sap.conn.jco.AbapException;\n");
		sb.append("import com.sap.conn.jco.JCoFunction;\n");
		sb.append("import com.sap.conn.jco.JCoParameterList;\n");
		sb.append("import com.sap.conn.jco.JCoStructure;\n");
		sb.append("import com.sap.conn.jco.JCoTable;\n");

		sb.append(String.format(
				"public class %s implements JCoServerFunctionHandler{\n",
				className));

		sb.append("public void handleRequest(JCoServerContext serverContext, "
				+ "JCoFunction function) throws AbapException, AbapClassException {\n");
		sb.append("JCoParameterList impParmList = function.getImportParameterList();\n");

		// Get fields from FM parameters
		for (Sap2JavaField field : fields) {
			if (field.getSapType().equals("STRUCTURE")) {
				sb.append(String.format(
						"JCoStructure %s = impParmList.getStructure(\"%s\");\n",
						field.getJavaName(), field.getSapName()));
			} else if (field.getSapType().equals("TABLE")) {
				sb.append(String.format(
						"JCoTable %s = impParmList.getTable(\"%s\");\n",
						field.getJavaName(), field.getSapName()));
			} else {
				sb.append(String.format("%s %s = impParmList.get%s(\"%s\");\n",
						field.getJavaType(), field.getJavaName(),
						field.getJCoType(), field.getSapName()));
			}
		}

		// Mount new object
		sb.append(String.format("%s %s = new %s(\n", importClassName,
				StringHelper.smallizeFirstChar(importClassName),
				importClassName));
		
		for (Sap2JavaField field : fields) {
			sb.append(field.getJavaName());
			
			if(!field.equals(fields.get(fields.size()-1))) {
				sb.append(",");
			}
			
			sb.append("\n");
		}
		

		return sb.append(");\n}\n}");
	}
}
