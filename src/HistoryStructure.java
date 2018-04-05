import java.util.ArrayList;

/*******************************************************
 * Jonah Bukowsky
 *
 * class to hold history information needed to undo
 *******************************************************/
public class HistoryStructure {

    /* list of where code pages were stored in physical memory previous time */
    private ArrayList<Integer> codeFrames;

    /* list of where data pages were stored in physical memory previous time */
    private ArrayList<Integer> dataFrames;

    /*******************************************************
     * initialize arrays empty
     *******************************************************/
    {
        codeFrames = new ArrayList<>();
        dataFrames = new ArrayList<>();
    }

    /*******************************************************
     * adds frame to list of code frames
     *
     * @param codeFrame frame being added
     *******************************************************/
    public void addCodeFrame(int codeFrame) {
        codeFrames.add(codeFrame);
    }

    /*******************************************************
     * adds frame to list of data frames
     *
     * @param dataFrame frame being added
     *******************************************************/
    public void addDataFrame(int dataFrame) {
        dataFrames.add(dataFrame);
    }

    /*******************************************************
     * returns codeFrames list
     *
     * @return ArrayList<Integer> codeFrames list
     *******************************************************/
    public ArrayList<Integer> getCodeFrames() {
        return codeFrames;
    }

    /*******************************************************
     * returns dataFrames list
     *
     * @return ArrayList<Integer> dataFrames list
     *******************************************************/
    public ArrayList<Integer> getDataFrames() {
        return dataFrames;
    }
}
