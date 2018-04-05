import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/*******************************************************
 * Jonah Bukowsky
 *
 * class to create a visual representation of a process
 *******************************************************/
public class ProcessDisplay extends JPanel {

    /* table width and entry height */
    private static final int PAGE_TABLE_WIDTH;
    private static final int ENTRY_HEIGHT;

    /*******************************************************
     * static initializer
     *******************************************************/
    static {
        PAGE_TABLE_WIDTH = 100;
        ENTRY_HEIGHT = 30;
    }

    /* panels needed for process display */
    private JPanel mainPanel;
    private JPanel codePanel;
    private JPanel dataPanel;
    private JPanel[] codeEntries;
    private JPanel[] dataEntries;

    /* labels needed for process display */
    private JLabel title;
    private JLabel codeTitle;
    private JLabel dataTitle;

    /* text fields needed for process display */
    private JTextField[] codeTexts;
    private JTextField[] dataTexts;

    /* process being visualized */
    private PCBEntry pcbEntry;

    /*******************************************************
     * initializer
     *******************************************************/
    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        codeTitle = new JLabel("Code PT");
        codeTitle.setHorizontalAlignment(JLabel.CENTER);
        dataTitle = new JLabel("Data PT");
        dataTitle.setHorizontalAlignment(JLabel.CENTER);

        codePanel = new JPanel();
        codePanel.setLayout(new BoxLayout(codePanel, BoxLayout.Y_AXIS));
        codePanel.setAlignmentY(JPanel.TOP_ALIGNMENT);
        codePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(codePanel);

        codePanel.add(codeTitle);

        dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setAlignmentY(JPanel.TOP_ALIGNMENT);
        dataPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(dataPanel);

        dataPanel.add(dataTitle);

        this.add(mainPanel);
    }

    /*******************************************************
     * default constructor
     *
     * @param pcbEntry process being visualized
     *******************************************************/
    public ProcessDisplay(PCBEntry pcbEntry) {
        this.pcbEntry = pcbEntry;

        title = new JLabel("PTs for Process " + pcbEntry.getpID());
        title.setHorizontalAlignment(JLabel.CENTER);

        this.createCodePanels();
        this.createDataPanels();

        this.add(title);
        this.add(mainPanel);

        this.setMaximumSize(this.getPreferredSize());
    }

    /*******************************************************
     * creates code panels
     *******************************************************/
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

    /*******************************************************
     * creates data panels
     *******************************************************/
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
