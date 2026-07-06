package server;

import java.util.Scanner;

public class Controller {
    FileManager fileManager = new FileManager();

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
