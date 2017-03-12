package hx.jdk;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Java does not implement Generics the same way it is done in, say, C# : you cannot retrieve type information of a generic type at runtime, because it's simply not saved after compilation (see: type erasure)
 * This means that if I declare a variable of type List<MyObject>, then I cannot know if it is a list of MyObject, because the type information is lost, and the class of this list is just List, which is not implementing ParameterizedType.
 * <p>The only time where the generic type information is saved, is when it is known at compile time for a type definition:
 * <li><em>public class MyList extends List<MyObject> {}</em></li>
 * <p><p>Here, MyList.class.getGenericSuperclass() should be implementing ParameterizedType.
 * 
 * <ul>
 * @see <li>http://stackoverflow.com/questions/24829146/classcastexceptionjava-lang-class-cannot-be-cast-to-java-lang-reflect-parameter</li>
 * @see <li>https://docs.oracle.com/javase/tutorial/java/generics/bridgeMethods.html</li>
 * </ul>
 * 
 * <p>Created by BenSNW on Sep 20, 2016
 *
 */
public class RealtimeTypeInfo {
	
	static class Node<T> {
		T data;
		void setData(T data) {
			this.data = data;
		}
	}
	
	static class IntegerNode extends Node<Integer> {
		
		@SuppressWarnings("unchecked")
		static void testTypeErasure() {
			IntegerNode iNode = new IntegerNode();
			iNode.setData(123);
			
			@SuppressWarnings("rawtypes")
			Node node = iNode;	// now they refer to the same object but different behaviors are allowed
			System.out.println(iNode + " " + node);
			
			node.setData("sadsfdf");	// now data is Integer as to its static type, but its actually a String
			System.out.println(node.data + " " + iNode.data);
			// call data.getClass() will throw ClassCastException: String cannot be cast to Integer
			System.out.println(node.data.getClass() + " " + iNode.data.getClass());
			
		}
	}

	private static <T, R> Function<T, R> hackLambda(Function<T, R> lm) {
		return new Function<T, R>() {
			@Override
			public R apply(T t) {
				return lm.apply(t);
			}			
		};
	}
	
	private static Function<Integer, Integer> hackLambda2(Function<Integer, Integer> lm) {
		return new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				return lm.apply(t);
			}			
		};
	}
	
	// http://stackoverflow.com/questions/24829146/classcastexceptionjava-lang-class-cannot-be-cast-to-java-lang-reflect-parameter
	public static void main(String[] args) {
		
		try {
			IntegerNode.testTypeErasure();
		} catch (ClassCastException ex) {
			ex.printStackTrace();
		}
		
		List<?> list = new ArrayList<>();
		System.out.println(list.getClass());
		System.out.println(list.getClass().getGenericInterfaces()[0]);
		ParameterizedType pt = (ParameterizedType)list.getClass().getGenericInterfaces()[0];
		System.out.println(pt.getClass());
		System.out.println(pt.getActualTypeArguments()[0].getTypeName());
		
		ArrayList<?> arrayList = new ArrayList<>();
		System.out.println(arrayList.getClass().getGenericInterfaces()[0] + "\n");
//		pt = (ParameterizedType) arrayList.getClass(); // cannot compile
		
		Function<Integer, Integer> f = new Function<Integer, Integer>() {		
			@Override
			public Integer apply(Integer t) {
				return Math.abs(t);
			}
		};
		// java.util.function.Function<java.lang.Integer, java.lang.Integer>
		System.out.println(f.getClass().getGenericInterfaces()[0]);
		pt = (ParameterizedType)f.getClass().getGenericInterfaces()[0];
		System.out.println(pt.getClass());
		System.out.println(pt.getActualTypeArguments()[0].getTypeName() + "\n");
		
		f = RealtimeTypeInfo.hackLambda(Math::abs);
		// java.util.function.Function<T, R>
		System.out.println(f.getClass().getGenericInterfaces()[0]);
		pt = (ParameterizedType)f.getClass().getGenericInterfaces()[0];
		System.out.println(pt.getClass());
		System.out.println(pt.getActualTypeArguments()[0].getTypeName());
		
		f = RealtimeTypeInfo.<Integer,Integer>hackLambda(Math::abs);
		// java.util.function.Function<T, R>
		System.out.println(f.getClass().getGenericInterfaces()[0]);
		pt = (ParameterizedType)f.getClass().getGenericInterfaces()[0];
		System.out.println(pt.getClass());
		System.out.println(pt.getActualTypeArguments()[0].getTypeName() + "\n");
		
		f = RealtimeTypeInfo.hackLambda2(Math::abs);
		// java.util.function.Function<java.lang.Integer, java.lang.Integer>
		System.out.println(f.getClass().getGenericInterfaces()[0]);
		pt = (ParameterizedType)f.getClass().getGenericInterfaces()[0];
		System.out.println(pt.getClass());
		System.out.println(pt.getActualTypeArguments()[0].getTypeName());
		
		f = Math::abs;
		// java.util.function.Function -> not ParameterizedType anymore !!
		System.out.println(f.getClass().getGenericInterfaces()[0]);
		try{
			pt = (ParameterizedType)f.getClass().getGenericInterfaces()[0];
		} catch (ClassCastException ex) {
			ex.printStackTrace();
		}
		
		System.err.println("\nImpossible to infer generic type with lambda");
		
	}
}
