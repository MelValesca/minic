package minic.opt;

import minic.ir.*;
import minic.lib.Dataflow;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/** Classic reaching definitions analysis.
 * Can also compute use-def graphs.
 */
public class ReachingDefinitions extends Dataflow<IR> {

    private List<IR> instructions = new ArrayList<>();

    public ReachingDefinitions(CFG cfg) {
        super(cfg);
    }

    @Override
    public void process() {
        instructions.clear();
        for (BasicBlock block : cfg.blocks)
            instructions.addAll(block.instructions);
        super.process();
    }

    @Override
    public String stringOf(IR ir) {
        return instructions.indexOf(ir) + ":" + ir.dst;
    }

    @Override
    protected Collection<IR> gen(BasicBlock block, IR ir) {
        if (ir.dst == null) return null;
        return Collections.singleton(ir);
    }

    @Override
    protected Collection<IR> kill(BasicBlock block, IR ir) {
        if (ir.dst == null) return null;
        Collection<IR> result = new ArrayList<>();
        for (IR instruction : instructions)
            if (instruction != ir && instruction.dst == ir.dst)
                result.add(instruction);
        return result;
    }

    List<IR> uses(IR ir) {
        List<IR> uses = new ArrayList<>();
        Register dst = ir.dst;
        for (IR instruction : instructions) {
            if (!instruction.uses(dst)) continue;
            if (getIn(instruction).contains(ir))
                uses.add(instruction);
        }
        return uses;
    }

    List<IR> defs(IR ir, Register op) {
        List<IR> defs = new ArrayList<>();
        for (IR instruction : getIn(ir))
            if (instruction.dst == op)
                defs.add(instruction);
        return defs;
    }

    List<IR> defs(IR ir) {
        List<IR> defs = new ArrayList<>();
        for (Operand op : ir.src)
            if (op instanceof Register reg)
                defs.addAll(defs(ir, reg));
        return defs;
    }

    @Override
    public void dump(String prefix) {
        super.dump(prefix);
        dumpUseDefGraph(prefix + ".usedef.dot");
    }

    void dumpUseDefGraph(String filename) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write("digraph {\n");
            fw.write("node [shape=box];\n");
            fw.write("label=" + cfg.function.name + ";\n");
            for (int i=0; i<instructions.size(); i++) {
                IR ir = instructions.get(i);
                if (defs(ir).isEmpty() && uses(ir).isEmpty()) continue;
                if (ir.dst == null && (ir.src == null || ir.src.length==0)) continue;
                fw.write("Node" + i + "[label=\"" + i + ": " + ir + "\"];\n");
                for (IR use : uses(ir)) {
                    fw.write("Node" + i + "-> Node" + instructions.indexOf(use) + ";\n");
                }
                for (IR def : defs(ir)) {
                    //fw.write("Node" + instructions.indexOf(def) + "-> Node" + i + "[color=blue];\n");
                }
            }
            fw.write("}\n");
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}