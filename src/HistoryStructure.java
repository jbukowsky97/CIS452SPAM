import java.util.ArrayList;

public class HistoryStructure {

    private ArrayList<Integer> codeFrames;
    private ArrayList<Integer> dataFrames;

    {
        codeFrames = new ArrayList<>();
        dataFrames = new ArrayList<>();
    }

    public void addCodeFrame(int codeFrame) {
        codeFrames.add(codeFrame);
    }

    public void addDataFrame(int dataFrame) {
        dataFrames.add(dataFrame);
    }

    public ArrayList<Integer> getCodeFrames() {
        return codeFrames;
    }

    public ArrayList<Integer> getDataFrames() {
        return dataFrames;
    }

    public String toString() {
        return "Code Frames:\t" + codeFrames.toString() + "Data Frames:\t" + dataFrames.toString();
    }
}
