package minic.back;

import minic.ir.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/** Transform IR to RISC-V assembly.
 * <br>
 * IR must be legal for RISC-V assembly.
 * <br>
 * Quite dumb but does the following:
 * <li> Allocate stack and save registers.
 * <li> Assign parameters and return (form API registers aX)
 * <li> Setup arguments and result on call (from API register aX)
 * <li> Remove unneeded move from/to same machine register
 * <li> Remove unneeded jumps (fallthrough)
 **/
public class AsmEmitter {
    private Writer writer;

    public AsmEmitter(String filename) {
        try {
            writer = new FileWriter(filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void write(String string) {
        try {
            writer.write(string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BasicBlock currentBlock;
    private CFG currentCFG;

    public boolean hasCall(CFG cfg) {
        for (BasicBlock block : cfg.blocks)
            for (IR ir : block.instructions)
                if (ir.instr == Instr.Call)
                    return true;
        return false;
    }

    public void generate(CFG cfg) {
        currentCFG = cfg;
        emitAsm(".globl", cfg.function.name);
        write(cfg.function.name + ":\n");

        /* Compute the stack size */
        int nbRegisterToSave = 0;
        for (Register reg : cfg.registers) {
            if (reg.machineRegister >= nbRegisterToSave && reg.machineSaved)
                nbRegisterToSave = reg.machineRegister + 1;
        }
        boolean hasCall = hasCall(cfg);
        int stacksize = nbRegisterToSave * 8 + (hasCall?8:0);
        int stackpad = stacksize % 16;
        if (stackpad > 0) stacksize = (stacksize/16 + 1) * 16;
        //stacksize = -Math.floorDiv(-stacksize, 16) * 16; // hack for ceilDiv.

        /* Prologue */
        if (stacksize > 0)
            emitAsm("addi", "sp", "sp", String.valueOf(-stacksize));
        for(int i=0; i<nbRegisterToSave; i++)
            emitAsm("sd", "s"+i, (i*8+stackpad) +"(sp)");
        if (hasCall)
            emitAsm("sd", "ra", (nbRegisterToSave*8+stackpad) +"(sp)");

        // reorder (to ensure exit block is last)
        cfg.reorder();

        /* Body */
        for (BasicBlock block : cfg.blocks) {
            currentBlock = block;
            write(a(block) + ":\n");
            for (IR instruction : block.instructions)
                generate(instruction);
        }

        /* Epilogue */
        for(int i=0; i<nbRegisterToSave; i++)
            emitAsm("ld", "s"+i, (i*8+stackpad) +"(sp)");
        if(hasCall)
            emitAsm("ld", "ra", (nbRegisterToSave*8+stackpad) +"(sp)");
        if (stacksize > 0)
            emitAsm("addi", "sp", "sp", String.valueOf(stacksize));
        emitAsm("ret");
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Constant getSingleDefinition(Register reg) {
        IR ir = currentCFG.getSingleDef(reg);
        if (ir != null && ir.instr == Instr.Mv && ir.src[0] instanceof Constant constant) return constant;
        return null;
    }

    String a(Operand op) {
        return op.toMachine();
    }

    String a(BasicBlock block) {
        return "." + currentCFG.function.name + "." + (block.id);
    }

    void emitAsm(String name, String... ops) {
        write("\t");
        write(name);
        write("\t");
        boolean first = true;
        for (String op : ops) {
            if (first) first = false; else write(",");
            write(op);
        }
        write("\n");
    }

    void emitBinOp(String name, IR ir) {
        emitAsm(name, a(ir.dst), a(ir.src[0]), a(ir.src[1]));
    }

    void emitMv(String dst, String src) {
        if (dst.equals(src)) return; // skip superfluous move
        emitAsm("mv", dst, src);
    }

    void emitMv(String dst, Operand src) {
        String s = a(src);
        if (dst.equals(s)) return; // skip superfluous move
        emitAsm((src.isMachineImmediate() ? "li" : "mv"), dst, s);
    }

    void emitJ(BasicBlock dest) {
        if (currentBlock.nextLinearBlock() == dest) return; // skip superfluous jump
        emitAsm("j", a(dest));
    }

    void generate(IR ir) {
        switch (ir.instr) {
            case Add:
                emitBinOp(ir.src[1].isMachineImmediate() ? "addi" : "add", ir);
                break;

            case Sub:
                emitBinOp("sub", ir);
                break;

            case Mul:
                emitBinOp("mul", ir);
                break;

            case Lt:
                emitBinOp("slt", ir);
                break;

            case Mv:
                emitMv(a(ir.dst), ir.src[0]);
                break;

            case Iflt:
                emitAsm("bge", a(ir.src[0]), a(ir.src[1]), a(ir.labels[1]));
                emitJ(ir.labels[0]);
                break;

            case If:
                emitAsm("beqz", a(ir.src[0]), a(ir.labels[1]));
                emitJ(ir.labels[0]);
                break;

            case Jmp:
                emitJ(ir.labels[0]);
                break;

            case Ret:
                emitMv("a0", ir.src[0]);
                break;

            case Call:
                int i = 0;
                for (Operand op : ir.src) {
                    emitMv("a" + i, op);
                    i++;
                }
                write("\tcall " + ir.symbol + "\n");
                if (ir.dst != null) {
                    emitMv(a(ir.dst), "a0");
                }
                break;

            default:
                throw new RuntimeException("ICE: Unknown instr: " + ir.instr);
        }
    }
}