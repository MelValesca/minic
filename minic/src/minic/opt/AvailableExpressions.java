package minic.opt;

import minic.ir.*;
import minic.lib.Dataflow;

import java.util.*;

/** Classic available expression analysis */
public class AvailableExpressions extends Dataflow<IR> {

    // TODO Tâche 2.2, devoir 3

    public AvailableExpressions(CFG cfg) {
        super(cfg);
    }
}