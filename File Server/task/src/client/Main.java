package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    private static final String address = "127.0.0.1";
    private static final int port = 23456;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(address, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner userInput = new Scanner(System.in);
                ) {
            System.out.print("Enter action (1 - get a file, 2 - create a file, 3 - delete a file): ");
            String action = userInput.nextLine();
            if (action.equals("exit")) {
                out.println(action);
                System.out.println("The request was sent.");
                return;
            }
            System.out.print("Enter filename: ");
            String filename = userInput.nextLine();
            String fileContent = null;
            if (action.equals("2")) {
                System.out.print("Enter file content: ");
                fileContent = userInput.nextLine();
            }
            System.out.println("The request was sent.");

            String request = null;
            switch (action) {
                case "1" -> request = "GET " + filename;
                case "2" -> request = "PUT " + filename + " " + fileContent;
                case "3" -> request = "DELETE " + filename;
            }

            //System.out.println("request: " + request);

            out.println(request);

            String response = in.readLine();
            System.out.println("[client] response from server: " + response);
            switch (action) {
                case "1" -> {
                    String[] responseArray = response.split(" ", 2);
                    if (responseArray[0].equals("200")) {
                        System.out.println("The content of the file is: " + responseArray[1]);
                    } else {
                        System.out.println("The response says that the file was not found!");
                    }
                }
                case "2" -> {
                    if (response.equals("200")) {
                        System.out.println("The response says that the file was created!");
                    } else {
                        System.out.println("The response says that creating the file was forbidden!");
                    }
                }
                case "3" -> {
                    if (response.equals("200")) {
                        System.out.println("The response says that the file was successfully deleted!");
                    } else {
                        System.out.println("The response says that the file was not found!");
                    }
                }
            }

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
