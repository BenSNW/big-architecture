package hx.util;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;

import java.lang.Character.UnicodeScript;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CaseFormat;

public class StringUtil {

	public static String camelToUnderscore(String camel) {
//    	CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camel);

//    	Chars.asList(camel.toCharArray()).stream().skip(0);
    	return IntStream.range(1, camel.length())
    		.mapToObj( i -> upToLow(camel, i, "_"))
    		.collect(Collectors.joining("", upToLow(camel, 0, ""), ""));
//    	StringBuilder sb = new StringBuilder(camel.length() + 3);
//    	sb.append(Character.toLowerCase(camel.charAt(0)));
//    	IntStream.range(1, camel.length()).mapToObj(camel::charAt)
//    		.forEach(c -> sb.append(
//    			Character.isUpperCase(c) ? "_" + Character.toLowerCase(c) : c));
//    	return sb.toString();
    }
	
	private static String upToLow(String s, int i, String prefix) {
		return isUpperCase(s.charAt(i)) ? prefix + toLowerCase(s.charAt(i)) : "" + s.charAt(i);
	}
	
	public static String underScoreToCamel(String underscore) {
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, underscore);
	}
	
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
	public interface ObjectBuilder<T> {
		T buildObject(String... args) throws RuntimeException;
	}
	
}
