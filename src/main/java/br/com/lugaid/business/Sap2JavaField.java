package br.com.lugaid.business;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import static br.com.lugaid.helper.StringHelper.captalizeFirstChar;
import static br.com.lugaid.helper.StringHelper.smallizeFirstChar;
import static br.com.lugaid.helper.StringHelper.titleize;

/**
 * Class to map SAP types to Java types
 * 
 * @author Emerson Rancoletta
 * @version = 1.0
 */
public class Sap2JavaField {
	private static Logger logger = LoggerFactory
			.getLogger(Sap2JavaField.class);
	
	private String sapName;
	private String sapDescription;
	private String sapType;
	private int sapLength;
	private int sapDecimals;
	private List<Sap2JavaField> listSubField;

	/**
	 * Constructor for final types, not for STRUCTURES or TABLES
	 * that have subtypes, for these types use the constructor 
	 * with sub fields list as parameter.
	 * 
	 * @param sapName SAP parameter name
	 * @param sapType SAP data type
	 * @param sapLength SAP length
	 * @param sapDecimals SAP decimal length
	 */
	public Sap2JavaField(String sapName, String sapDescription, String sapType, int sapLength,
			int sapDecimals) {
		this.sapName = sapName;
		this.sapDescription = sapDescription;
		this.sapType = sapType;
		this.sapLength = sapLength;
		this.sapDecimals = sapDecimals;
	}
	
	/**
	 * Constructor for STRUCTURES or TABLES that have subtypes.
	 * 
	 * @param sapName SAP parameter name
	 * @param sapType SAP data type
	 * @param sapLength SAP length
	 * @param sapDecimals SAP decimal length
	 */
	public Sap2JavaField(String sapName, String sapDescription, String sapType, int sapLength,
			int sapDecimals, List<Sap2JavaField> listSubField) {
		this(sapName, sapDescription, sapType, sapLength, sapDecimals);
		this.listSubField = listSubField;
	}

	/**
	 * Return SAP parameter name
	 * 
	 * @return SAP parameter name
	 */
	public String getSapName() {
		return sapName;
	}

	/**
	 * Return SAP parameter description
	 * 
	 * @return SAP parameter description
	 */
	public String getSapDescription() {
		return sapDescription;
	}

	/**
	 * Return JAVA attribute name
	 * 
	 * @return JAVA attribute name
	 */
	public String getJavaName() {
		return smallizeFirstChar(titleize(sapName.replaceAll("[-_.]", " "))
				.replace(" ", ""));
	}

	/**
	 * Return SAP data type
	 * 
	 * @return SAP data type
	 */
	public String getSapType() {
		return sapType;
	}

	/**
	 * Return JAVA data type correspondent to SAP data type
	 * 
	 * @return JAVA data type
	 */
	public String getJavaType() {
		if (sapType.equals("STRUCTURE")) {
			return captalizeFirstChar(getJavaName());
		} else if (sapType.equals("TABLE")) {
			return captalizeFirstChar(getJavaName());
		} else {
			String type = "";
			switch (sapType) {
			case "CHAR":
				type = "String";
				break;
			case "DATE":
				type = "Date";
				break;
			case "STRING":
				type = "String";
				break;
			case "NUM":
				type = "Long";
				break;
			case "BCD":
				type = "BigDecimal";
				break;
			case "INT":
				type = "Integer";
				break;
			default:
				type = "UNAVAILABLE";
				break;
			}
			return type;
		}
	}

	/**
	 * Return JCo data type correspondent to SAP data type
	 * 
	 * @return JCo Type
	 */
	public String getJCoType() {
		if (sapType.equals("INT")) {
			return "Int";
		} else {
			return getJavaType();
		}
	}
	
	/**
	 * Get SAP length
	 * 
	 * @return SAP length
	 */
	public int getSapLength() {
		return sapLength;
	}

	/**
	 * Get SAP decimals
	 * 
	 * @return SAP decimals length
	 */
	public int getSapDecimals() {
		return sapDecimals;
	}

	/**
	 * Get sub fields for STRUCTURE and TABLE
	 * 
	 * @return Sub fields for STRUCTURE and TABLE
	 */
	public List<Sap2JavaField> getListSubField() {
		return listSubField;
	}

	/**
	 * Retur if it is a final type, just STRUCTURE and TABLE is not a final type.
	 * 
	 * @return Final type, just STRUCTURE and TABLE is not a final type.
	 */
	public boolean isFinalLevel() {
		return !sapType.equals("STRUCTURE") && !sapType.equals("TABLE");
	}
	
	/**
	 * Transform SAP field list to a easy to handle class Sap2JavaField
	 * 
	 * @param parmIterator
	 *            Iterator of SAP fields
	 * @return List of Sap2JavaField
	 */
	public static List<Sap2JavaField> mapTypes(JCoFieldIterator parmIterator) {
		List<Sap2JavaField> locFields = new ArrayList<>();

		parmIterator.reset();
		while (parmIterator.hasNextField()) {
			JCoField field = parmIterator.nextField();

			logger.info("Field {} type {} length {} decimal {}.",
					field.getName(), field.getTypeAsString(),
					field.getLength(), field.getDecimals());

			if (field.getTypeAsString().equals("STRUCTURE")) {
				List<Sap2JavaField> subFields = mapTypes(field.getStructure()
						.getFieldIterator());
				locFields.add(new Sap2JavaField(field.getName(), field
						.getDescription(), field.getTypeAsString(), field
						.getLength(), field.getDecimals(), subFields));
			} else if (field.getTypeAsString().equals("TABLE")) {
				List<Sap2JavaField> subFields = mapTypes(field.getTable()
						.getFieldIterator());
				locFields.add(new Sap2JavaField(field.getName(), field
						.getDescription(), field.getTypeAsString(), field
						.getLength(), field.getDecimals(), subFields));
			} else {
				locFields.add(new Sap2JavaField(field.getName(), field
						.getDescription(), field.getTypeAsString(), field
						.getLength(), field.getDecimals()));
			}
		}

		return locFields;
	}
}
