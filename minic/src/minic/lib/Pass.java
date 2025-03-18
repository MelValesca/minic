package minic.lib;

import minic.ir.*;

import java.util.*;

/** Base off all passes.
 * The main entry point is {@link #process()}.
 * Most of the code here is to produce nice and fancy CFG.
 */
public class Pass {
    public CFG cfg;
    public Pass(CFG cfg) {
        this.cfg = cfg;
    }

    public void prepare() {
        oldBlocks = new ArrayList<>(cfg.blocks);
    }

    public void process() {}

    public boolean smallSteps = false;

    public int passNumber;
    boolean someLog = false;
    public void log(String msg) {
        if (!someLog) {
            System.err.println(" *** " + cfg.function.name + ": " + passNumber + " " + getClass().getSimpleName());
            someLog = true;
        }
        System.err.println(msg);
    }

    /** Check service on analysis */
    public void check() {}

    public void dump(String prefix) {
        if (changed()) {
            dumpCFG(prefix + ".cfg.dot");
            if(CFGchanged())
                dumpDiff(prefix + ".cfg.diff.dot");
            //cfg.dumpDag(prefix + ".dag.dot");
        }
    }

    public boolean changed() {
        return CFGchanged();
    }

    public boolean CFGchanged() {
        if (!oldBlocks.equals(cfg.blocks)) return true;
        for (BasicBlock block : cfg.blocks)
            for (IR ir : block.instructions)
                if (ir.instr == null || ir.was != null)
                    return true;
        return false;
    }

    public void dumpCFG(String filename) {
        cfg.dumpDot(filename);
    }

    public void dumpDiff(String filename) {
        List<BasicBlock> newBlocks = new ArrayList<>(cfg.blocks);
        newBlocks.removeAll(oldBlocks);
        oldBlocks.removeAll(cfg.blocks);
        CFGraphviz vis = new CFGraphviz(cfg) {
            @Override
            protected void writeInstruction(BasicBlock block, IR instruction, int number) {
                IR was = instruction.was;
                boolean blue = instruction.same(was);

                if (was != null && was.instr != null && !blue) {
                    write("<S><FONT COLOR=\"darkred\">");
                    super.writeInstruction(block, was, number);
                    write("</FONT></S>");
                }
                if (instruction.instr != null) {
                    if (blue)
                        write("<FONT COLOR=\"darkblue\">");
                    else if (was != null)
                        write("<B><FONT COLOR=\"darkgreen\">");
                    super.writeInstruction(block, instruction, number);
                    if (blue)
                        write("</FONT>");
                    else if (was != null)
                        write("</FONT></B>");
                }
            }

            @Override
            protected void writeNodeContent(BasicBlock block) {
                for (IR instruction : block.instructions) {
                    writeInstruction(block, instruction, instructionNumber);
                    instructionNumber++;
                }
            }

            @Override
            protected void writeNodes() {
                super.writeNodes();
                for (BasicBlock block : oldBlocks)
                    if (!cfg.blocks.contains(block))
                        writeNode(block);
            }

            @Override
            protected void writeNodeAttributes(BasicBlock block) {
                if (oldBlocks.contains(block))
                    write("color=darkred;fontcolor=darkred;style=dashed");
                if (newBlocks.contains(block))
                    write("color=darkgreen;fontcolor=darkgreen;style=bold");
            }

            @Override
            protected void writeEdges(BasicBlock block) {
                super.writeEdges(block);
                IR was = block.getTerminator().was;
                if (was == null || was.labels == null) return;
                List<BasicBlock> outBlocks = block.getOut();
                for (BasicBlock out : was.labels)
                    if (!outBlocks.contains(out))
                        super.writeEdge(block, out);
            }

            @Override
            protected void writeEdgeAttributes(BasicBlock from, BasicBlock to) {
                if (!from.getOut().contains(to) || oldBlocks.contains(from)) {
                    write("color=darkred,style=dashed");
                    return;
                }
                if (newBlocks.contains(from) || newBlocks.contains(to)) {
                    write("color=darkgreen");
                    return;
                }
                IR was = from.getTerminator().was;
                if (was == null || was.labels == null) return;
                List<BasicBlock> outBlocks = Arrays.asList(was.labels);
                if (!outBlocks.contains(to))
                    write("color=darkgreen,style=bold");
            }
        };
        vis.dumpDot(filename);
    }

    public void teardown() {
        for (BasicBlock block : cfg.blocks) {
            for(ListIterator<IR> iir = block.instructions.listIterator(); iir.hasNext();) {
                IR ir = iir.next();
                ir.was = null;
                if (ir.instr == null)
                    iir.remove();
            }
        }
        oldBlocks = new ArrayList<>(cfg.blocks);
        someLog = false;
    }

    List<BasicBlock> oldBlocks;
}