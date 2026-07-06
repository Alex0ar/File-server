package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
    private static final String address = "127.0.0.1";
    private static final int port = 23456;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(address, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                ) {
            System.out.println("Client started!");
            out.println("Give me everything you have!");
            System.out.println("Sent: Give me everything you have!");

            String received = in.readLine();
            System.out.println("Received: " + received);

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
