import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarliestDateFinder {

    public static void main(String[] args) {
        // Define the list of search strings
        ArrayList<String> searchStrings = new ArrayList<>();
        searchStrings.add("example1");
        searchStrings.add("example2");
        // Add more strings as needed

        // Define the file path
        String fileName = "path/to/your/file.txt";

        try {
            // Find the line with the earliest date that contains any of the search strings
            String result = findEarliestDateLine(fileName, searchStrings);
            if (result != null) {
                System.out.println("Line with the earliest date: " + result);
            } else {
                System.out.println("No matching lines found.");
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds the line with the earliest date that contains any of the specified search strings.
     * @param fileName The file to search in.
     * @param searchStrings The list of strings to search for.
     * @return The line with the earliest date, or null if no matching lines are found.
     * @throws IOException If an I/O error occurs.
     * @throws ParseException If a date parsing error occurs.
     */
    public static String findEarliestDateLine(String fileName, ArrayList<String> searchStrings) throws IOException, ParseException {
        // Define the date format used in the file
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Adjust format as per your date format in the file
        String earliestDateLine = null;
        Date earliestDate = null;

        // Open the file for reading
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            // Read each line of the file
            while ((line = br.readLine()) != null) {
                // Check if the line contains any of the search strings
                boolean containsSearchString = searchStrings.stream().anyMatch(line::contains);
                if (containsSearchString) {
                    // Split the line by the '|' delimiter
                    String[] parts = line.split("\\|");
                    if (parts.length > 0) {
                        // Parse the date from the first part of the line
                        Date lineDate = dateFormat.parse(parts[0].trim());
                        // Check if this is the earliest date found so far
                        if (earliestDate == null || lineDate.before(earliestDate)) {
                            earliestDate = lineDate;
                            earliestDateLine = line;
                        }
                    }
                }
            }
        }

        // Return the line with the earliest date
        return earliestDateLine;
    }
}
