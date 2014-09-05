package br.com.lugaid.business;

import java.util.List;
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
	private String sapName;
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
	public Sap2JavaField(String sapName, String sapType, int sapLength,
			int sapDecimals) {
		this.sapName = sapName;
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
	public Sap2JavaField(String sapName, String sapType, int sapLength,
			int sapDecimals, List<Sap2JavaField> listSubField) {
		this(sapName, sapType, sapLength, sapDecimals);
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
}
