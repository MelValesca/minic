package minic.opt;

import minic.ir.*;
import minic.lib.Pass;

/** Classic Common Subexpression Elimination transformation. */
public class CommonSubexpressionElimination extends Pass {

    AvailableExpressions availableExpressions;

    public CommonSubexpressionElimination(AvailableExpressions availableExpressions) {
        super(availableExpressions.cfg);
        this.availableExpressions = availableExpressions;
    }

    @Override
    public void process() {
        throw new RuntimeException("ICE TODO: Tâche 2.2, Devoir 3.");
    }
}
