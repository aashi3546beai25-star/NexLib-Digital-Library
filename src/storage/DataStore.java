package storage;

import service.LibraryService;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Byte Streams Persistence Manager using Java Object Serialization.
 */
public class DataStore {
    private final String fileName;
    public DataStore(String fileName) { this.fileName = fileName; }

    public LibraryService load() {
        File file = new File(fileName);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                LibraryService svc = (LibraryService) in.readObject();
                svc.init();
                return svc;
            } catch (Exception ignored) {}
        }
        LibraryService fresh = new LibraryService();
        fresh.seedInitialData();
        save(fresh);
        return fresh;
    }

    public synchronized void save(LibraryService svc) {
        File file = new File(fileName);
        try {
            if (file.exists()) Files.copy(file.toPath(), new File(fileName + ".bak").toPath(), StandardCopyOption.REPLACE_EXISTING);
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(svc);
            }
        } catch (IOException e) {
            System.err.println("DATASTORE ERROR: Persistence failed: " + e.getMessage());
        }
    }
}
