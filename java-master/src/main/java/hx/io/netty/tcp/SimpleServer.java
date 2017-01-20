package hx.io.netty.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.stream.IntStream;

/**
 * Created by Benchun on 1/19/17
 */

public class SimpleServer {

    ServerSocket server;
    int serverPort = 8888;

    // Constructor to allocate a ServerSocket listening at the given port.
    public SimpleServer() {
        try {
            server = new ServerSocket(serverPort);
            System.out.println("ServerSocket: " + server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Start listening.
    private void listen() {
        while (true) { // run until you terminate the program
            try {
                // Wait for connection. Block until a connection is made.
                Socket socket = server.accept();
                System.out.println("Socket: " + socket);
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                int byteRead;
                // Block until the client closes the connection (i.e., read() returns -1)
                while ((byteRead = in.read()) != -1) {
                    System.out.print((char)byteRead + "[" + byteRead + "]");
                    out.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(byteRead).array());
//                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
//        new SimpleServer().listen();  // Start the server and listening

        IntStream.rangeClosed(1, 100).forEach(i -> {
            try {
                Socket socket = new Socket("127.0.0.1", 8888);
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream();
                out.write(ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putInt(i).putInt(i).array());
                out.flush();
                int byteRead;
                // Block until the client closes the connection (i.e., read() returns -1)
                while ((byteRead = in.read()) != -1) {
                    System.out.print((char)byteRead + "[" + byteRead + "]");
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

}
