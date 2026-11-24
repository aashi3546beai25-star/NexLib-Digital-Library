package app;

import service.LibraryService;
import storage.DataStore;

/**
 * Application Entry Point for NexLib - Digital College Library & RFID Book Lending System.
 * Initializes storage, restores database state, registers JVM shutdown hooks for resilience,
 * and launches the menu-driven presentation console.
 */
public class Main {
    private static final String DATA_FILE = "nexlib-data.ser";

    public static void main(String[] args) {
        // Initialize persistent datastore
        DataStore store = new DataStore(DATA_FILE);
        LibraryService library = store.load();

        // Register graceful shutdown hook to guarantee data safety on abnormal termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            store.save(library);
        }));

        // Launch presentation controller
        LibraryApp app = new LibraryApp(library, store);
        app.start();
    }
}
