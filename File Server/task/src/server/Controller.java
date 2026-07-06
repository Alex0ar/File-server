package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.Scanner;

public class Controller {
    private static final String address = "127.0.0.1";
    private static final int port = 23456;
    FileManager fileManager = new FileManager();

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(address))) {
            System.out.println("Server started!");
            //while (true) {
            try (
                    Socket socket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    ) {
                String request = in.readLine();
                System.out.println("Received: " + request);

                out.println("All files were sent!");
                System.out.println("Sent: " + "All files were sent!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //}
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void takeInput(){
        Scanner sc = new Scanner(System.in);
        while(true){
            String[] parts =  sc.nextLine().split(" ");
            switch(parts[0]) {
                case "add" -> fileManager.add(parts[1]);
                case "get" -> fileManager.get(parts[1]);
                case "delete" -> fileManager.delete(parts[1]);
                case "exit" -> { return; }
                default -> throw new IllegalStateException("Unexpected value: " + parts);
            }
        }
    }

}
