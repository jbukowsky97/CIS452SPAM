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
    private int instructionIndex;

    {
        RAM = 4096;
        PAGE_SIZE = 512;

        instructionIndex = 0;

        pcb = new LinkedList<>();
        freeFrames = new TreeSet<>();

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
        if (!instruction.contains("-1")) {
            int pID = Integer.parseInt(instruction.substring(0, instruction.indexOf(" ")));
            instruction = instruction.substring(instruction.indexOf(" ") + 1);
            int codeSize = Integer.parseInt(instruction.substring(0, instruction.indexOf(" ")));
            instruction = instruction.substring(instruction.indexOf(" ") + 1);
            int dataSize = Integer.parseInt(instruction);

            this.loadProcess(pID, codeSize, dataSize);
        } else {
            int pID = Integer.parseInt(instruction.substring(0, instruction.indexOf(" ")));
        }
    }

    private void loadProcess(int pID, int codeSize, int dataSize) {
        int codePages = (codeSize + PAGE_SIZE - 1) / PAGE_SIZE;
        int dataPages = (dataSize + PAGE_SIZE - 1) / PAGE_SIZE;
        PCBEntry pcbEntry = new PCBEntry(pID);
        this.assignPages(pcbEntry, codePages, dataPages);
        pcb.add(pcbEntry);
    }

    private void assignPages(PCBEntry p, int codePages, int dataPages) {
        for (int i = 0; i < codePages; i++) {
            int nextFreeFrame = freeFrames.first();
            freeFrames.remove(nextFreeFrame);
            System.out.println("getting next frame : " + nextFreeFrame);

            frameTable[nextFreeFrame] = new FrameTableEntry(p.getpID(), i, FrameTableEntry.FRAME_TYPE.CODE);
        }

        for (int i = 0; i < dataPages; i++) {
            int nextFreeFrame = freeFrames.first();
            freeFrames.remove(nextFreeFrame);
            System.out.println("getting next frame : " + nextFreeFrame);

            frameTable[nextFreeFrame] = new FrameTableEntry(p.getpID(), i, FrameTableEntry.FRAME_TYPE.DATA);
        }

        p.addCodePages(codePages);
        p.addDataPages(dataPages);
    }

    public int getNumFrames() {
        return RAM / PAGE_SIZE;
    }

    public LinkedList<PCBEntry> getPcb() {
        return pcb;
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
