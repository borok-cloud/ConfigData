import concurrent.futures
import csv
from datetime import datetime

def process_file(file_path, search_text, date_column_index, delimiter='|'):
    earliest_line = None
    earliest_date = None
    date_format = '%Y-%m-%d'  # Adjust the date format if needed

    try:
        with open(file_path, 'r') as csvfile:
            reader = csv.reader(csvfile, delimiter=delimiter)
            for line in reader:
                if search_text in line:
                    date_str = line[date_column_index]
                    try:
                        current_date = datetime.strptime(date_str, date_format)
                    except ValueError:
                        continue  # Skip lines with invalid date format

                    if earliest_date is None or current_date < earliest_date:
                        earliest_date = current_date
                        earliest_line = line
    except Exception as e:
        print(f"Error processing file {file_path}: {e}")

    return earliest_line

def main():
    file_paths = [
        'file1.txt',
        'file2.txt',
        'file3.txt',
        'file4.txt',
        'file5.txt',
        'file6.txt'
    ]
    search_text = 'some_text'
    date_column_index = 0  # Adjust this to the actual date column index
    delimiter = '|'
    result_file_path = 'result.txt'

    with concurrent.futures.ThreadPoolExecutor() as executor:
        futures = [
            executor.submit(process_file, file_path, search_text, date_column_index, delimiter)
            for file_path in file_paths
        ]

        results = [future.result() for future in concurrent.futures.as_completed(futures)]

    # Filter out None results and write to the result file
    with open(result_file_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile, delimiter=delimiter)
        for result in results:
            if result:
                writer.writerow(result)

if __name__ == '__main__':
    main()
