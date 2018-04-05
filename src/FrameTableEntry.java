/*******************************************************
 * Jonah Bukowsky
 *
 * class to hold frame table entry
 *******************************************************/
public class FrameTableEntry {

    /* process ID */
    private long pID;

    /* virtual page frame is mapped to */
    private int pageMapping;

    /* enum for frame type */
    public enum FRAME_TYPE {
        CODE,
        DATA
    }

    /* type of virtual page frame is mapped to */
    private FRAME_TYPE frameType;

    /*******************************************************
     * default constructor
     *
     * @param pID process ID
     * @param pageMapping virtual page frame is mapped to
     * @param frameType type of virtual page frame is mapped to
     *******************************************************/
    public FrameTableEntry(long pID, int pageMapping, FRAME_TYPE frameType) {
        this.pID = pID;
        this.pageMapping = pageMapping;
        this.frameType = frameType;
    }

    /*******************************************************
     * returns process ID
     *
     * @return long process ID
     *******************************************************/
    public long getpID() {
        return pID;
    }

    /*******************************************************
     * returns page mapping
     *
     * @return int page mapping
     *******************************************************/
    public int getPageMapping() {
        return pageMapping;
    }

    /*******************************************************
     * returns frame type
     *
     * @return FRAME_TYPE frame type
     *******************************************************/
    public FRAME_TYPE getFrameType() {
        return frameType;
    }
}
