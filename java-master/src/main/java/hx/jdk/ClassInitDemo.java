package hx.jdk;

import java.util.Arrays;

public class ClassInitDemo {

	static {
		System.out.println("static block");
	}
	
	public ClassInitDemo() {
		System.out.println("ctor");
	}
	
	public static void main(String[] args) {
		System.out.println("main");
		Arrays.asList(SomeEnum.values()).stream();
		System.out.println(SomeEnum.valueOf("ONE"));
		System.out.println(SomeEnum.valueOf(SomeEnum.class, "TWO"));
	}
}

enum SomeEnum {
	
	/** 
	 * enum instances must be placed in the first and they are all initialized
	 * when the class is loaded, thus they have no access to static fields
	 * because at that time static fields are not yet initialzed correctly
	 */
	ONE(1), TWO(2);
	
	static {
		System.out.println("enum static block");
	}
	
	private int number;
	private static int max = 10;
	
	SomeEnum(int n) {
//		this.number = n > max ? max : n;
		this.number = n;
		System.out.println("enum ctor");
	}
	
	@Override
	public String toString() {
		return number + "-" + max;
	}
	
}
