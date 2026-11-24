package storage;

import service.LibraryService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * File handling and data persistence manager utilizing Java Object Serialization.
 * Provides resilient loading, automatic schema recovery, transactional backups,
 * and initial data seeding.
 */
public class DataStore {
    private final String fileName;

    public DataStore(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Loads the serialized LibraryService from disk.
     * Rebuilds transient indices and seeds default data if first-time launch.
     */
    public LibraryService load() {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("[DATASTORE] No existing database found. Initializing fresh library catalog...");
            LibraryService fresh = new LibraryService();
            fresh.seedInitialDataIfEmpty();
            save(fresh);
            return fresh;
        }

        try (ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            Object data = input.readObject();
            if (data instanceof LibraryService service) {
                service.rebuildIndices();
                service.seedInitialDataIfEmpty();
                System.out.println("[DATASTORE] Successfully loaded library records from: " + fileName);
                return service;
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("[DATASTORE WARNING] Error reading serialized database (" + ex.getMessage() + ").");
            File backup = new File(fileName + ".bak");
            if (backup.exists()) {
                System.out.println("[DATASTORE] Attempting recovery from backup database...");
                try (ObjectInputStream backupInput = new ObjectInputStream(new BufferedInputStream(new FileInputStream(backup)))) {
                    Object data = backupInput.readObject();
                    if (data instanceof LibraryService service) {
                        service.rebuildIndices();
                        service.seedInitialDataIfEmpty();
                        System.out.println("[DATASTORE] Recovery successful from: " + fileName + ".bak");
                        return service;
                    }
                } catch (Exception backupEx) {
                    System.err.println("[DATASTORE ERROR] Recovery from backup also failed.");
                }
            }
        }

        LibraryService fallback = new LibraryService();
        fallback.seedInitialDataIfEmpty();
        return fallback;
    }

    /**
     * Persists the current state of LibraryService to disk with automatic atomic backup.
     */
    public synchronized void save(LibraryService library) {
        if (library == null) return;
        File file = new File(fileName);

        // Create backup of current file before overwriting
        if (file.exists()) {
            try {
                File backupFile = new File(fileName + ".bak");
                Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignored) { }
        }

        try (ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            output.writeObject(library);
            output.flush();
        } catch (IOException ex) {
            System.err.println("[DATASTORE ERROR] Critical: Data persistence failed: " + ex.getMessage());
        }
    }
}
