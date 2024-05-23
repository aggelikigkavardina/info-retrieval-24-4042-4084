package gui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import org.apache.lucene.document.Document;
import collectData.DataCollector.Record;

public class ResultWindow {
    private JFrame frame;
    private JList<String> resultList;
    private DefaultListModel<String> listModel;
    private JButton nextButton;
    private JButton prevButton;
    private JCheckBox sortByYearCheckBox;
    private List<Document> documents;
    private List<Document> originalDocuments; // To store original order of documents
    private HashSet<Record> recordSet;
    private int currentPage;
    private static final int RESULTS_PER_PAGE = 10;
     private Resolution res = new Resolution("1200x800");
    private String mainResolution = res.getResolution();
    @SuppressWarnings("unused")
    private boolean isSortedByYear = false;

    // Constructor
    public ResultWindow(List<Document> results, String query, HashSet<Record> recordSet) {
        this.documents = new ArrayList<>(results);
        this.originalDocuments = new ArrayList<>(results); // Save original order
        this.currentPage = 0;
        this.recordSet = recordSet;
        initialize(query); // Initialize the GUI components
    }

    // Initialize the GUI components
    private void initialize(String query) {
        frame = new JFrame("Search Results");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(res.changeResolution(mainResolution));
        frame.setLocationRelativeTo(null); // Center the frame on the screen

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

        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add listener for list item selection
        resultList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int index = resultList.getSelectedIndex();
                    if (index != -1) {
                        int globalIndex = currentPage * RESULTS_PER_PAGE + index;
                        String sourceId = documents.get(globalIndex).get("source_id");
                        for (Record record : recordSet) {
                            if (record.getSourceId().equals(sourceId)) {
                                new FullTextWindow(record); // Open full text window
                                break;
                            }
                        }
                    }
                }
            }
        });

        nextButton = new JButton("Next");
        prevButton = new JButton("Previous");
        sortByYearCheckBox = new JCheckBox("Sort by Year");

        // Add action listener for the next button
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage++;
                updateResults(query); // Update results for next page
            }
        });

        // Add action listener for the previous button
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage--;
                updateResults(query); // Update results for previous page
            }
        });

        // Add action listener for the sort by year checkbox
        sortByYearCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sortByYearCheckBox.isSelected()) {
                    documents.sort((d1, d2) -> -d1.get("year").compareTo(d2.get("year"))); // Sort by year descending
                    isSortedByYear = true;
                    currentPage = 0;
                    updateResults(query);
                } else {
                    documents = new ArrayList<>(originalDocuments); // Restore original order
                    isSortedByYear = false;
                    currentPage = 0;
                    updateResults(query);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);

        frame.add(new JScrollPane(resultList), BorderLayout.CENTER);
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(sortByYearCheckBox, BorderLayout.WEST); // Align "Sort by Year" to the left
        controlPanel.add(buttonPanel, BorderLayout.EAST); // Align buttons panel to the right
        frame.add(controlPanel, BorderLayout.SOUTH);

        updateResults(query); // Initial result update
        frame.setVisible(true); // Make the frame visible
    }

    // Update the result list based on the current page and query
    private void updateResults(String query) {
        listModel.clear();
        int start = currentPage * RESULTS_PER_PAGE;
        int end = Math.min(start + RESULTS_PER_PAGE, documents.size());

        // Add results to the list model
        for (int i = start; i < end; i++) {
            Document doc = documents.get(i);
            String title = doc.get("title");
            String year = doc.get("year");
            String snippet = createSnippet(doc.get("abstract"), query);
            listModel.addElement(formatResult(title, year, snippet, query));
            if (i < end - 1) {
                listModel.addElement("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            }
        }

        // Enable or disable buttons based on current page
        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(end < documents.size());
    }

    // Create a snippet from the abstract text that includes the query
    private String createSnippet(String abstractText, String query) {
        if (abstractText == null || abstractText.isEmpty()) {
            return "No abstract available.";
        }

        int queryIndex = abstractText.toLowerCase().indexOf(query.toLowerCase());
        if (queryIndex == -1) {
            return abstractText.substring(0, Math.min(50, abstractText.length())) + "...";
        }

        int start = Math.max(0, queryIndex - 50);
        int end = Math.min(abstractText.length(), queryIndex + query.length() + 50);

        String snippet = abstractText.substring(start, queryIndex) +
                "<b>" + abstractText.substring(queryIndex, queryIndex + query.length()) + "</b>" +
                abstractText.substring(queryIndex + query.length(), end);

        if (start > 0) {
            snippet = "..." + snippet;
        }
        if (end < abstractText.length()) {
            snippet += "...";
        }

        return snippet;
    }

    // Format the result for display in the list
    private String formatResult(String title, String year, String snippet, String query) {
        return "<html><body style='font-family:sans-serif;'>" +
                "<h2 style='margin:0;'>" + highlightQuery(title, query) + "</h2>" +
                "<p style='margin:0;'><b>Year: </b>" + year + "</p>" +
                "<p style='margin:0;'><b>Abstract: </b>" + highlightQuery(snippet, query) + "</p>" +
                "</body></html>";
    }

    // Highlight occurrences of the query in the text
    private String highlightQuery(String text, String query) {
        if (query == null || query.isEmpty()) {
            return text;
        }

        String queryLower = query.toLowerCase();
        String textLower = text.toLowerCase();
        int startIndex = 0;
        StringBuilder highlightedText = new StringBuilder();

        // Highlight all occurrences of the query
        while (startIndex < text.length()) {
            int queryIndex = textLower.indexOf(queryLower, startIndex);
            if (queryIndex == -1) {
                highlightedText.append(text.substring(startIndex));
                break;
            }

            highlightedText.append(text.substring(startIndex, queryIndex))
                    .append("<b style='background-color: yellow;'>")
                    .append(text.substring(queryIndex, queryIndex + query.length()))
                    .append("</b>");
            startIndex = queryIndex + query.length();
        }

        return highlightedText.toString();
    }
}
