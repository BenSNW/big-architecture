package hx.util;

import com.google.common.collect.ImmutableMap;
import org.openjdk.jol.vm.VM;

import java.io.*;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * @see http://www.importnew.com/19172.html
 * @see https://my.oschina.net/xianggao/blog/361584
 * @see http://openjdk.java.net/projects/code-tools/jol/
 * @see https://github.com/twitter/commons/blob/master/src/java/com/twitter/common/objectsize/ObjectSizeCalculator.java
 * @see http://stackoverflow.com/questions/52353/in-java-what-is-the-best-way-to-determine-the-size-of-an-object
 * @see http://stackoverflow.com/questions/9368764/calculate-size-of-object-in-java
 *
 * Created by Benchun on 1/15/17
 */
public class ObjectProfiler {

    public static void main(String[] args) {

        Stream.of(A.class, B.class, C.class, D.class).forEach( clazz -> {

            try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
            ) {
                try {
                    Object object = clazz.newInstance();
                    oos.writeObject(object);
                    System.out.println(bos.size());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Stream.of(new D("112233"), new D("aewrevd")).forEach(obj -> {
            try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
            ) {
                oos.writeObject(obj);
                System.out.println(bos.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Stream.of(new B(123), new C(1234), new Integer(1234)).forEach(obj -> {
            try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
            ) {
                oos.writeObject(obj);
                System.out.println(bos.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Stream.of(new HashMap<>(), new HashMap<>(0), ImmutableMap.of("i", 100),
                ImmutableMap.of("s", "sso"), ImmutableMap.of("i", 100, "s", "sos")).forEach(obj -> {

            try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
            ) {
                oos.writeObject(obj);
                System.out.println(bos.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        int int1 = 2, int2 = 8;
        System.out.println(VM.current().addressOf(int1));   // Stack Higher Address
        System.out.println(VM.current().addressOf(int2));   // Stack Lower Address
        System.out.println(VM.current().addressOf(args));   // Unknown...
        System.out.println(VM.current().addressOf(new Object()));   // HEAP
        System.out.println(VM.current().addressOf("SA"));   // String Constant Pool
        System.out.println(VM.current().addressOf("SA" + "SB"));    // NEW
        System.out.println(VM.current().addressOf("SA" + "SB"));    // REUSE
        System.out.println(VM.current().addressOf("SA".intern()));              // REUSE
        System.out.println(VM.current().addressOf("SA".intern() + "SB"));       // NEW
        System.out.println(VM.current().addressOf(new String("SA")));           // NEW
        System.out.println(VM.current().addressOf(new String("SA")));           // NEW
        System.out.println(VM.current().addressOf(new String("SA").intern()));  // REUSE

        System.out.println(VM.current().addressOf("2"));                        // NEW
        System.out.println(VM.current().addressOf(int1 + ""));                  // NEW
        System.out.println(VM.current().addressOf((int1 + "").intern()));       // REUSE
    }

}

class A implements Serializable {}

class B implements Serializable { int a; B(){} B(int x){a = x;} }

class C implements Serializable { Integer a; C(){} C(int x){a = x;} }

class D implements Serializable { String a; D(){} D(String x){a = x;} }