package luceneCode;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class Index {

    private IndexWriter writer;

    // Constructor to initialize IndexWriter with the specified index path
    public Index(String indexPath) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer());
        writer = new IndexWriter(dir, config);
    }
    
    // Method to index the text file
	public void indexTextFile(String textFilePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(textFilePath))) {
        	StringBuilder fullTextBuilder = new StringBuilder();
        	String line;
        	String fullText;
            Document doc = new Document();
            // Read each line of the text file and create a document
            while ((line = br.readLine()) != null) {
                if (line.startsWith("source_id: ")) {
                    if (doc.getFields().size() > 0) {
                        writer.addDocument(doc);
                        doc = new Document();
                    }
                    doc.add(new TextField("source_id", line.substring(11), Field.Store.YES));
                } else if (line.startsWith("year: ")) {
                	doc.add(new TextField("year", line.substring(6), Field.Store.YES));
                	doc.add(new SortedDocValuesField("year", new BytesRef(line.substring(6))));
                } else if (line.startsWith("title: ")) {
                    doc.add(new TextField("title", line.substring(7), Field.Store.YES));
                } else if (line.startsWith("abstract: ")) {
                    doc.add(new TextField("abstract", line.substring(10), Field.Store.YES));
                } else if (line.startsWith("full_text: ")) {
                	while (!(line.equals("----------"))) {
                		fullTextBuilder.append(line).append("\n"); // Append each line and a newline character
                		line = br.readLine();
                	}
                	fullText = fullTextBuilder.toString().trim(); // Get the complete text as a string, removing trailing newline if any
                    doc.add(new TextField("full_text", fullText.substring(11), Field.Store.YES));
                    fullText = "";                
                } else if (line.equals("----------")) {
                    writer.addDocument(doc);
                    doc = new Document();
                }
            }
            // Add the last document if not already added
            if (doc.getFields().size() > 0) {
                writer.addDocument(doc);
            }
        }
    }

	// Method to close the IndexWriter
    public void close() throws IOException {
        writer.close();
    }

    public static void main(String[] args) {
        try {
        	// Initialize Index and index the text file
            Index indexer = new Index("/your/path/to/SearchEngineLucene/index");
            indexer.indexTextFile("/your/path/to/SearchEngineLucene/data/data.txt");
            indexer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
