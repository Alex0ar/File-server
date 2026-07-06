package server;

import java.util.ArrayList;
import java.util.List;

public class FileManager {
    List<String> files = new ArrayList<>();

    public void add(String fileName) {
        if (files.contains(fileName)) {
            System.out.println("Cannot add the file " + fileName);
            return;
        }
        try{
            int fileNumber;
            if (fileName.length() > 5)
                fileNumber = Integer.parseInt(fileName.substring(4, 6));
            else
                fileNumber = Integer.parseInt(fileName.substring(4, 5));
            if (fileNumber > 10) throw new Exception();
            files.add(fileName);
            System.out.println("The file " + fileName + " added successfully");
        } catch(Exception e){
            System.out.println("Cannot add the file " + fileName);
        }
    }

    public void get(String fileName) {
        if (files.contains(fileName)) {
            System.out.printf("The file %s was sent\n",  fileName);
        } else {
            System.out.printf("The file %s not found\n",   fileName);
        }
    }

    public void delete(String fileName) {
        if (files.contains(fileName)) {
            files.remove(fileName);
            System.out.printf("The file %s was deleted\n",  fileName);
        } else  {
            System.out.printf("The file %s not found\n",   fileName);
        }
    }
}
