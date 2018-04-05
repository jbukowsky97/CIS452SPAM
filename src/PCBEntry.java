import java.util.LinkedList;

public class PCBEntry {

    private long pID;

    private LinkedList<Integer> codePages;
    private LinkedList<Integer> dataPages;

    {
        codePages = new LinkedList<>();
        dataPages = new LinkedList<>();
    }

    public PCBEntry(int pID) {
        this.pID = pID;
    }

    public void addCodePages(int numPages) {
        for (int i = 0; i < numPages; i++) {
            codePages.addLast(codePages.size());
        }
    }

    public void addDataPages(int numPages) {
        for (int i = 0; i < numPages; i++) {
            dataPages.addLast(dataPages.size());
        }
    }

    public long getpID() {
        return pID;
    }

    public LinkedList<Integer> getCodePages() {
        return codePages;
    }

    public LinkedList<Integer> getDataPages() {
        return dataPages;
    }
}
