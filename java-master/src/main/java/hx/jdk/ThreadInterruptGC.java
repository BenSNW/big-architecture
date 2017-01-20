package hx.jdk;

import org.openjdk.jol.vm.VM;

/**
 * VM: -XX:NewSize=2M -XX:MaxNewSize=2M (or -Xmn2M)
 *
 * Created by Benchun on 1/15/17
 */
public class ThreadInterruptGC {

    public static void main(String[] args) throws InterruptedException {

        System.out.println(VM.current().addressOf(args));

        // GC will not interrupt thread sleep
        Thread thread = new Thread(() -> {
            try {
                System.out.println("before sleep " + System.currentTimeMillis());
                Thread.sleep(60000);
                System.out.println("after sleep " + System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        byte[] bytes = new byte[2028 * 1024];
        System.out.println(VM.current().addressOf(bytes));

        System.gc();    // will trigger a full GC, if not called, only minor GC

        if (! thread.isInterrupted())
            thread.interrupt();

        Thread.sleep(60000);

    }
}
