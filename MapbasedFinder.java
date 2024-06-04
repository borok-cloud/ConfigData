import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EarliestDateFinderPerString {

    public static void main(String[] args) {
        // Define the set of search strings
        Set<String> searchStrings = new HashSet<>();
        searchStrings.add("example1");
        searchStrings.add("example2");
        // Add more strings as needed

        // Define the file path
        String fileName = "path/to/your/file.txt";

        try {
            // Find the earliest date for each search string
            Map<String, String> result = findEarliestDateForStrings(fileName, searchStrings);
            result.forEach((searchString, line) -> {
                System.out.println("Earliest line for " + searchString + ": " + line);
            });
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds the earliest date line for each search string.
     * @param fileName The file to search in.
     * @param searchStrings The set of strings to search for.
     * @return A map of search strings to their earliest date lines.
     * @throws IOException If an I/O error occurs.
     * @throws ParseException If a date parsing error occurs.
     */
    public static Map<String, String> findEarliestDateForStrings(String fileName, Set<String> searchStrings) throws IOException, ParseException {
        // Define the date format used in the file
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Adjust format as per your date format in the file
        Map<String, Date> earliestDates = new HashMap<>();
        Map<String, String> earliestDateLines = new HashMap<>();

        // Open the file for reading
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            // Read each line of the file
            while ((line = br.readLine()) != null) {
                for (String searchString : searchStrings) {
                    if (line.contains(searchString)) {
                        // Split the line by the '|' delimiter
                        String[] parts = line.split("\\|");
                        if (parts.length > 0) {
                            // Parse the date from the first part of the line
                            Date lineDate = dateFormat.parse(parts[0].trim());
                            // Check if this is the earliest date found for this search string
                            if (!earliestDates.containsKey(searchString) || lineDate.before(earliestDates.get(searchString))) {
                                earliestDates.put(searchString, lineDate);
                                earliestDateLines.put(searchString, line);
                            }
                        }
                    }
                }
            }
        }

        // Return the map of search strings to their earliest date lines
        return earliestDateLines;
    }
}
