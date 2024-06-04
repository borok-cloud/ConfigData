import datetime

def find_earliest_dates(file_name, search_strings):
    # Initialize dictionaries to store the earliest dates and corresponding lines
    earliest_dates = {string: None for string in search_strings}
    earliest_date_lines = {string: None for string in search_strings}

    # Open the file for reading
    with open(file_name, 'r') as file:
        for line in file:
            # Split the line by the '|' delimiter
            parts = line.split('|')
            if len(parts) > 0:
                try:
                    # Parse the date from the first part of the line
                    line_date = datetime.datetime.strptime(parts[0].strip(), '%Y-%m-%d')
                except ValueError:
                    # If the date is not in the correct format, skip this line
                    continue

                # Check if the line contains any of the search strings
                for search_string in search_strings:
                    if search_string in line:
                        # Check if this is the earliest date found for this search string
                        if earliest_dates[search_string] is None or line_date < earliest_dates[search_string]:
                            earliest_dates[search_string] = line_date
                            earliest_date_lines[search_string] = line

    return earliest_date_lines

# Example usage
search_strings = ["example1", "example2"]
file_name = "path/to/your/file.txt"

result = find_earliest_dates(file_name, search_strings)
for search_string, line in result.items():
    print(f"Earliest line for '{search_string}': {line.strip()}")
