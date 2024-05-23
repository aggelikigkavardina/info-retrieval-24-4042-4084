package gui;

import javax.swing.*;

import collectData.DataCollector;

import java.awt.*;

public class FullTextWindow {
    private JFrame frame;
    private JTextArea textArea;
    private Resolution res = new Resolution("800x1000");
    private String mainResolution = res.getResolution();

    // Constructor
    public FullTextWindow(DataCollector.Record record) {
        initialize(record);
    }

 // Initialize the GUI components
    private void initialize(DataCollector.Record record) {
        frame = new JFrame("Full Text");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose the frame on close
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

        textArea = new JTextArea();
        textArea.setLineWrap(true); // Enable line wrap
        textArea.setWrapStyleWord(true); // Wrap at word boundaries
        textArea.setText(record.getFullText()); // Set the text area with the full text of the record
        textArea.setCaretPosition(0); // Set caret position to the beginning

        // Set font size to 14
        Font font = textArea.getFont();
        textArea.setFont(new Font(font.getName(), font.getStyle(), 14));

        // Add the text area to a scroll pane and add it to the frame
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.setVisible(true); // Make the frame visible
    }
}