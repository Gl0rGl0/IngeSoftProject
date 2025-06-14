package V5.Ingsoft.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import V5.Ingsoft.controller.item.interfaces.StorageManager; // Assicurati che l'import sia corretto

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// JsonStorage ora implementa StorageManager
public final class JsonStorage<T> implements StorageManager<T> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static String BASE_PATH = "data/"; // Questo dovrebbe essere forse un membro non statico se l'istanza non è singleton

    private String path;
    private Class<T> clazz;

    public JsonStorage(String path, Class<T> clazz){
        this.path = path;
        this.clazz = clazz;
    }

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        if (!new File(BASE_PATH).exists()) {
            new File(BASE_PATH).mkdirs();
        }
    }

    // Per implementare l'interfaccia, i metodi non devono essere statici.
    // Se vuoi mantenere JsonStorage con solo metodi statici (come una utility class),
    // allora l'interfaccia StorageManager dovrebbe essere usata per un'altra classe che la implementa
    // e incapsula JsonStorage o ha un suo metodo statico getIstance().

    // PER UN VERO DECOUPLING, RENDE I METODI NON STATICI:
    @Override
    synchronized public List<T> loadList() {
        File file = new File(BASE_PATH + path + ".json");
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(file,
                    objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
        } catch (IOException e) {
            System.out.println("Error loading JSON file: " + e.getMessage());
            // Considera di lanciare un'eccezione specifica per la persistenza o usare un Payload di ritorno.
            // AssertionControl.logMessage("Error loading JSON file", Payload.Status.ERROR, "JsonStorage");
            return new ArrayList<>();
        }
    }

    @Override
    synchronized public boolean saveList(Collection<T> collection) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(BASE_PATH + path + ".json"),
                    collection);
            return true;
        } catch (IOException e) {
            // AssertionControl.logMessage("Error saving JSON file", Payload.Status.ERROR, "JsonStorage");
            System.out.println("Error saving JSON file: " + e.getMessage());
            return false;
        }
    }

    @Override
    synchronized public void clearList() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(BASE_PATH + path + ".json"), new ArrayList<T>());
        } catch (IOException e) {
            // AssertionControl.logMessage("Error clearing JSON file", Payload.Status.ERROR, "JsonStorage");
            System.out.println("Error clearing JSON file: " + e.getMessage());
        }
    }

    @Override
    synchronized public T loadObject() {
        File file = new File(BASE_PATH + path); // Assuming filePath includes .json
        if (!file.exists()) {
            return null; // File not found
        }

        try {
            return objectMapper.readValue(file, clazz);
        } catch (IOException e) {
            // AssertionControl.logMessage("Error loading JSON object file: " + filePath, Payload.Status.ERROR, "JsonStorage");
            System.out.println("Error loading JSON object file: " + e.getMessage());
            return null;
        }
    }

    @Override
    synchronized public boolean saveObject(T object) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(BASE_PATH + path), object); // Assuming filePath includes .json
            return true;
        } catch (IOException e) {
            // AssertionControl.logMessage("Error saving JSON object file: " + filePath, Payload.Status.ERROR, "JsonStorage");
            System.out.println("Error saving JSON object file: " + e.getMessage());
            return false;
        }
    }
}