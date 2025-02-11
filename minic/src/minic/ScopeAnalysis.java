package minic;

import minic.language_minic.*;

import java.util.HashMap;
import java.util.Map;

public class ScopeAnalysis extends Walker {

    Scope currentScope;

    @Override
    public void caseBlock(NBlock node) {
        currentScope = new Scope(currentScope);
        super.caseBlock(node);
        currentScope = currentScope.prev;
    }

    Map<NId, Variable> variables = new HashMap<>();

    @Override
    public void inStmt_Var(NStmt_Var node) {
        String name = node.get_Id().getText();
        Variable variable = new Variable(name, node);
        currentScope.vars.put(name, variable);
        variables.put(node.get_Id(), variable);
    }

    void getVar(NId nid) {
        Variable variable = currentScope.lookup(nid.getText());
        if (variable == null)
            throw new RuntimeException("Variable '" + nid.getText() + "' not found");
        variables.put(nid, variable);
    }

    @Override
    public void caseExp_Var(NExp_Var node) {
        getVar(node.get_Id());
    }

    @Override
    public void inStmt_Assign(NStmt_Assign node) {
        getVar(node.get_Id());
    }

    static class Scope {
        Scope prev;
        Map<String, Variable> vars = new HashMap<String, Variable>();
        Scope(Scope prev) {
            this.prev = prev;
        }

        Variable lookup(String name) {
            Variable var = vars.get(name);
            if (var == null && prev != null) {
                var = prev.lookup(name);
            }
            return var;
        }
        @Override
        public String toString() {
            String res = vars.toString();
            if (prev != null) res = res + super.toString();
            return res;
        }
    }

}
