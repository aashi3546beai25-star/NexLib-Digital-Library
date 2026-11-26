package service;

import model.IssueRecord;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Background Daemon Thread (Lectures 26-29).
 */
public class BackgroundAuditDaemon implements Runnable {
    private final LibraryService service;
    private final Thread thread;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicInteger overdueAlerts = new AtomicInteger(0);
    private final long interval;

    public BackgroundAuditDaemon(LibraryService s, long interval) {
        this.service = s; this.interval = interval;
        this.thread = new Thread(this, "NexLib-AuditDaemon");
        this.thread.setDaemon(true); // Daemon thread exits automatically
        this.thread.setPriority(Thread.NORM_PRIORITY - 1);
    }

    public void start() { if (thread.getState() == Thread.State.NEW) thread.start(); }
    public void stop() {
        running.set(false);
        thread.interrupt();
        try { thread.join(1000); } catch (InterruptedException ignored) {}
    }

    public void scan() {
        if (service == null) return;
        synchronized (service) {
            int count = (int) service.getAllLoans().stream().filter(IssueRecord::isOverdue).count();
            overdueAlerts.set(count);
        }
    }

    @Override
    public void run() {
        while (running.get() && !Thread.currentThread().isInterrupted()) {
            try {
                scan();
                Thread.sleep(interval); // TIMED_WAITING
            } catch (InterruptedException e) { break; }
        }
    }

    public Thread.State getState() { return thread.getState(); }
    public int getOverdueAlerts() { return overdueAlerts.get(); }
    public boolean isDaemon() { return thread.isDaemon(); }
    public String getName() { return thread.getName(); }
}
