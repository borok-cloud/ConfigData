import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.stream.Stream;

public class ProcessFilesWithNIO {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static CompletableFuture<String> processFile(Path filePath, String searchText, int dateColumnIndex, String delimiter) {
        return CompletableFuture.supplyAsync(() -> {
            String earliestLine = null;
            LocalDate earliestDate = null;

            try (Stream<String> lines = Files.lines(filePath)) {
                for (String line : (Iterable<String>) lines::iterator) {
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
        List<Path> filePaths = List.of(
                Paths.get("file1.txt"),
                Paths.get("file2.txt"),
                Paths.get("file3.txt"),
                Paths.get("file4.txt"),
                Paths.get("file5.txt"),
                Paths.get("file6.txt")
        );
        String searchText = "some_text";
        int dateColumnIndex = 0; // adjust this to the actual date column index
        String delimiter = "|";
        Path resultFilePath = Paths.get("result.txt");

        List<CompletableFuture<String>> futures = filePaths.stream()
                .map(filePath -> processFile(filePath, searchText, dateColumnIndex, delimiter))
                .collect(Collectors.toList());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        allOf.thenRun(() -> {
            List<String> results = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(line -> line != null)
                    .collect(Collectors.toList());

            try {
                Files.write(resultFilePath, results);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).join(); // wait for all tasks to complete
    }
}
