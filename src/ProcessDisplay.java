import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProcessDisplay extends JPanel {

    private static final int PAGE_TABLE_WIDTH;
    private static final int ENTRY_HEIGHT;

    static {
        PAGE_TABLE_WIDTH = 100;
        ENTRY_HEIGHT = 50;
    }

    private JPanel mainPanel;

    private JPanel codePanel;
    private JPanel dataPanel;

    private JPanel[] codeEntries;
    private JPanel[] dataEntries;

    private JTextField title;

    private JTextField[] codeTexts;
    private JTextField[] dataTexts;

    private PCBEntry pcbEntry;

    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        codePanel = new JPanel();
        codePanel.setLayout(new BoxLayout(codePanel, BoxLayout.Y_AXIS));
        codePanel.setAlignmentY(JPanel.TOP_ALIGNMENT);
        codePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(codePanel);

        dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setAlignmentY(JPanel.TOP_ALIGNMENT);
        dataPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(dataPanel);

        this.add(mainPanel);
    }

    public ProcessDisplay(PCBEntry pcbEntry) {
        this.pcbEntry = pcbEntry;

        title = new JTextField("PT for Process " + pcbEntry.getpID());
        title.setEditable(false);

        this.createCodePanels();
        this.createDataPanels();

        this.add(title);
        this.add(mainPanel):

        this.setMaximumSize(this.getPreferredSize());
    }

    private void createCodePanels() {
        codeEntries = new JPanel[pcbEntry.getCodePages().size()];
        codeTexts = new JTextField[pcbEntry.getCodePages().size()];
        for (Integer i : pcbEntry.getCodePages()) {
            codeEntries[i] = new JPanel();
            codeEntries[i].setLayout(new BorderLayout());
            codeEntries[i].setPreferredSize(new Dimension(PAGE_TABLE_WIDTH, ENTRY_HEIGHT));
            codeTexts[i] = new JTextField();
            codeTexts[i].setHorizontalAlignment(JTextField.CENTER);
            codeTexts[i].setText("C" + Integer.toString(i));
            codeEntries[i].add(codeTexts[i], BorderLayout.CENTER);
            codePanel.add(codeEntries[i]);
        }
        codePanel.setMaximumSize(codePanel.getPreferredSize());
    }
    
    private void createDataPanels() {
        dataEntries = new JPanel[pcbEntry.getDataPages().size()];
        dataTexts = new JTextField[pcbEntry.getDataPages().size()];
        for (Integer i : pcbEntry.getDataPages()) {
            dataEntries[i] = new JPanel();
            dataEntries[i].setLayout(new BorderLayout());
            dataEntries[i].setPreferredSize(new Dimension(PAGE_TABLE_WIDTH, ENTRY_HEIGHT));
            dataTexts[i] = new JTextField();
            dataTexts[i].setHorizontalAlignment(JTextField.CENTER);
            dataTexts[i].setText("D" + Integer.toString(i));
            dataEntries[i].add(dataTexts[i], BorderLayout.CENTER);
            dataPanel.add(dataEntries[i]);
        }
        dataPanel.setMaximumSize(dataPanel.getPreferredSize());
    }
}
