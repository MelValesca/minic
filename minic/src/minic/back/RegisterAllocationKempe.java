package minic.back;

import minic.ir.*;
import minic.lib.Pass;
import minic.opt.LiveVariables;

/** Register allocation using Kempe heuristic. */
public class RegisterAllocationKempe extends Pass {
    LiveVariables liveVariables;

    public RegisterAllocationKempe(LiveVariables liveVariables) {
        super(liveVariables.cfg);
        this.liveVariables = liveVariables;
    }

    public void process() {
        throw new RuntimeException("ICE: TODO: Tâche 3.1, devoir 3. Et p'tet tâche 4.4.");
    }

    public void dump(String prefix) {
        super.dump(prefix);
        liveVariables.dumpInterference(prefix + ".coloring.dot");
    }
}