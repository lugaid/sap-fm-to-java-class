package br.com.lugaid.helper;

/**
 * Some helpers to handle strings
 * 
 * @author Emerson Rancoletta
 * @version 1.0
 */
public class StringHelper {
	/**
	 * Capitalizes all the words
	 * 
	 * @param givenString String to be fomatted
	 * @return Fomatted string
	 */
	public static String titleize(String givenString) {
		givenString = givenString.toLowerCase();
		String[] arr = givenString.split(" ");
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < arr.length; i++) {
			sb.append(Character.toUpperCase(arr[i].charAt(0)))
					.append(arr[i].substring(1)).append(" ");
		}
		return sb.toString().trim();
	}

	/**
	 * Smallize just first character
	 * 
	 * @param givenString String to be fomatted
	 * @return Fomatted string
	 */
	public static String smallizeFirstChar(String givenString) {
		StringBuffer sb = new StringBuffer();
		sb.append(Character.toLowerCase(givenString.charAt(0))).append(
				givenString.substring(1));
		return sb.toString().trim();
	}
	
	/**
	 * Captalize just first character
	 * 
	 * @param givenString String to be fomatted
	 * @return Fomatted string
	 */
	public static String captalizeFirstChar(String givenString) {
		StringBuffer sb = new StringBuffer();
		sb.append(Character.toUpperCase(givenString.charAt(0))).append(
				givenString.substring(1));
		return sb.toString().trim();
	}
}
