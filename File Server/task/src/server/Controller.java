package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Controller {
    private static final String address = "127.0.0.1";
    private static final int port = 23456;
    //private static final Path DATA_DIRECTORY = Paths.get("File Server", "task", "src", "server", "data");
    private static final Path DATA_DIRECTORY = Paths.get("src", "server", "data");

    FileManager fileManager = new FileManager();

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(address))) {
            System.out.println("Server started!");
            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        ) {
                    String request = in.readLine();
                    if (request.equals("exit")) {
                        break;
                    }
                    String response = "";

                    // TO DELETE
                    //System.out.println("request: " + request);

                    String[] requestArray = request.split(" ", 3);

                    // TO DELETE
//                    for (int i = 0; i < requestArray.length; i++) {
//                        System.out.print(requestArray[i] + " | 2");
//                    }

                    switch (requestArray[0]) {
                        case "GET" -> {
                            Path filePath = DATA_DIRECTORY.resolve(requestArray[1]);
                            if (Files.exists(filePath)) {
                                response = "200 " + Files.readString(filePath);
                            } else {
                                response = "404";
                            }
                        }
                        case "PUT" -> {
                            Path filePath = DATA_DIRECTORY.resolve(requestArray[1]);
                            if (Files.exists(filePath)) {
                                response = "403";
                            } else {
                                response = "200";
                                Files.writeString(filePath, requestArray[2]);
                            }
                        }
                        case "DELETE" -> {
                            Path filePath = DATA_DIRECTORY.resolve(requestArray[1]);
                            if (Files.exists(filePath)) {
                                Files.delete(filePath);
                                response = "200";
                            } else {
                                response = "404";
                            }
                        }
                    }
                    out.println(response);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
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
