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

public class GUI extends JPanel {

    private ArrayList<String> instructions;

    private JFrame jFrame;

    private JPanel mainPanel;
    private JPanel instructionPanel;
    private JPanel processPanel;

    private FramePanel framePanel;

    private ArrayList<ProcessDisplay> processDisplays;

    private JTextArea instructionArea;
    private JTextArea outputArea;

    private JButton nextButton;
    private JButton prevButton;

    private Kernel kernel;

    private MyActionListener myActionListener;

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

    public GUI(ArrayList<String> instructions) {
        this.instructions = instructions;

        for (String instruction : instructions) {
            instructionArea.append(instruction + "\n");
        }
        instructionArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        instructionArea.setMaximumSize(instructionArea.getPreferredSize());

        kernel = new Kernel(instructions);

        prevButton.setEnabled(!kernel.getFirst());

        framePanel = new FramePanel(kernel.getNumFrames());
        mainPanel.add(framePanel);

        mainPanel.add(outputArea);

        mainPanel.setMaximumSize(mainPanel.getPreferredSize());

        this.add(mainPanel);
        this.add(processPanel);

        jFrame.pack();
    }

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

        jFrame.pack();
    }

    private class MyActionListener implements ActionListener {
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

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Must provide instruction file");
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

        new GUI(instructions);
    }
}
