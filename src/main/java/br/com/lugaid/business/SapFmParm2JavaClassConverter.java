package br.com.lugaid.business;

import java.util.List;
import com.sap.conn.jco.JCoParameterList;

import static br.com.lugaid.helper.StringHelper.captalizeFirstChar;

/**
 * Class to transform a list of SAP parameter to class file.
 * 
 * @author Emerson Rancoletta
 * @version 1.0
 */
public class SapFmParm2JavaClassConverter {
	private String className;
	private JCoParameterList parmList;

	/**
	 * Constructor
	 * 
	 * @param className
	 *            Class name
	 * @param parmList
	 *            SAP parameter list
	 */
	public SapFmParm2JavaClassConverter(String className,
			JCoParameterList parmList) {
		this.className = className;
		this.parmList = parmList;
	}

	/**
	 * Return class content
	 * 
	 * @return Class content
	 */
	public String getClassString() {
		StringBuilder sb = new StringBuilder();

		sb.append("import java.io.Serializable;\n");
		sb.append("import java.math.BigDecimal;\n");
		sb.append("import java.util.ArrayList;\n");
		sb.append("import java.util.Date;\n");
		sb.append("import java.util.List;\n");
		sb.append("import com.sap.conn.jco.JCoStructure;\n");
		sb.append("import com.sap.conn.jco.JCoTable;\n");

		sb.append(generateClass(className,
				Sap2JavaField.mapTypes(parmList.getFieldIterator())));

		return sb.toString();
	}

	/**
	 * Generate class content
	 * 
	 * @param className
	 *            Class name
	 * @param fields
	 *            Fields of parameter
	 * @return Class content
	 */
	private StringBuilder generateClass(String className,
			List<Sap2JavaField> fields) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("public class %s implements Serializable {\n",
				className.trim()));
		sb.append("private static final long serialVersionUID = 337339270983782151L;\n");

		// mount attributes of class
		for (Sap2JavaField field : fields) {
			sb.append(String.format("// %s\n", field.getSapDescription()));
			if (field.getSapType().equals("TABLE")) {
				sb.append(String.format("private List<%s> %s;\n", field
						.getJavaType().trim(), field.getJavaName().trim()));
			} else {
				sb.append(String.format("private %s %s;\n", field.getJavaType()
						.trim(), field.getJavaName().trim()));
			}
		}

		// constructor
		StringBuilder sbParm = new StringBuilder();
		for (Sap2JavaField field : fields) {
			if (!sbParm.toString().trim().isEmpty()) {
				sbParm.append(",\n");
			}

			if (field.getSapType().equals("STRUCTURE")) {
				sbParm.append(String.format("JCoStructure %s",
						field.getJavaName()));
			} else if (field.getSapType().equals("TABLE")) {
				sbParm.append(String.format("JCoTable %s", field.getJavaName()));
			} else {
				sbParm.append(String.format("%s %s", field.getJavaType(),
						field.getJavaName()));
			}
		}
		sb.append(String.format("public %s(%s) {\n", className.trim(), sbParm));
		for (Sap2JavaField field : fields) {
			if (field.getSapType().equals("STRUCTURE")) {

				sb.append(String.format("this.%s = %s;\n", field.getJavaName(),
						buildNewObject(field)));
			} else if (field.getSapType().equals("TABLE")) {
				sb.append(String.format("this.%s = new ArrayList<>();\n",
						field.getJavaName()));

				sb.append(String.format("%s.firstRow();\n", field.getJavaName()));
				sb.append(String
						.format("for (int i = 0; i < %s.getNumRows(); %s.nextRow()) {\n",
								field.getJavaName(), field.getJavaName()));
				sb.append(String.format("this.%s.add(%s);\n",
						field.getJavaName(), buildNewObject(field)));
				sb.append("i++;\n}\n");
			} else {
				sb.append(String.format("this.%s = %s;\n", field.getJavaName(),
						field.getJavaName()));
			}
		}
		sb.append("}\n");

		// mount getters
		for (Sap2JavaField field : fields) {

			if (field.getSapType().equals("TABLE")) {
				sb.append(String.format("public List<%s> get%s() {\n",
						field.getJavaType(),
						captalizeFirstChar(field.getJavaName())));
			} else {
				sb.append(String.format("public %s get%s() {\n",
						field.getJavaType(),
						captalizeFirstChar(field.getJavaName())));
			}

			sb.append(String.format("return this.%s;\n}\n", field.getJavaName()));
		}

		// mount subclasses
		for (Sap2JavaField field : fields) {
			if (!field.isFinalLevel()) {
				sb.append(generateClass(field.getJavaType().trim(),
						field.getListSubField()));
			}
		}

		sb.append("}\n");
		return sb;
	}

	private String buildNewObject(Sap2JavaField field) {
		StringBuilder sbParm = new StringBuilder();
		for (Sap2JavaField sbField : field.getListSubField()) {
			if (!sbParm.toString().trim().isEmpty()) {
				sbParm.append(",\n");
			}

			if (sbField.getSapType().equals("STRUCTURE")) {
				sbParm.append(String.format("%s.getStructure(\"%s\")",
						field.getJavaName(), sbField.getSapName()));
			} else if (sbField.getSapType().equals("TABLE")) {
				sbParm.append(String.format("%s.getTable(\"%s\")",
						field.getJavaName(), sbField.getSapName()));
			} else {
				sbParm.append(String.format("%s.get%s(\"%s\")",
						field.getJavaName(), sbField.getJCoType(),
						sbField.getSapName()));
			}
		}

		return String.format("new %s(%s)", field.getJavaType(), sbParm);
	}
}
