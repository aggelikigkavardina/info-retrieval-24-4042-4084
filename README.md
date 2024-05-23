**Search Engine Lucene**

**Overview**
This project is a comprehensive system for collecting data from CSV files, indexing the data using Apache Lucene, and providing a graphical user interface (GUI) for searching and viewing the collected data. The system is divided into four main components:

Data Collection: Reads data from a CSV file and processes it into a text file format.
Indexing: Indexes the processed data using Apache Lucene for efficient search capabilities.
Searching: Provides functionality to search the indexed data using various search types.
Graphical User Interface (GUI): Offers an interactive GUI for users to perform searches and view results.

**Components**
1. Data Collection
The DataCollector class is responsible for reading data from a CSV file, processing it, and saving it to a text file. The data is also stored in a HashSet of Record objects for further use.

CSV Input: /your/path/to/file.csv (Linux)
            your\\path\\to\\file.csv (Windows)
Text Output: /your/path/to/data.txt (Linux)
            your\\path\\to\\data.txt 
2. Indexing
The Index class indexes the text file created by the DataCollector class using Apache Lucene. This enables fast and efficient searches over the data.

Index Directory: /your/path/to/index_directory (Linux)
                your\\path\\to\\index_directory
Text File for Indexing: your/path/to/data.txt (Linux)
                        your\\path\\to\\data.txt
3. Searching
The Search class provides methods for keyword search, field-specific search, and phrase search using the Lucene index.

4. Graphical User Interface (GUI)
The SearchGUI class provides a GUI for users to perform searches and view the results. The results are displayed in a paginated format, with the option to view the full text of selected records.

**How to Use**
Prerequisites
Java Development Kit (JDK) 8 or higher
Apache Lucene library
Apache Commons CSV library

**Setup**
Clone the Repository: Clone the project repository to your local machine.
Configure File Paths: Ensure the file paths in the code are correctly set to your local directories:
CSV input file path in DataCollector constructor.
Text output file path in processCSV method of DataCollector.
Index directory path in Index constructor.
Text file path for indexing in indexTextFile method of Index.
Index directory path in SearchGUI constructor.
Compile the Project: Compile all Java files using your preferred Java IDE or command-line tools.

**Running the Application**
Data Collection: Run the DataCollector main method to process the CSV file and generate the text file.
Indexing: Run the Index main method to index the text file.
Search GUI: Run the SearchGUI main method to start the GUI.
Search Query: Enter your search query in the input field.
Select Search Type: Choose the search type (Keyword, Field, Phrase) from the dropdown.
Perform Search: Click the "Search" button to perform the search.
View Results: Browse through the search results, and click on any result to view the full text.

**Additional Features**
Search History: The GUI maintains a history of recent searches, which can be accessed from the search input dropdown.
Pagination: Results are paginated for easy navigation.
Sorting: Option to sort search results by year.
