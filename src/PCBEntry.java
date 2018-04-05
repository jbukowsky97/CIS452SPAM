import java.util.LinkedList;

/*******************************************************
 * Jonah Bukowsky
 *
 * class to hold PCB entry
 *******************************************************/
public class PCBEntry {

    /* process ID */
    private long pID;

    /* code and data pages */
    private LinkedList<Integer> codePages;
    private LinkedList<Integer> dataPages;

    /*******************************************************
     * instance initializer
     *******************************************************/
    {
        codePages = new LinkedList<>();
        dataPages = new LinkedList<>();
    }

    /*******************************************************
     * default constructor
     *
     * @param pID process ID
     *******************************************************/
    public PCBEntry(int pID) {
        this.pID = pID;
    }

    /*******************************************************
     * add specified number of code pages to process
     *
     * @param numPages number of code pages
     *******************************************************/
    public void addCodePages(int numPages) {
        for (int i = 0; i < numPages; i++) {
            codePages.addLast(codePages.size());
        }
    }

    /*******************************************************
     * add specified number of data pages to process
     *
     * @param numPages number of data pages
     *******************************************************/
    public void addDataPages(int numPages) {
        for (int i = 0; i < numPages; i++) {
            dataPages.addLast(dataPages.size());
        }
    }

    /*******************************************************
     * return process ID
     *
     * @return long process ID
     *******************************************************/
    public long getpID() {
        return pID;
    }

    /*******************************************************
     * return code pages
     *
     * @return LinkedList<Integer> code pages
     *******************************************************/
    public LinkedList<Integer> getCodePages() {
        return codePages;
    }

    /*******************************************************
     * return data pages
     *
     * @return LinkedList<Integer> data pages
     *******************************************************/
    public LinkedList<Integer> getDataPages() {
        return dataPages;
    }
}
