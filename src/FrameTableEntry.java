public class FrameTableEntry {

    private long pID;

    private int pageMapping;

    public enum FRAME_TYPE {
        CODE,
        DATA
    }

    private FRAME_TYPE frameType;

    public FrameTableEntry(long pID, int pageMapping, FRAME_TYPE frame_type) {
        this.pID = pID;
        this.pageMapping = pageMapping;
        this.frameType = frame_type;
    }

    public long getpID() {
        return pID;
    }

    public int getPageMapping() {
        return pageMapping;
    }

    public FRAME_TYPE isCodeFrame() {
        return frameType;
    }
}
