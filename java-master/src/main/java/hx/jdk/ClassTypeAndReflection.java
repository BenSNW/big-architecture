package hx.jdk;

import java.lang.reflect.Field;

public class ClassTypeAndReflection {

	private int value = 1;
	public Integer objValue;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public static void main(String[] args) {
		
		System.out.println(int.class == Integer.class);
		System.out.println(int.class.equals(Integer.class));
		System.out.println(int.class == Integer.TYPE);
		System.out.println(Integer.class == Integer.TYPE);
		
		try {
			Field field = ClassTypeAndReflection.class.getField("value");	// NoSuchFieldException
			System.out.println(field.getType());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		try {
			Field field = ClassTypeAndReflection.class.getDeclaredField("value");
			System.out.println(field.getType());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		try {
			ClassTypeAndReflection obj = new ClassTypeAndReflection();
			Field field = obj.getClass().getField("objValue");
			int value = 5;
			field.set(obj, value);	// automatically wrappered to Object and unwrappered back to Integer
			field.get(obj);
			System.out.println(obj.objValue);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			ClassTypeAndReflection obj = new ClassTypeAndReflection();
			Field field = obj.getClass().getField("objValue");
			int value = 5;
			field.setInt(obj, value);	// Can not set java.lang.Integer field hx.jdk.ClassTypeAndReflection.objValue to (int)5
			field.get(obj);
			System.out.println(obj.objValue);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		try {
			ClassTypeAndReflection obj = new ClassTypeAndReflection();
			Field field = obj.getClass().getField("objValue");
			field.getInt(obj);	// illegal conversion, only widening conversio is allowed such getLong from int
			System.out.println(obj.objValue);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		try {
			ClassTypeAndReflection obj = new ClassTypeAndReflection();
			Field field = obj.getClass().getDeclaredField("value");
			System.out.println(field.getLong(obj));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
