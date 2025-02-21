package ingsoft.DB;

import java.io.*;
import java.net.URL;
import java.util.Properties;

public abstract class DBAbstractHelper {
    protected final String basePath;

    public DBAbstractHelper() {
        // Prova a ottenere la cartella "data" dal classpath
        URL dataURL = getClass().getClassLoader().getResource("data");
        if (dataURL != null) {
            // Otteniamo il path e aggiungiamo il separatore finale se necessario
            String path = dataURL.getPath();
            if (!path.endsWith(File.separator)) {
                path += File.separator;
            }
            this.basePath = path;
        } else {
            // Se non viene trovato, utilizziamo un default (o lanci un'eccezione)
            this.basePath = "./data/";
        }
    }

    protected Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        File file = new File(basePath + fileName);
        // Se il file (o la directory) non esistono, li crea
        if (!file.exists()) {
            File directory = file.getParentFile();
            if (directory != null && !directory.exists()) {
                directory.mkdirs();
            }
            file.createNewFile();
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        }
        return properties;
    }

    protected void storeProperties(String fileName, Properties properties) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(basePath + fileName)) {
            properties.store(fos, null);
        }
    }

    public static String securePsw(String user, String psw) {
        return Integer.toHexString(user.hashCode() + psw.hashCode());
    }
}