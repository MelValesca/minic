package minic.lib;

import minic.back.Legalize;
import minic.back.RegisterAllocationKempe;
import minic.back.RegisterAllocationLinearScan;
import minic.front.Function;
import minic.ir.CFG;
import minic.opt.*;

/** A dumb pass manager. It operates function (CFG) per function. */
public class PassManager {
    /* Flags */
    public boolean opt1;
    public boolean kempe;
    public int repeat;
    public int subrepeat;
    public int ramax = 100;
    public boolean smallSteps;

    /* Analysis */
    ReachingDefinitions rd;
    VeryBusyExpressions vbe;
    AvailableExpressions ae;
    LiveVariables lv;

    /** Analysis are executed between transformation passes, because why not. */
    Pass[] getAnalysis(CFG cfg) {
        return new Pass[]{
                rd = new ReachingDefinitions(cfg),
                vbe = new VeryBusyExpressions(cfg),
                ae = new AvailableExpressions(cfg),
                lv = new LiveVariables(cfg),
        };
    }

    /** Transformation passes, in order. */
    Pass[] getTransformations(CFG cfg) {
        return new Pass[]{
                opt1 ? new CommonSubexpressionElimination(ae) : null,
                new Legalize(cfg),
                kempe ? new RegisterAllocationKempe(lv) : new RegisterAllocationLinearScan(lv, ramax),
        };
    }

    public void processPasses(CFG cfg) {
        Function function = cfg.function;
        String prefix = function.file + "." + function.name;

        cfg.dumpDot(prefix + ".00.cfg.dot");
        cfg.dump(prefix + "00.ir");
        //cfg.dumpDag(prefix + ".00.dag.dot");

        Pass[] allAnalysis = getAnalysis(cfg);
        Pass[] allPasses = getTransformations(cfg);

        int passNumber = 0;
        for(int r=0; r<=repeat; r++) {
            for (Pass pass : allPasses) {
                if (pass == null) continue;
                pass.smallSteps = smallSteps;

                boolean changed = false;
                do {
                    for (int sr = 0; sr <= subrepeat; sr++) {
                        int analysisNumber = 0;
                        for (Pass analyze : allAnalysis) {
                            analysisNumber++;
                            analyze.passNumber = passNumber;
                            analyze.prepare();
                            analyze.process();
                            if (analyze.changed()) {
                                analyze.dump(prefix + "." + String.format("%02d.%02d", passNumber, analysisNumber) + analyze.getClass().getSimpleName());
                                analyze.teardown();
                            }
                            analyze.check();
                        }

                        passNumber++;

                        pass.passNumber = passNumber;
                        pass.prepare();
                        pass.process();
                        changed = pass.changed();
                        String prefix2 = prefix + "." + String.format("%02d.00", passNumber) + pass.getClass().getSimpleName();
                        pass.dump(prefix2);
                        pass.teardown();
                        if (changed)
                            cfg.dump(prefix2 + ".ir");
                    }
                } while (smallSteps && changed);
            }
        }

        passNumber++;
        String prefix2 = prefix + "." + String.format("%02d", passNumber);
        cfg.dumpDot(prefix2 + ".cfg.dot");
        cfg.dump(prefix2 + ".ir");
    }
}