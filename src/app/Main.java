package app;

import service.LibraryService;
import storage.DataStore;

/**
 * Main Application Entry Point with JVM shutdown hook.
 */
public class Main {
    private static final String DB = "nexlib-data.ser";

    public static void main(String[] args) {
        DataStore store = new DataStore(DB);
        LibraryService svc = store.load();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> store.save(svc)));

        LibraryApp app = new LibraryApp(svc, store);
        app.start();
    }
}
