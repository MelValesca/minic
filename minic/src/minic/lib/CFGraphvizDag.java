package minic.lib;

import minic.ir.*;

import java.util.HashMap;

public class CFGraphvizDag extends CFGraphviz {

    public CFGraphvizDag(CFG cfg) {
        super(cfg);
    }

    @Override
    public void writeNodes() {
        write("compound=true;\n");
        for (BasicBlock block : cfg.blocks) {
            write("subgraph cluster_" + block.id + " {\n");
            write("label=\"" + block + "\";\n");
            writeNode(block);
            write("}\n");
        }
        for (BasicBlock block : cfg.blocks) {
            for (BasicBlock next : block.getOut()) {
                write("I" + lastNumber.get(block) + " -> I" + firstNumber.get(next) + ";\n");
            }
        }
    }

    int globalNumbering = 0;
    HashMap<BasicBlock, Integer> firstNumber = new HashMap<>();
    HashMap<BasicBlock, Integer> lastNumber = new HashMap<>();

    @Override
    public void writeNode(BasicBlock block) {
        HashMap<Register, Integer> numbering = new HashMap<>();
        int last = -1;
        HashMap<Register, IR> lastDefs = new HashMap<>();
        for (IR ir : block.instructions) {
            Register dst = ir.dst;
            if (dst != null && cfg.getSingleDef(dst) == null)
               lastDefs.put(ir.dst, ir);
        }
        for (IR ir : block.instructions) {
            if (ir.instr == null) continue;
            globalNumbering++;
            int i = globalNumbering;

            String shape;
            if (lastDefs.get(ir.dst) == ir)
                shape="box";
            else
                shape="plaintext";

            write("I" + i + "[shape="+shape+",label=\"" + ir + "\",ordering=out];\n");
            if (!ir.isPure()) {
                if (last>=0) {
                    write("I" + last + " -> I" + i + "[constraint=false];\n");
                } else {
                    firstNumber.put(block, i);
                }
                last = i;
                lastNumber.put(block, i);
            }
            for (Operand op : ir.src) {
                if (op instanceof Register reg) {
                    Integer num = numbering.get(reg);
                    if (num == null) {
                        globalNumbering++;
                        numbering.put(reg, globalNumbering);
                        num = globalNumbering;
                        write("I" + globalNumbering + "[shape=box,label=\"" + reg + "\"];\n");
                    }
                    write("I" + i + " -> " + "I" + num + "[arrowhead=none];\n");
                } else {
                    /*globalNumbering++;
                    write("O" + globalNumbering + "[shape=plaintext,label=\"" + op + "\"];\n");
                    write("I" + i + " -> " + "O" + globalNumbering + "[arrowhead=none];\n");*/
                }
            }
            Register dst = ir.dst;
            if (dst != null) {
                numbering.put(dst, i);
                /*
                String shape;
                if (lastDefs.get(dst) == ir)
                    shape="circle";
                else
                    shape="plaintext";

                write("R" + globalNumbering + "[shape=" + shape + ",label=\"" + dst + "\"];\n");
                write("R" + globalNumbering + " -> " + "I" + i + "[arrowhead=none];\n");

                 */
            }
        }
    }
}
