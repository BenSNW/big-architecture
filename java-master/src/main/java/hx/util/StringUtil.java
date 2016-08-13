package hx.util;

import java.lang.Character.UnicodeScript;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

	public static String[] splitByComma(String s) {
		return s.replaceAll("\uff0c", ",").split(",");
	}
	
	public static String[] splitBySemicolon(String s) {
		return s.replaceAll("\uff1b", ";").split(";");
	}
	
	public static String[] splitAndTrimByComma(String s) {
//		return Stream.of(splitByComma(s)).map(String::trim).toArray(size -> new String[size]);
		return Stream.of(splitByComma(s)).map(String::trim).toArray(String[]::new);
	}
	
	public static String[] splitAndTrimBySemicolon(String s) {
//		return Stream.of(splitBySemicolon(s)).map(String::trim).toArray(size -> new String[size]);
		return Stream.of(splitBySemicolon(s)).map(String::trim).toArray(String[]::new);
	}
	
	/** remove all whitespaces and then split by comma (in Chinese or English) */
	public static String[] extractByComma(String s) {
		return s.replaceAll("\\s", "").replaceAll("\uff0c", ",").split(",");
	}
	
	/** remove all whitespaces and then split by semicolon (in Chinese or English) */
	public static String[] extractBySemicolon(String s) {
		return s.replaceAll("\\s", "").replaceAll("\uff1b", ";").split(";");
	}
	
	public static boolean isAllPrintableAsc(String s) {
		for (char c : s.toCharArray())
			if ( c < 32 || c > 127)
				return false;
		return true;
	}
	
	public static boolean containsChinese(String s) {
		return s.codePoints().anyMatch(
				codepoint -> UnicodeScript.of(codepoint) == UnicodeScript.HAN);
	}
	
	public static String[] filterBlankArgs(String... args) {
		return Arrays.stream(args).filter(arg -> StringUtils.isNotBlank(arg))
				.toArray(String[]::new);
	}
	
	public static String[] splitByNewLine(String text) {
		return text.split("[\\r?\\n|\\r]+");
	}
	
	public static <T> List<T> parseCsvToObjects(String text, ObjectBuilder<T> objBuilder) {
		if (StringUtils.isBlank(text))
			return new ArrayList<>(0);
		return Arrays.stream(splitByNewLine(text))
				.map(line -> objBuilder.buildObject(splitAndTrimByComma(line)))
				.collect(Collectors.toList());
	}
	
	@FunctionalInterface
	public static interface ObjectBuilder<T> {
		T buildObject(String... args) throws RuntimeException;
	}
	
}
