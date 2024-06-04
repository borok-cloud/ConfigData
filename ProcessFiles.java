import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


public class ProcessFiles {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static CompletableFuture<String> processFile(String filePath, String searchText, int dateColumnIndex, String delimiter) {
        return CompletableFuture.supplyAsync(() -> {
            String earliestLine = null;
            LocalDate earliestDate = null;

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(searchText)) {
                        String[] parts = line.split("\\|");
                        LocalDate date = LocalDate.parse(parts[dateColumnIndex], DATE_FORMATTER);
                        if (earliestDate == null || date.isBefore(earliestDate)) {
                            earliestDate = date;
                            earliestLine = line;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return earliestLine;
        });
    }

    public static void main(String[] args) {
        List<String> filePaths = List.of("file1.txt", "file2.txt", "file3.txt", "file4.txt", "file5.txt", "file6.txt");
        String searchText = "some_text";
        int dateColumnIndex = 0; // adjust this to the actual date column index
        String delimiter = "|";
        String resultFilePath = "result.txt";

        List<CompletableFuture<String>> futures = filePaths.stream()
                .map(filePath -> processFile(filePath, searchText, dateColumnIndex, delimiter))
                .collect(Collectors.toList());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        allOf.thenRun(() -> {
            List<String> results = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(line -> line != null)
                    .collect(Collectors.toList());

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath))) {
                for (String line : results) {
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).join(); // wait for all tasks to complete
    }
}
