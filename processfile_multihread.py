from concurrent.futures import ThreadPoolExecutor
from datetime import datetime, date  # Adjust import based on date format
import csv  # Assuming CSV format, adjust for other delimiters

def process_file(filepath):
  """
  Processes a file, finds the line with the earliest date for a specific text, 
  and returns a string combining filename and the line.
  """
  delimiter = "|"  # Adjust delimiter if needed
  date_format = "%Y-%m-%d"  # Adjust date format if needed
  text_to_find = "your_text_here"  # Adjust text to search for
  earliest_line = None
  earliest_date = None

  try:
    with open(filepath, 'r') as file:
      reader = csv.reader(file, delimiter=delimiter)
      for row in reader:
        if text_to_find in row and len(row) > 1:  # Check for text and at least 2 fields
          try:
            current_date = datetime.strptime(row[0], date_format).date() if len(row) > 0 else None  # Adjust index for date field
          except ValueError:
            current_date = None  # Handle potential invalid date format
          if earliest_date is None or current_date and current_date < earliest_date:
            earliest_date = current_date
            earliest_line = row
        
  except FileNotFoundError:
    print(f"Error: File not found - {filepath}")
  return f"{filepath}: {' | '.join(earliest_line) if earliest_line else 'No line found'}"  # Combine filename and line

def write_to_file(filename, content):
  """
  Writes the content list to a file.
  """
  with open(filename, 'w') as file:
    for line in content:
      file.write(f"{line}\n")

if __name__ == "__main__":
  # File paths (replace with your actual file paths)
  file_paths = ["file1.txt", "file2.txt", "file3.txt", "file4.txt", "file5.txt", "file6.txt"]
  result_file = "results.txt"

  # Use a thread pool with 6 threads for concurrent processing
  with ThreadPoolExecutor(max_workers=6) as executor:
    # Submit tasks for processing each file asynchronously
    futures = [executor.submit(process_file, filepath) for filepath in file_paths]

    # Collect results from all futures
    all_results = [future.result() for future in futures]

  # Write combined results to a file
  write_to_file(result_file, all_results)
