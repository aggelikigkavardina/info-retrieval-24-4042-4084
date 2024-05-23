package gui;

import javax.swing.*;
import org.apache.lucene.document.Document;
import luceneCode.Search;
import collectData.DataCollector;
import collectData.DataCollector.Record;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;

public class SearchGUI {
    private JFrame frame;
    private JComboBox<String> searchField;
    private JComboBox<String> searchTypeBox;
    private JButton searchButton;
    private Search searcher;
    private HashSet<Record> recordSet;
    private LinkedList<String> searchHistory;
    private Resolution res = new Resolution("600x200");
    private String mainResolution = res.getResolution();

    private static final String HISTORY_FILE = "/your/path/to/data/search_history.txt";

    // Constructor
    public SearchGUI(String indexPath, HashSet<Record> recordSet) throws IOException {
        searcher = new Search(indexPath);  // Initialize the searcher with the given index path
        this.recordSet = recordSet;
        this.searchHistory = new LinkedList<>();  // Initialize the search history
        initialize();  // Initialize the GUI components
    }

    // Initialize the GUI components
    private void initialize() {
        frame = new JFrame("Search Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.setSize( res.changeResolution(mainResolution));
        frame.setLocationRelativeTo(null);  // Center the frame on the screen

        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("Options");
        
        // Define common resolutions
        String[] resolutions = {"600x200","800x600", "1024x768", "1280x720", "1366x768", "1920x1080"};

        // Add submenu items for each resolution
        for (String resolution : resolutions) {
            JMenuItem resolutionItem = new JMenuItem(resolution);
            resolutionItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedResolution = resolutionItem.getText();
                    frame.setSize(res.changeResolution(selectedResolution));
                }
            });
            optionsMenu.add(resolutionItem);
        }

        menuBar.add(optionsMenu);
        frame.setJMenuBar(menuBar);


        JPanel panel = new JPanel(new GridLayout(3, 1));
        searchField = new JComboBox<>();
        searchField.setEditable(true);  // Allow user to enter text

        String[] searchTypes = {"Keyword", "Field", "Phrase"};
        searchTypeBox = new JComboBox<>(searchTypes);
        searchButton = new JButton("Search");

        // Add components to the panel
        panel.add(new JLabel("Enter search query:"));
        panel.add(searchField);
        panel.add(searchTypeBox);
        panel.add(searchButton);
        frame.add(panel);

        // Add action listener for the search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();  // Perform search when button is clicked
            }
        });
        
        loadSearchHistory();  // Load previous search history
        searchField.setSelectedItem(null);  // Set the search field initially to empty
        frame.setVisible(true);  // Make the frame visible
    }

    // Perform the search operation
    private void performSearch() {
        String query = (String) searchField.getSelectedItem();
        if (query == null || query.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Search query cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        addSearchHistory(query);  // Add the query to search history

        String searchType = (String) searchTypeBox.getSelectedItem();
        List<Document> results = null;
        try {
            // Perform search based on selected type
            if ("Keyword".equals(searchType)) {
                results = searcher.keywordSearch(query);
            } else if ("Field".equals(searchType)) {
                String field = JOptionPane.showInputDialog(frame, "Enter field (title, year, abstract, full_text):");
                results = searcher.fieldSearch(field, query);
            } else if ("Phrase".equals(searchType)) {
                results = searcher.phraseSearch(query);
            }
            new ResultWindow(results, query, recordSet);  // Display results in a new window
            
            searchField.setSelectedItem("");  // Clear search field after search
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add query to search history
    private void addSearchHistory(String query) {
        searchHistory.remove(query);  // Remove the query if it already exists to avoid duplicates
        searchHistory.addFirst(query);  // Add the query to the beginning of the list
        if (searchHistory.size() > 3) {
            searchHistory.removeLast();  // Limit history size to the last 3 queries
        }
        updateSearchHistory();  // Update the search history in the combo box
        saveSearchHistory();  // Save the updated search history to file
    }

    // Update the search history combo box
    private void updateSearchHistory() {
        searchField.removeAllItems();
        for (String history : searchHistory) {
            searchField.addItem(history);
        }
    }

    // Load search history from file
    private void loadSearchHistory() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HISTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                searchHistory.add(line);
            }
            updateSearchHistory();  // Update the combo box with loaded history
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save search history to file
    private void saveSearchHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            for (String query : searchHistory) {
                writer.write(query);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        try {
            DataCollector dataCollector = new DataCollector("/your/path/to/papers.csv");
            dataCollector.processCSV("/your/path/to/data.txt");
            new SearchGUI("/your/path/to/index", dataCollector.getRecords());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
