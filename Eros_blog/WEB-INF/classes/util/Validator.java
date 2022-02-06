package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 檢核功能
 */
public class Validator {
	
	/**
	 * @param args
	 */
	public static void main(String args[]) {

	}
	
	/**
	 * @param emailAddress
	 * @return
	 */
	public static boolean isValidEmailAddress(String emailAddress) {
		String expression = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = emailAddress;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}

	/**
	 * @param account
	 * @return
	 */
	public static boolean isValidEngNumString(String account) {
		String expression = "^[A-Za-z0-9]+$";
		CharSequence inputStr = account;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}
	
}
