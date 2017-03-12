package hx.spark.util;

/**
 * Java does not implement Generics the same way it is done in, say, C# : you cannot retrieve type information of a generic type at runtime, because it's simply not saved after compilation (see: type erasure)
 * This means that if I declare a variable of type List<MyObject>, then I cannot know if it is a list of MyObject, because the type information is lost, and the class of this list is just List, which is not implementing ParameterizedType.
 *
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
public class RttiUtil {

}
