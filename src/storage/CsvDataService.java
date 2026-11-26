package storage;

import exception.LibraryException;
import model.Book;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Character Streams CSV Import & Export Service (Lectures 49-54).
 */
public class CsvDataService {
    private CsvDataService() {}

    public static int exportBooks(File file, List<Book> books) throws IOException {
        int count = 0;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) { // Character Stream (FileWriter)
            bw.write("BookID,Title,Author,Category,Shelf,Available\n");
            for (Book b : books) {
                bw.write(String.format("%s,\"%s\",\"%s\",\"%s\",%s,%b\n",
                        b.getId(), b.getTitle().replace("\"", "\"\""), b.getAuthor().replace("\"", "\"\""),
                        b.getCategory(), b.getShelf(), b.isAvailable()));
                count++;
            }
        }
        return count;
    }

    public static List<Book> importBooks(File file) throws IOException, LibraryException {
        List<Book> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) { // Character Stream (FileReader)
            String line = br.readLine(); // Header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (p.length >= 4) {
                    list.add(new Book(p[0].trim().replace("\"", ""), p[1].trim().replace("\"", ""),
                            p[2].trim().replace("\"", ""), p[3].trim().replace("\"", ""),
                            p.length > 4 ? p[4].trim().replace("\"", "") : "Rack-A1"));
                }
            }
        }
        return list;
    }
}
