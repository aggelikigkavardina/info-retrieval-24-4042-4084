package luceneCode;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
//import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Search {
    private IndexSearcher searcher;
    private EnglishAnalyzer analyzer;
    private List<String> searchHistory;

    // Constructor to initialize IndexSearcher and Analyzer with the specified index path
    public Search(String indexPath) throws IOException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        searcher = new IndexSearcher(reader);
        analyzer = new EnglishAnalyzer();
        searchHistory = new ArrayList<>();
    }

    // Method for keyword search
    public List<Document> keywordSearch(String queryStr) throws Exception {
        searchHistory.add(queryStr);
        Query query = new QueryParser("full_text", analyzer).parse(queryStr);
        return executeSearch(query);
    }
    
    // Method for field-specific search
    public List<Document> fieldSearch(String field, String queryStr) throws Exception {
        searchHistory.add(field + ": " + queryStr);
        Query query = new QueryParser(field, analyzer).parse(queryStr);
        return executeSearch(query);
    }

    // Method for phrase search
    public List<Document> phraseSearch(String queryStr) throws Exception {
        searchHistory.add("\"" + queryStr + "\"");
        Query query = new QueryParser("full_text", analyzer).parse("\"" + queryStr + "\"");
        return executeSearch(query);
    }

    // Method to execute the search and return results
    @SuppressWarnings("deprecation")
	private List<Document> executeSearch(Query query) throws IOException {
        TopDocs results = searcher.search(query, Integer.MAX_VALUE);
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : results.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }
        return documents;
    }
    
    // Getter for search history
    public List<String> getSearchHistory() {
        return searchHistory;
    }
}