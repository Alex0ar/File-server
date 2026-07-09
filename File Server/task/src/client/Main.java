package client;

import java.io.*;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    private static final String address = "127.0.0.1";
    private static final int port = 23456;
    //private static final Path DATA_DIRECTORY = Paths.get(System.getProperty("user.dir"), "File Server", "task", "src", "client", "data");
    //For Hyperskill
    private static final Path DATA_DIRECTORY = Paths.get("src", "client", "data");


//    private enum FileSelectionMethod {
//        BY_ID,
//        BY_NAME
//    }

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(address, port);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream());
                Scanner userInput = new Scanner(System.in);
                ) {
            System.out.print("Enter action (1 - get a file, 2 - create a file, 3 - delete a file): ");
            String action = userInput.nextLine();
            if (action.equals("exit")) {
                out.writeUTF(action);
                System.out.println("The request was sent.");
                return;
            }

            String request = null;
            switch (action) {
                case "1" -> { //GET
                    System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");

                    // message to server structure: "GET" | "N" (if BY_NAME) | name length | name
                    //                           OR "GET" | "I" (if BY_ID) | id
                    out.writeUTF("GET");
                    switch (userInput.nextLine()) {
                        case "1" -> {
                            System.out.print("Enter name: ");
                            String name = userInput.nextLine();
                            out.writeUTF("BY_NAME");
                            out.writeUTF(name);
                        }
                        case "2" -> {
                            System.out.print("Enter id: ");
                            long id = Long.parseLong(userInput.nextLine());
                            out.writeUTF("BY_ID");
                            out.writeLong(id);
                        }
                    }

                    System.out.println("The request was sent.");

                    // response from server structure: STATUS_CODE | FILE_LENGTH | FILE IN BYTES

                    int STATUS_CODE = in.readInt();
                    System.out.println(STATUS_CODE);
                    if  (STATUS_CODE == 200) {
                        int fileContentLength = in.readInt();
                        byte[] fileContent = new byte[fileContentLength];
                        in.readFully(fileContent, 0, fileContentLength);
                        System.out.print("The file was downloaded! Specify a name for it: ");
                        String fileNameToCreate = userInput.nextLine();
                        Files.write(DATA_DIRECTORY.resolve(fileNameToCreate),  fileContent);
                        System.out.println("File saved on the hard drive!");
                    } else {
                        System.out.println("The response says that this file is not found!");
                    }

                }
                case "2" -> { //PUT
                    System.out.print("Enter name of the file: ");
                    String fileNameToSend = userInput.nextLine();
                    System.out.print("Enter name of the file to be saved on server: ");
                    String fileNameToSave = userInput.nextLine();
                    if (!Files.exists(DATA_DIRECTORY.resolve(fileNameToSend))) {
                        System.out.println("The file doesn't exist!");
                        return;
                    }
                    byte[] fileContent = Files.readAllBytes(DATA_DIRECTORY.resolve(fileNameToSend));
                    out.writeUTF("PUT");
                    out.writeUTF(fileNameToSend);
                    if (fileNameToSave.length() > 0) {
                        out.writeInt(1);
                        out.writeUTF(fileNameToSave);
                    } else {
                        out.writeInt(0);
                    }
                    out.writeInt(fileContent.length);
                    out.write(fileContent);
                    System.out.println("The request was sent.");
                    int STATUS_CODE = in.readInt();
                    if (STATUS_CODE == 200) {
                        long fileId = in.readLong();
                        System.out.println("Response says that file is saved! ID = " + fileId);
                    } else {
                        System.out.println("The response says that creating the file was forbidden!");
                    }
                }
                case "3" -> { //DELETE
                    System.out.print("Do you want to delete the file by name or by id (1 - name, 2 - id):");
                    out.writeUTF("DELETE");
                    switch (userInput.nextLine()) {
                        case "1" -> {
                            System.out.print("Enter name: ");
                            String name = userInput.nextLine();
                            out.writeUTF("BY_NAME");
                            out.writeUTF(name);
                        }
                        case "2" -> {
                            System.out.print("Enter id: ");
                            long id = Long.parseLong(userInput.nextLine());
                            out.writeUTF("BY_ID");
                            out.writeLong(id);
                        }
                    }
                    System.out.println("The request was sent.");

                    int STATUS_CODE = in.readInt();
                    if (STATUS_CODE == 200) {
                        System.out.println("The response says that this file was deleted successfully!");
                    } else {
                        System.out.println("The response says that this file is not found!");
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
