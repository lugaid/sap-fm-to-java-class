package br.com.lugaid.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoParameterList;
import static br.com.lugaid.helper.StringHelper.captalizeFirstChar;

public class SapFmParm2JavaClassConverter {
	private static Logger logger = LoggerFactory
			.getLogger(SapFmParm2JavaClassConverter.class);

	private String mainClass;
	private JCoParameterList parmList;
	
	public SapFmParm2JavaClassConverter(String mainClass, JCoParameterList parmList) {
		this.mainClass = mainClass;
		this.parmList = parmList;
	}

	public String getClassString() {
		return generateClass(mainClass, mapTypes(parmList.iterator())).toString();
	}

	private List<Sap2JavaField> mapTypes(Iterator<JCoField> parmIterator) {
		List<Sap2JavaField> locFields = new ArrayList<>();
		
		while (parmIterator.hasNext()) {
			JCoField field = parmIterator.next();

			logger.info("Field {} type {} length {} decimal {}.",
					field.getName(), field.getTypeAsString(),
					field.getLength(), field.getDecimals());

			if (field.getTypeAsString().equals("STRUCTURE")) {
				List<Sap2JavaField> subFields = mapTypes(field.getStructure()
						.iterator());
				locFields.add(new Sap2JavaField(field.getName(), field
						.getTypeAsString(), field.getLength(), field
						.getDecimals(), subFields));
			} else if (field.getTypeAsString().equals("TABLE")) {
				List<Sap2JavaField> subFields = mapTypes(field.getTable()
						.iterator());
				locFields.add(new Sap2JavaField(field.getName(), field
						.getTypeAsString(), field.getLength(), field
						.getDecimals(), subFields));
			} else {
				locFields.add(new Sap2JavaField(field.getName(), field
						.getTypeAsString(), field.getLength(), field
						.getDecimals()));
			}
		}

		return locFields;
	}

	private StringBuilder generateClass(String className, List<Sap2JavaField> fields) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("public class %s implements Serializable {\n", className.trim()));
		sb.append("private static final long serialVersionUID = 337339270983782151L;\n");

		// mount attributes of class
		for (Sap2JavaField field : fields) {
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
						field.getJavaName(), sbField.getJavaType(),
						sbField.getSapName()));
			}
		}

		return String.format("new %s(%s)", field.getJavaType(), sbParm);
	}
}
