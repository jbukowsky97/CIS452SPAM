import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;

public class Kernel {

    /* ram size */
    private final int RAM;

    /* page/frame size */
    private final int PAGE_SIZE;

    /* linked list of processes running */
    private LinkedList<PCBEntry> pcb;

    /* tree set of free frame indexes */
    private TreeSet<Integer> freeFrames;

    /* index of frame with value of PID it is used by */
    private FrameTableEntry[] frameTable;

    private ArrayList<String> instructions;
    private ArrayList<HistoryStructure> historyStructures;

    private ArrayList<String> logStrings;

    private int instructionIndex;

    {
        RAM = 4096;
        PAGE_SIZE = 512;

        instructionIndex = 0;

        pcb = new LinkedList<>();
        freeFrames = new TreeSet<>();
        historyStructures = new ArrayList<>();
        logStrings = new ArrayList<>();

        frameTable = new FrameTableEntry[RAM / PAGE_SIZE];

        for (int i = 0; i < frameTable.length; i++) {
            freeFrames.add(i);
            frameTable[i] = null;
        }
    }

    public Kernel(ArrayList<String> instructions) {
        this.instructions = instructions;
    }

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

    private void loadProcess(int pID, int codeSize, int dataSize) {
        int codePages = (codeSize + PAGE_SIZE - 1) / PAGE_SIZE;
        int dataPages = (dataSize + PAGE_SIZE - 1) / PAGE_SIZE;
        PCBEntry pcbEntry = new PCBEntry(pID);

        logStrings.add("Loading program " + pID + " into RAM: code=" + codeSize + " (" + codePages + " pages), data=" + dataSize + " (" + dataPages + " pages)");

        this.assignPages(pcbEntry, codePages, dataPages);
        pcb.add(pcbEntry);

        logStrings.add("Program " + pID + " loaded");
    }

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

    public boolean getDone() {
        return (instructionIndex >= instructions.size());
    }

    public boolean getFirst() {
        return (instructionIndex <= 0);
    }

    public int getNumFrames() {
        return RAM / PAGE_SIZE;
    }

    public LinkedList<PCBEntry> getPcb() {
        return pcb;
    }

    public ArrayList<String> getLogStrings() {
        return logStrings;
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

        new Kernel(instructions);
    }
}
