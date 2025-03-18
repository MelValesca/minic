package minic.opt;

import minic.ir.*;
import minic.lib.Dataflow;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/** Classic live variable analysis.
 * Can also compute the variable interference graph. */
public class LiveVariables extends Dataflow<Register> {
    public LiveVariables(CFG cfg) {
        super(cfg);
        backward = true;
    }

    @Override
    protected Collection<Register> gen(BasicBlock block, IR ir) {
        Operand[] uses = ir.src;
        if (uses == null) return null;
        Collection<Register> result = new ArrayList<>();
        for (Operand op : uses)
            if (op instanceof Register reg)
                result.add(reg);
        return result;
    }

    @Override
    protected Collection<Register> kill(BasicBlock block, IR ir) {
        Register dst = ir.dst;
        if (dst == null) return null;
        return Collections.singleton(dst);
    }

    public boolean needSavingAt(IR ir, Register reg) {
        if (ir.instr != Instr.Call) return false;
        if (reg == ir.dst) return false;
        if (getOut(ir).contains(reg)) return true;
        return false;
    }

    public boolean needSaving(Register reg) {
        for (BasicBlock block : cfg.blocks)
            for (IR ir : block.instructions)
                if (needSavingAt(ir, reg)) return true;
        return false;
    }

    public List<Register> liveRegistersAt(IR ir) {
        List<Register> regs = new ArrayList<>();
        for (Register reg : cfg.registers)
            if (isLiveAt(ir, reg))
                regs.add(reg);
        return regs;
    }

    public boolean isLiveAt(IR ir, Register reg) {
        if (getIn(ir).contains(reg)) return true;
        if (reg == ir.dst && !getOut(ir).contains(reg)) return true; // hack
        return false;
    }

    public List<Register> interferenceRegisters(Register reg) {
        List<Register> regs = new ArrayList<>();
        for (BasicBlock block : cfg.blocks)
            for (IR ir : block.instructions)
                if (isLiveAt(ir, reg))
                    for (Register candidate : liveRegistersAt(ir))
                        if (reg != candidate && !regs.contains(candidate))
                            regs.add(candidate);
        return regs;
    }

    public void dumpInterference(String filename) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write("graph {\n");
            fw.write("node [colorscheme=set312,style=filled];\n");
            fw.write("label=" + cfg.function.name + ";\n");
            int i = 0;
            for (Register reg : cfg.registers) {
                i++;
                List<Register> conflicts = interferenceRegisters(reg);
                fw.write("N" + reg.id + "[label=\"" + i + ". " + reg + "\\nd:" + conflicts.size() +
                        (reg.machineSaved ? " sav" : "") +
                        "\"" +
                        (reg.machineRegister>=0 ? ",fillcolor=" + (reg.machineRegister+1) + ",colorscheme=" + (reg.machineSaved ? "pastel19" : "set312") : "" ) +
                        "];\n");
                for (Register conflict : conflicts) {
                    if (reg.id < conflict.id)
                        fw.write("N"+reg.id+"--N"+conflict.id + "\n");
                }
            }
            fw.write("}\n");
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dump(String prefix) {
        super.dump(prefix);
        dumpInterference(prefix + ".interference.dot");
    }
}