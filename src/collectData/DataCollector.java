package collectData;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

public class DataCollector {

    private String csvFilePath;
    private HashSet<Record> records;
    
    // Constructor to initialize the CSV file path and the HashSet of records
    public DataCollector(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        this.records = new HashSet<>();
    }

    // Method to process the CSV file and write the data to a text file
    @SuppressWarnings("deprecation")
	public void processCSV(String textFilePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(textFilePath));
             FileReader reader = new FileReader(csvFilePath);
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {
        	// Iterate through each record in the CSV file
            for (CSVRecord record : parser) {
                String sourceId = record.get("source_id");
                String year = record.get("year");
                String title = record.get("title");
                String abstractText = record.get("abstract");
                String fullText = record.get("full_text");

                // Add the record to the HashSet
                records.add(new Record(sourceId, fullText));
                
                // Write the record details to the text file
                writer.write("source_id: " + sourceId);
                writer.newLine();
                writer.write("year: " + year);
                writer.newLine();
                writer.write("title: " + title);
                writer.newLine();
                writer.write("abstract: " + abstractText);
                writer.newLine();
                writer.write("full_text: " + fullText);
                writer.newLine();
                writer.write("----------");
                writer.newLine();
            }
        }
    }
    
    // Getter for the records HashSet
    public HashSet<Record> getRecords() {
        return records;
    }

    // Nested class to represent a record with source_id and full_text
    public static class Record {
        private String sourceId;
        private String fullText;

        // Constructor to initialize sourceId and fullText
        public Record(String sourceId, String fullText) {
            this.sourceId = sourceId;
            this.fullText = fullText;
        }
        
        // Getters for sourceId and fullText
        public String getSourceId() {
            return sourceId;
        }

        public String getFullText() {
            return fullText;
        }

        // Override equals method for comparison
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Record record = (Record) o;

            if (!sourceId.equals(record.sourceId)) return false;
            return fullText.equals(record.fullText);
        }

        // Override hashCode method for hashing
        @Override
        public int hashCode() {
            int result = sourceId.hashCode();
            result = 31 * result + fullText.hashCode();
            return result;
        }
    }
    
    public static void main(String[] args) {
        try {
        	// Initialize DataCollector and process the CSV file
            DataCollector data = new DataCollector("/your/path/to/papers.csv");
            data.processCSV("/your/path/to/SearchEngineLucene/data/data.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
