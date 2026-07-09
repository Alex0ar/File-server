package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class IdMapPersistance {
    private static final Gson gson = new Gson();
    //private static final Path IDENTIFIERS_FILE = Paths.get("File Server", "task", "src", "server", "identifiers.json");
    private static final Path IDENTIFIERS_FILE = Paths.get("src", "server", "identifiers.json");

    public static void saveMap(Map<Long, String> map) {
        String json = gson.toJson(map);
        try {
            Files.writeString(IDENTIFIERS_FILE, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<Long, String> loadMap() {
        if(!Files.exists(IDENTIFIERS_FILE)) {
            return new HashMap<>();
        }
        try {
            String json = Files.readString(IDENTIFIERS_FILE, StandardCharsets.UTF_8);
            Type mapType =  new TypeToken<Map<Long, String>>() {}.getType();
            Map<Long, String> map = gson.fromJson(json, mapType);
            return map != null ? map : new HashMap<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
