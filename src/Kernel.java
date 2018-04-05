import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;

/*******************************************************
 * Jonah Bukowsky
 *
 * class to mimic kernel managing virtual and physical frames
 *******************************************************/
public class Kernel {

    /* ram size */
    private int RAM;

    /* page/frame size */
    private int PAGE_SIZE;

    /* linked list of processes running */
    private LinkedList<PCBEntry> pcb;

    /* tree set of free frame indexes */
    private TreeSet<Integer> freeFrames;

    /* index of frame with value of PID it is used by */
    private FrameTableEntry[] frameTable;

    /* list of instructions for kernel to perform */
    private ArrayList<String> instructions;

    /* history objects to store information needed to undo */
    private ArrayList<HistoryStructure> historyStructures;

    /* output of kernel describing what is happening */
    private ArrayList<String> logStrings;

    /* current index in array of instructions */
    private int instructionIndex;

    /*******************************************************
     * initializes instance variables not affected by constructor
     *******************************************************/
    {
        instructionIndex = 0;

        pcb = new LinkedList<>();
        freeFrames = new TreeSet<>();
        historyStructures = new ArrayList<>();
        logStrings = new ArrayList<>();
    }

    /*******************************************************
     * default constructor
     *
     * @param instructions the instruction set for the kernel to perform
     * @param ramSize desired RAM size
     * @param pageSize desired PAGE size
     *******************************************************/
    public Kernel(ArrayList<String> instructions, int ramSize, int pageSize) {
        this.instructions = instructions;
        this.RAM = ramSize;
        this.PAGE_SIZE = pageSize;

        frameTable = new FrameTableEntry[RAM / PAGE_SIZE];

        for (int i = 0; i < frameTable.length; i++) {
            freeFrames.add(i);
            frameTable[i] = null;
        }
    }

    /*******************************************************
     * parses and executes next instruction from instruction set
     *******************************************************/
    public void parseNextInstruction() {
        String instruction = instructions.get(instructionIndex++);
        if (historyStructures.size() > instructionIndex - 1) {
            historyStructures.remove(instructionIndex - 1);
        }
        historyStructures.add(instructionIndex - 1, new HistoryStructure());
        if (!instruction.contains("-1")) {
            int pID = Integer.parseInt(instruction.substring(0, instruction.indexOf(" ")));
            instruction = instruction.substring(instruction.indexOf(" ") + 1);
            int codeSize = Integer.parseInt(instruction.substring(0, instruction.indexOf(" ")));
            instruction = instruction.substring(instruction.indexOf(" ") + 1);
            int dataSize = Integer.parseInt(instruction);

            this.loadProcess(pID, codeSize, dataSize);
        } else {
            int pID = Integer.parseInt(instruction.substring(0, instruction.indexOf(" ")));

            logStrings.add("Freeing process " + pID);
            this.freeProcess(pID);
            logStrings.add("Process " + pID + " freed");
        }
    }

    /*******************************************************
     * undoes last instruction from instruction set
     *******************************************************/
    public void undoLastInstruction() {
        logStrings.add("Undoing last instruction");

        String instruction = instructions.get(--instructionIndex);
        if (!instruction.contains("-1")) {
            int pID = Integer.parseInt(instruction.substring(0, instruction.indexOf(" ")));

            freeProcess(pID);
        } else {
            int pID = Integer.parseInt(instruction.substring(0, instruction.indexOf(" ")));

            String loadInstruction;
            int localIndex = instructionIndex - 1;
            while (true) {
                loadInstruction = instructions.get(localIndex--);
                if (pID == Integer.parseInt(loadInstruction.substring(0, loadInstruction.indexOf(" ")))) {
                    break;
                }
            }

            this.loadProcess(pID, historyStructures.get(localIndex + 1).getCodeFrames(), historyStructures.get(localIndex + 1).getDataFrames());
        }

        logStrings.add("Undo complete");
    }

    /*******************************************************
     * performs necessary operations to load process into RAM
     *
     * @param pID process ID
     * @param codeSize size of code section
     * @param dataSize size of data section
     *******************************************************/
    private void loadProcess(int pID, int codeSize, int dataSize) {
        int codePages = (codeSize + PAGE_SIZE - 1) / PAGE_SIZE;
        int dataPages = (dataSize + PAGE_SIZE - 1) / PAGE_SIZE;
        PCBEntry pcbEntry = new PCBEntry(pID);

        logStrings.add("Loading program " + pID + " into RAM: code=" + codeSize + " (" + codePages + " pages), data=" + dataSize + " (" + dataPages + " pages)");

        this.assignPages(pcbEntry, codePages, dataPages);
        pcb.add(pcbEntry);

        logStrings.add("Program " + pID + " loaded");
    }

