package hx.spark.util;

import java.math.BigDecimal;
import java.math.RoundingMode;



public class TypeUtil {
	
	/**
	 * mutual type conversion among {@link Integer} {@link Long} {@link Float}
	 * {@link Double} {@link String} and their coresponding primitive types
	 * 
	 * <p>Note the outout type can only be a wrapper type such as Integer.class with this conversion,
	 * besides, the conversion to int or long is a floor operation if it's fractional
	 * 
	 * @throws NumberFormatException if obj cannot be converted correctly,
	 * for example, by converting 3.7s or a very large number to int
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convert(Object obj, Class<T> type) {
		if (obj == null)
			return null;
		if (type == String.class)
			return (T) obj.toString();
		else if (isLong(type))
			return (T) Long.valueOf(castDecimal(obj.toString()));
		else if (isFloat(type))
			return (T) Float.valueOf(obj.toString());
		else if (isDouble(type))
			return (T) Double.valueOf(obj.toString());
		else
			return (T) Integer.valueOf(castDecimal(obj.toString()));
	}
	
	private static final BigDecimal ONE = new BigDecimal(1);

	/**
	 * @param src the object to be converted to Integer 
	 * @param roundingMode as specified by {@link java.math.RoundingMode}
	 * @return
	 */
	public static Integer convertToInt(Object src, int roundingMode) {
		if (src == null)
			return null;
		if (isIntegral(src.getClass()))
			return Integer.valueOf(src.toString());
		return new BigDecimal(src.toString()).divide(
				ONE, RoundingMode.valueOf(roundingMode)).intValue();
	}
	
	private static String castDecimal(String s) {
		Double.valueOf(s); // avoid 3.5s to be treated safe
		int decimalIndex = s.indexOf(".");
		return decimalIndex < 0 ? s : s.substring(0, decimalIndex);
	}
	
	public static boolean isInteger(Class<?> type) {
		return int.class == type || Integer.class == type;
	}
	
	public static boolean isLong(Class<?> type) {
		return long.class == type || Long.class == type;
	}
	
	public static boolean isIntegral(Class<?> type) {
		return isInteger(type) || isLong(type);
	}
	
	public static boolean isFloat(Class<?> type) {
		return float.class == type || Float.class == type;
	}
	
	public static boolean isDouble(Class<?> type) {
		return double.class == type || Double.class == type;
	}
	
	public static boolean isBoolean(Class<?> type) {
		return boolean.class == type || Boolean.class == type;
	}
	
	public static boolean isDecimal(Class<?> type) {
		return isFloat(type) || isDouble(type);
	}
	
	public static boolean isNumber(Class<?> type) {
		return isInteger(type) || isDecimal(type);
	}
	
	/**
	 * Determine whether the given object is an array:
	 * either an Object array or a primitive array.
	 * @param obj the object to check
	 */
	public static boolean isArray(Object obj) {
		return (obj != null && obj.getClass().isArray());
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(
				TypeUtil.class.getMethod("convert", Object.class, Class.class)
					.getReturnType());
			System.out.println(
				TypeUtil.class.getMethod("isArray", Object.class)
					.getReturnType());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		System.out.println(int.class + " " + int.class.getSimpleName());
		System.out.println( ((Object) 3).getClass().getSimpleName());
		
		System.out.println(new BigDecimal(Double.MAX_VALUE).intValue());
		System.out.println(new BigDecimal(Double.MIN_VALUE).intValue());
//		System.out.println(Integer.MAX_VALUE + 1);
		System.out.println(Integer.MAX_VALUE + 1D);
		System.out.println(new BigDecimal(Integer.MAX_VALUE + 1D).toPlainString());
		System.out.println(new BigDecimal(Integer.MAX_VALUE + 1D).intValue());
		
		// java.lang.NumberFormatException
		System.out.println(Integer.valueOf(Integer.MAX_VALUE + 1D + ""));
	}
	
}
