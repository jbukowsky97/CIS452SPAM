import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/*******************************************************
 * Jonah Bukowsky
 *
 * class that creates and manages GUI to depict kernels operations
 *******************************************************/
public class GUI extends JPanel {

    /* static variables */
    private static final int OUTPUT_MAX_LINES;
    private static final int DEFAULT_RAM_SIZE;
    private static final int DEFAULT_PAGE_SIZE;

    /*******************************************************
     * static initializer
     *******************************************************/
    static {
        OUTPUT_MAX_LINES = 39;
        DEFAULT_RAM_SIZE = 4096;
        DEFAULT_PAGE_SIZE = 512;
    }

    /* list of instructions */
    private ArrayList<String> instructions;

    /* JFrame of GUI */
    private JFrame jFrame;

    /* JPanels used for GUI */
    private JPanel mainPanel;
    private JPanel instructionPanel;
    private JPanel processPanel;

    /* custom panel to show visual of frame table */
    private FramePanel framePanel;

    /* custom panels that show visual of processes */
    private ArrayList<ProcessDisplay> processDisplays;

    /* area where instructions are shown */
    private JTextArea instructionArea;

    /* area where output of kernel is shown */
    private JTextArea outputArea;

    /* next and prev buttons to control kernel */
    private JButton nextButton;
    private JButton prevButton;

    /* kernel instance */
    private Kernel kernel;

    /* action listener instance */
    private MyActionListener myActionListener;

    /*******************************************************
     * initializer of instance variables that don't require constructor
     *******************************************************/
    {
        myActionListener = new MyActionListener();

        jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setResizable(false);

        jFrame.getContentPane().add(this);

        jFrame.setVisible(true);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        instructionPanel = new JPanel();
        instructionPanel.setLayout(new BoxLayout(instructionPanel, BoxLayout.Y_AXIS));

        processPanel = new JPanel();
        processPanel.setLayout(new BoxLayout(processPanel, BoxLayout.X_AXIS));

        processDisplays = new ArrayList<>();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        instructionArea = new JTextArea();
        instructionArea.setEditable(false);
        instructionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionPanel.add(instructionArea);

        nextButton = new JButton("Next Instruction");
        nextButton.addActionListener(myActionListener);
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionPanel.add(nextButton);

        prevButton = new JButton("Prev Instruction");
        prevButton.addActionListener(myActionListener);
        prevButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionPanel.add(prevButton);

        mainPanel.add(instructionPanel);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        outputArea.setPreferredSize(new Dimension(800, 600));
        outputArea.setMaximumSize(new Dimension(800, 600));
    }

    /*******************************************************
     * default constructor
     *
     * @param instructions instruction list for kernel
     * @param ramSize desired RAM size
     * @param pageSize desired PAGE size
     *******************************************************/
    public GUI(ArrayList<String> instructions, int ramSize, int pageSize) {
        this.instructions = instructions;

        for (String instruction : instructions) {
            instructionArea.append(instruction + "\n");
        }
        instructionArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        instructionArea.setMaximumSize(instructionArea.getPreferredSize());

        kernel = new Kernel(instructions, ramSize, pageSize);

        prevButton.setEnabled(!kernel.getFirst());

        framePanel = new FramePanel(kernel.getNumFrames());
        mainPanel.add(framePanel);

        mainPanel.add(outputArea);

        mainPanel.setMaximumSize(mainPanel.getPreferredSize());

        this.add(mainPanel);
        this.add(processPanel);

        jFrame.pack();
    }

    /*******************************************************
     * refreshes all dynamic panels of GUI
     *******************************************************/
    private void refreshDisplay() {
        processDisplays.clear();
        for (PCBEntry pcbEntry : kernel.getPcb()) {
            processDisplays.add(new ProcessDisplay(pcbEntry));
        }
        this.remove(processPanel);
        processPanel = new JPanel();
        for (ProcessDisplay processDisplay : processDisplays) {
            processPanel.add(processDisplay);
        }
        this.add(processPanel);

        mainPanel.remove(framePanel);

        ArrayList<String> newTexts = kernel.getNewTexts();

        framePanel.updateTexts(newTexts);

        mainPanel.add(framePanel, 1);

        nextButton.setEnabled(!kernel.getDone());
        prevButton.setEnabled(!kernel.getFirst());

        outputArea.setText("");
        ArrayList<String> logStrings = kernel.getLogStrings();
        for (int i = 0; i < OUTPUT_MAX_LINES; i++) {
            if (i >= logStrings.size()) {
                break;
            }
            outputArea.append(logStrings.get(i + Math.max(logStrings.size() - OUTPUT_MAX_LINES, 0)) + "\n");
        }

        jFrame.pack();
    }

    /*******************************************************
     * class to handle actions
     *******************************************************/
    private class MyActionListener implements ActionListener {

        /*******************************************************
         * method to handle action
         *
         * @param actionEvent action being handled
         *******************************************************/
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == nextButton) {
                kernel.parseNextInstruction();
            } else if (actionEvent.getSource() == prevButton) {
                kernel.undoLastInstruction();
            }
            refreshDisplay();
        }
    }

    /*******************************************************
     * main function to create GUI, entry point to project
     *
     * @param args list of arguments supplied to program
     *******************************************************/
    public static void main(String[] args) {
        if (args.length != 1 && args.length != 3) {
            System.out.println("Usage:\n\tjava GUI <instruction file>\n\t\t-OR-\n\tjava GUI <instruction file> <RAM_SIZE> <PAGE_SIZE>");
            System.exit(-1);
        }
        String filename = args[0];

        ArrayList<String> instructions = new ArrayList<>();

        try {
            BufferedReader r = new BufferedReader(new FileReader(filename));
            while (r.ready()) {
                instructions.add(r.readLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (args.length == 3) {
            int ramSize = Integer.parseInt(args[1]);
            int pageSize = Integer.parseInt(args[2]);
            new GUI(instructions, ramSize, pageSize);
        }else {
            new GUI(instructions, DEFAULT_RAM_SIZE, DEFAULT_PAGE_SIZE);
        }
    }
}
