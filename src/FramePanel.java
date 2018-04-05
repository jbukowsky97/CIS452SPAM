import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

/*******************************************************
 * Jonah Bukowsky
 *
 * class to create visual represention of frame table
 *******************************************************/
public class FramePanel extends JPanel {

    /* frame width and height */
    private static final int FRAME_WIDTH;
    private static final int TOTAL_HEIGHT;

    /*******************************************************
     * static initializer
     *******************************************************/
    static {
        FRAME_WIDTH = 200;
        TOTAL_HEIGHT = 600;
    }

    /* JPanels needed to create visual */
    private JPanel frameTablePanel;
    private JPanel[] framePanels;

    /* JTextField needed to create visual */
    private JTextField[] frameTexts;

    /*******************************************************
     * instance initializer
     *******************************************************/
    {
        frameTablePanel = new JPanel();
        frameTablePanel.setLayout(new BoxLayout(frameTablePanel, BoxLayout.Y_AXIS));
        frameTablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(frameTablePanel);
    }

    /*******************************************************
     * default constructor
     *
     * @param numFrames number of frames in frame table
     *******************************************************/
    public FramePanel(int numFrames) {
        framePanels = new JPanel[numFrames];
        frameTexts = new JTextField[numFrames];
        for (int i = 0; i < numFrames; i++) {
            framePanels[i] = createFramePanel(TOTAL_HEIGHT / numFrames);
            frameTexts[i] = createFrameText();
            framePanels[i].add(frameTexts[i], BorderLayout.CENTER);
            frameTablePanel.add(framePanels[i]);
        }
        frameTablePanel.setMaximumSize(frameTablePanel.getPreferredSize());
    }

    /*******************************************************
     * creates single frame panel
     *
     * @param height height of frame panel
     *******************************************************/
    private JPanel createFramePanel(int height) {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.setPreferredSize(new Dimension(FRAME_WIDTH, height));
        p.setMaximumSize(new Dimension(FRAME_WIDTH, height));
        return p;
    }

    /*******************************************************
     * creates single frame text
     *******************************************************/
    private JTextField createFrameText() {
        JTextField t = new JTextField();
        t.setHorizontalAlignment(JTextField.CENTER);
        t.setFont(new Font(t.getFont().getName(), Font.BOLD, t.getFont().getSize()));
        t.setEditable(false);
        t.setText("Free");
        return t;
    }

    /*******************************************************
     * updates texts of frame entries
     *
     * @param newTexts list of updated texts
     *******************************************************/
    public void updateTexts(ArrayList<String> newTexts) {
        if (newTexts.size() != frameTexts.length) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < frameTexts.length; i++) {
            frameTexts[i].setText(newTexts.get(i));
        }
    }
}
