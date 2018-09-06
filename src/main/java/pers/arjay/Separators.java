package pers.arjay;

public interface Separators {

	/**
	 * 無號分割 (?!^) <BR>
	 * java 8 之前用來避免split("") 造成多一空白字元的Regex
	 */
	String unsign = "(?!^)";

	/** 多符號分割 非數字、字母、底線[^a-zA-Z0-9_] */
	String multiple = "\\W";

	/** 逗號 */
	String comma = ",";

	/** 空白 */
	String space = " ";
	
	/** 減號 */
	String desh = "-";

}
