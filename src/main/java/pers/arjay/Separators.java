package pers.arjay;

public class Separators {

	/**
	 * 無號分割 (?!^) <BR>
	 * java 8 之前用來避免split("") 造成多一空白字元的Regex
	 */
	public static final String unsign = "(?!^)";

	/** 多符號分割 非數字、字母、底線[^a-zA-Z0-9_] */
	public static final String multiple = "\\W";

	/** 逗號 */
	public static final String comma = ",";

	/** 空白 */
	public static final String space = " ";
	
	/** 減號 */
	public static final String desh = "-";

}
