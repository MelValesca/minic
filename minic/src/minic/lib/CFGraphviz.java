package minic.lib;

import minic.ir.BasicBlock;
import minic.ir.CFG;
import minic.ir.IR;

import java.io.FileWriter;
import java.io.IOException;

/** Helper class to draw CFG for graphviz (dot) */
public class CFGraphviz {
    CFG cfg;
    public int instructionNumber;

    public CFGraphviz(CFG cfg) {
        this.cfg = cfg;
    }

    public static String escape(String s) {
        s = s.replaceAll("<", "&lt;");
        return s;
    }

    protected void writeNodes() {
        for (BasicBlock block : cfg.blocks)
            writeNode(block);
    }

    protected void writeNode(BasicBlock block) {
        write("Node" + block.id + "[shape=plaintext,label=<<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\"><TR><TD ALIGN=\"LEFT\">" + block + ":</TD></TR><TR><TD>");
        writeNodeContent(block);
        write("</TD></TR></TABLE>>,");
        writeNodeAttributes(block);
        write("];\n");
        writeEdges(block);
    }

    protected void writeNodeAttributes(BasicBlock block) {
    }

    protected void writeNodeContent(BasicBlock block) {
        for (IR instruction : block.instructions) {
            if (instruction.instr == null) continue;
            writeInstruction(block, instruction, instructionNumber);
            instructionNumber++;
        }
    }

    protected void writeInstruction(BasicBlock block, IR instruction, int number) {
        write("<FONT FACE=\"Latin Modern Mono\">" + String.format("%2d", number) + ": " + escape(instruction.toString()) + "</FONT><BR ALIGN=\"LEFT\"/>");
    }

    protected void writeEdge(BasicBlock from, BasicBlock to) {
        write("Node" + from.id + " -> Node" + to.id + "[label=<");
        writeEdgeLabel(from, to);
        write(">,");
        writeEdgeAttributes(from, to);
        write("];\n");
    }

    protected void writeEdges(BasicBlock block) {
        for (BasicBlock out : block.getOut())
            writeEdge(block, out);
    }

    protected void writeEdgeLabel(BasicBlock from, BasicBlock to) {
    }

    protected void writeEdgeAttributes(BasicBlock from, BasicBlock to) {
    }

    protected void write(String string) {
        try {
            fw.write(string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    FileWriter fw;

    public void dumpDot(String filename) {
        try {
            fw = new FileWriter(filename);
            instructionNumber = 0;
            fw.write("digraph {\n");
            fw.write("label=\"" + cfg.function.file + ": " + cfg.function.name + "()\";\n");
            writeNodes();
            fw.write("}\n");
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
