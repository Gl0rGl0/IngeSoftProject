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

    /**
     * Loads a single object from a JSON file.
     *
     * @param <T>      the type of the object
     * @param filePath the path to the JSON file (relative to BASE_PATH, without .json extension)
     * @param clazz    the class of the object
     * @return the loaded object, or null if the file doesn't exist or an error occurs.
     */
    synchronized public static <T> T loadObject(String filePath, Class<T> clazz) {
        File file = new File(BASE_PATH + filePath); // Assuming filePath includes .json
        if (!file.exists()) {
            return null; // File not found
        }

        try {
            return objectMapper.readValue(file, clazz);
        } catch (IOException e) {
            AssertionControl.logMessage("Error loading JSON object file: " + filePath, 1, "JsonStorage");
            ViewSE.println("Error loading JSON object file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Saves a single object to a JSON file.
     *
     * @param <T>      the type of the object
     * @param filePath the path to the JSON file (relative to BASE_PATH, without .json extension)
     * @param object   the object to save
     * @return true if saving was successful, false otherwise.
     */
    synchronized public static <T> boolean saveObject(String filePath, T object) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(BASE_PATH + filePath), object); // Assuming filePath includes .json
            return true;
        } catch (IOException e) {
            AssertionControl.logMessage("Error saving JSON object file: " + filePath, 1, "JsonStorage");
            ViewSE.println("Error saving JSON object file: " + e.getMessage());
            return false;
        }
    }
}
