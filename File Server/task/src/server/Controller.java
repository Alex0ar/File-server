package server;

import com.google.gson.Gson;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Controller {
    private static final String address = "127.0.0.1";
    private static final int port = 23456;
    //private static final Path DATA_DIRECTORY = Paths.get("File Server", "task", "src", "server", "data");
    //For Hyperskill
    private static final Path DATA_DIRECTORY = Paths.get("src", "server", "data");


    private Map<Long, String> filesMap = new HashMap<>();

    FileManager fileManager = new FileManager();

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(address))) {
            System.out.println("Server started!");
            filesMap = IdMapPersistance.loadMap();

            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        ) {
                    String action = in.readUTF();
                    if (action.equals("exit")) {
                        IdMapPersistance.saveMap(filesMap);
                        break;
                    }

                    switch (action) {
                        case "GET" -> {
                            byte[] fileContent = null;
                            String fileName = null;
                            String identifierType = in.readUTF();
                            //System.out.println(identifierType);
                            switch (identifierType) {
                                case "BY_NAME" -> fileName = in.readUTF();
                                case "BY_ID" -> {
                                    long id = in.readLong();
                                    System.out.println("id in server: " + id);
                                    if (filesMap.containsKey(id))
                                        fileName = filesMap.get(id);
                                    else
                                        System.out.println("id not found");
                                }
                            }
                            if (fileName != null && Files.exists(DATA_DIRECTORY.resolve(fileName))) {
                                fileContent = Files.readAllBytes(DATA_DIRECTORY.resolve(fileName));
                                out.writeInt(200);
                                out.writeInt(fileContent.length);
                                out.write(fileContent);
                            } else {
                                out.writeInt(404);
                            }
                        }
                        case "PUT" -> {
                            String initialFileName = in.readUTF();
                            int nameToSaveExist = in.readInt();
                            String fileName = null;
                            long id;
                            Random random = new Random();
                            while (true) {
                                id = random.nextInt(Integer.MAX_VALUE);

                                if (!filesMap.containsKey(id))
                                    break;
                            }
                            switch (nameToSaveExist) {
                                case 0 -> {
                                    String[] initialNameArr = initialFileName.split("\\.");
                                    fileName = String.valueOf(id) + '.' + initialNameArr[initialNameArr.length - 1];
                                }
                                case 1 -> fileName = in.readUTF();
                            }
                            byte[] fileContent = new byte[in.readInt()];
                            in.readFully(fileContent);
                            Path path = DATA_DIRECTORY.resolve(fileName);
                            if (Files.exists(path)) {
                                out.writeInt(403);
                            } else {
                                filesMap.put(id, fileName);
                                Files.write(path, fileContent);
                                out.writeInt(200);
                                out.writeLong(id);
                            }
                        }
                        case "DELETE" -> {
                            String fileName = null;
                            String identifierType = in.readUTF();
                            switch (identifierType) {
                                case "BY_NAME" -> fileName = in.readUTF();
                                case "BY_ID" -> {
                                    long id = in.readLong();
                                    if (filesMap.containsKey(id))
                                        fileName = filesMap.get(id);
                                }
                            }
                            if  (fileName != null && Files.exists(DATA_DIRECTORY.resolve(fileName))) {
                                Files.delete(DATA_DIRECTORY.resolve(fileName));
                                String finalFileName = fileName;
                                filesMap.entrySet().stream()
                                                .filter(entry -> entry.getValue().equals(finalFileName))
                                                        .map(Map.Entry::getKey)
                                                                .findFirst()
                                                                        .ifPresent(filesMap::remove);
                                out.writeInt(200);
                            } else {
                                out.writeInt(404);
                            }
                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