    /*******************************************************
     * special version of loadProcess for undo to properly work
     *
     * @param pID process ID
     * @param codeFrames previous frames code section was allocated in
     * @param dataFrames previous frames data section was allocated in
     *******************************************************/
    private void loadProcess(int pID, ArrayList<Integer> codeFrames, ArrayList<Integer> dataFrames) {
        PCBEntry pcbEntry = new PCBEntry(pID);

        for (int i = 0; i < codeFrames.size(); i++) {
            freeFrames.remove(codeFrames.get(i));

            frameTable[codeFrames.get(i)] = new FrameTableEntry(pID, i, FrameTableEntry.FRAME_TYPE.CODE);
        }

        for (int i = 0; i < dataFrames.size(); i++) {
            freeFrames.remove(dataFrames.get(i));

            frameTable[dataFrames.get(i)] = new FrameTableEntry(pID, i, FrameTableEntry.FRAME_TYPE.DATA);
        }

        pcbEntry.addCodePages(codeFrames.size());
        pcbEntry.addDataPages(dataFrames.size());

        pcb.add(pcbEntry);
    }

    /*******************************************************
     * frees specified process from RAM
     *
     * @param pID process ID
     *******************************************************/
    private void freeProcess(int pID) {
        for (PCBEntry pcbEntry : pcb) {
            if (pcbEntry.getpID() == pID) {
                pcb.remove(pcbEntry);
                break;
            }
        }

        for (int i = 0; i < frameTable.length; i++) {
            if (frameTable[i] != null && frameTable[i].getpID() == pID) {
                frameTable[i] = null;
                freeFrames.add(i);
            }
        }
    }

    /*******************************************************
     * assigns code and data pages to physical frames in RAM
     *
     * @param p PCB entry for process
     * @param codePages number of code pages needed
     * @param dataPages number od data pages needed
     *******************************************************/
    private void assignPages(PCBEntry p, int codePages, int dataPages) {
        for (int i = 0; i < codePages; i++) {
            int nextFreeFrame = freeFrames.first();
            freeFrames.remove(nextFreeFrame);

            frameTable[nextFreeFrame] = new FrameTableEntry(p.getpID(), i, FrameTableEntry.FRAME_TYPE.CODE);
            historyStructures.get(instructionIndex - 1).addCodeFrame(nextFreeFrame);

            logStrings.add("\tProcess " + p.getpID() + ":\t loaded code page " + i + " to frame " + nextFreeFrame);
        }

        for (int i = 0; i < dataPages; i++) {
            int nextFreeFrame = freeFrames.first();
            freeFrames.remove(nextFreeFrame);

            frameTable[nextFreeFrame] = new FrameTableEntry(p.getpID(), i, FrameTableEntry.FRAME_TYPE.DATA);
            historyStructures.get(instructionIndex - 1).addDataFrame(nextFreeFrame);

            logStrings.add("\tProcess " + p.getpID() + ":\t loaded data page " + i + " to frame " + nextFreeFrame);
        }

        p.addCodePages(codePages);
        p.addDataPages(dataPages);
    }

    /*******************************************************
     * returns current status of each frame in physical memory
     *
     * @return ArrayList<String> status of each frame
     *******************************************************/
    public ArrayList<String> getNewTexts() {
        ArrayList<String> newTexts = new ArrayList<>();
        for (FrameTableEntry frameTableEntry : frameTable) {
            if (frameTableEntry == null) {
                newTexts.add("Free");
            } else {
                newTexts.add("Process " + frameTableEntry.getpID() + " -> " + (frameTableEntry.getFrameType() == FrameTableEntry.FRAME_TYPE.CODE ? "C" : "D") + frameTableEntry.getPageMapping());
            }
        }
        return newTexts;
    }

    /*******************************************************
     * returns whether all instructions have been executed
     *
     * @return boolean true if done, false if not
     *******************************************************/
    public boolean getDone() {
        return (instructionIndex >= instructions.size());
    }

    /*******************************************************
     * returns whether the current instruction is the first
     *
     * @return boolean true if first instruction, false if not
     *******************************************************/
    public boolean getFirst() {
        return (instructionIndex <= 0);
    }

    /*******************************************************
     * returns number of frames in physical memory
     *
     * @return int number of frames in physical memory
     *******************************************************/
    public int getNumFrames() {
        return RAM / PAGE_SIZE;
    }

    /*******************************************************
     * returns PCB
     *
     * @return LinkedList<PCBEntry> the PCB
     *******************************************************/
    public LinkedList<PCBEntry> getPcb() {
        return pcb;
    }

    /*******************************************************
     * returns the log strings of the kernel
     *
     * @return ArrayList<String> log strings of the kernel
     *******************************************************/
    public ArrayList<String> getLogStrings() {
        return logStrings;
    }
}
