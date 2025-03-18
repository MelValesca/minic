package minic.opt;

import minic.ir.*;
import minic.lib.Dataflow;

import java.util.*;

/** Classic very busy expression analysis. */
public class VeryBusyExpressions extends Dataflow<String> {

    List<String> expressions = new ArrayList<>();

    String expression(IR ir) {
        if (!ir.isArith()) return null;
        IR ir2 = ir.clone();
        ir2.dst = null;
        return ir2.toString();
    }

    public VeryBusyExpressions(CFG cfg) {
        super(cfg);
        must = true;
        backward = true;
        for (BasicBlock block : cfg.blocks) {
            for (IR ir : block.instructions) {
                String expr = expression(ir);
                if (expr == null) continue;
                if (!expressions.contains(expr))
                    expressions.add(expr);
            }
        }
    }

    @Override
    protected Collection<String> gen(BasicBlock block, IR ir) {
        Register def = ir.dst;
        if (def == null) return null;
        String expr = expression(ir);
        if (expr == null) return null;
        return Collections.singleton(expr);
    }

    @Override
    protected Collection<String> kill(BasicBlock block, IR ir) {
        Register dst = ir.dst;
        Collection<String> result = new ArrayList<>();
        for (IR use : cfg.getUses(dst)) {
            String expr = expression(use);
            if (expr == null) continue;
            result.add(expr);
        }
        return result;
    }
}