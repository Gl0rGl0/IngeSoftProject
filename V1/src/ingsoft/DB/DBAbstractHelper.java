package ingsoft.DB;

import java.io.*;
import java.util.Properties;

public abstract class DBAbstractHelper {
    protected final String basePath = "./V1/data/";

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