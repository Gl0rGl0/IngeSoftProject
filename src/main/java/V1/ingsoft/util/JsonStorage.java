package V1.ingsoft.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class JsonStorage {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String basePath = "data/";

    public static <T> ArrayList<T> loadList(String fileName, Class<T> clazz) {
        File file = new File(basePath + fileName + ".json");
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(file,
                    objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
        } catch (IOException e) {
            System.out.println("Errore nel caricamento del file JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static <T> boolean saveList(String fileName, Collection<T> collection) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(basePath + fileName + ".json"),
                    collection);
            return true;
        } catch (IOException e) {
            System.out.println("Errore nel salvataggio del file JSON: " + e.getMessage());
            return false;
        }
    }

}
