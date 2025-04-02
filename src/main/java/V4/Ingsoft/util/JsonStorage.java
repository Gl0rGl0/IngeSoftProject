package V4.Ingsoft.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import V4.Ingsoft.view.ViewSE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class JsonStorage {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static String BASE_PATH = "data/";

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        if (!new File(BASE_PATH).exists()) {
            BASE_PATH = "/tmp/ingesoft/";
            ViewSE.println("'data/' folder not available. Using /tmp/ for saving.");
            new File(BASE_PATH).mkdirs();
        }
    }

    synchronized public static <T> ArrayList<T> loadList(String fileName, Class<T> clazz) {
        File file = new File(BASE_PATH + fileName + ".json");
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(file,
                    objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
        } catch (IOException e) {
            AssertionControl.logMessage("Error loading JSON file", 1, "JsonStorage");
            ViewSE.println("Error loading JSON file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    synchronized public static <T> boolean saveList(String fileName, Collection<T> collection) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(BASE_PATH + fileName + ".json"),
                    collection);
            return true;
        } catch (IOException e) {
            AssertionControl.logMessage("Error saving JSON file", 1, "JsonStorage");
            ViewSE.println("Error saving JSON file: " + e.getMessage());
            return false;
        }
    }

    synchronized public static <T> boolean clearList(String fileName) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(BASE_PATH + fileName + ".json"), new ArrayList<T>());
            return true;
        } catch (IOException e) {
            AssertionControl.logMessage("Error clearing JSON file", 1, "JsonStorage");
            ViewSE.println("Error clearing JSON file: " + e.getMessage());
            return false;
        }
    }
}
