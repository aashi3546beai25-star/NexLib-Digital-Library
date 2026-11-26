package service;

/**
 * Inter-thread coordination monitor (Lectures 32-33: wait() & notifyAll()).
 */
public class BookReturnNotifier {
    private final Object lock = new Object();
    private String lastReturnedId;

    public void signalBookReturned(String id) {
        synchronized (lock) {
            this.lastReturnedId = id;
            lock.notifyAll(); // Wake waiting threads
        }
    }

    public boolean waitForBook(String id, long timeout) throws InterruptedException {
        synchronized (lock) {
            long deadline = System.currentTimeMillis() + timeout;
            while (lastReturnedId == null || !lastReturnedId.equalsIgnoreCase(id)) {
                long rem = deadline - System.currentTimeMillis();
                if (rem <= 0) return false;
                lock.wait(rem); // Guarded wait
            }
            return true;
        }
    }
}
