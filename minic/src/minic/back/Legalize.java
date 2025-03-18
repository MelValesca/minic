package minic.back;

import minic.ir.*;
import minic.lib.Pass;

import java.util.ListIterator;

/** Pass that transform or extract IR operands and instructions to make them valid for a dumb IR->RISC-V translation. */
public class Legalize extends Pass {

    public Legalize(CFG cfg) {
        super(cfg);
    }

    void unfold(ListIterator<IR> iir, IR ir, int idx) {
        Operand op = ir.src[idx];
        if (op instanceof Constant cst) {
            if (cst.value == 0) return;
            Register reg = cfg.newRegister("");
            iir.previous();
            IR mv = new IR(Instr.Mv, reg, cst);
            mv.setNew();
            iir.add(mv);
            iir.next();
            ir.replaceSrc(cst, reg);
        }
    }

    void unfold12bits(ListIterator<IR> iir, IR ir, int idx) {
        Operand op = ir.src[idx];
        if (op instanceof Constant cst) {
            int val = cst.value;
            if (cst.value == 0) return;
            if (val > 2047 || val < -2048)
                unfold(iir, ir, idx);
        }
    }

    public void process() {
        for (BasicBlock block : cfg.blocks) {
            for (ListIterator<IR> iir = block.instructions.listIterator(); iir.hasNext(); ) {
                IR ir = iir.next();

                switch (ir.instr) {
                    case Sub:
                        if (ir.src[1] instanceof Constant con) {
                            ir.replaceSrc(ir.src[1], new Constant(-con.value));
                            ir.instr = Instr.Add;
                        }
                        // not break
                    case Add:
                        unfold(iir, ir, 0);
                        unfold12bits(iir, ir, 1);
                        break;
                    case Mul:
                    case Lt:
                    case Iflt:
                        unfold(iir, ir, 0);
                        unfold(iir, ir, 1);
                        break;
                    case If:
                        unfold(iir, ir, 0);
                        break;
                }
            }
        }
    }
}