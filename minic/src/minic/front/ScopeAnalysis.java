package minic.front;

import language_minic.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScopeAnalysis extends Walker {

    private Scope currentScope;

    public Map<String, Function> functions = new HashMap<String, Function>();

    private Function currentFunction;

    @Override
    public void caseFun(NFun node) {
        String name = node.get_Id().getText();
        Function function = new Function(name, node.get_Block());
        currentFunction = function;
        functions.put(name, function);
        currentScope = new Scope(currentScope);
        super.caseFun(node);
        currentScope = currentScope.prev;
        currentFunction = null;
    }

    @Override
    public void caseBlock(NBlock node) {
        currentScope = new Scope(currentScope);
        super.caseBlock(node);
        currentScope = currentScope.prev;
    }

    public Map<NId, Variable> variables = new HashMap<>();

    @Override
    public void caseParam(NParam node) {
        String name = node.get_Id().getText();
        Variable variable = new Variable(name, node);
        currentScope.vars.put(name, variable);
        variables.put(node.get_Id(), variable);
        currentFunction.parameters.add(variable);
    }

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

    public List<NExp> collectArgs(NArgs nargs) {
        List<NExp> result = new ArrayList<>();
        for (;;) {
            switch (nargs.getType()) {
                case T_Args_None:
                    return result;
                case T_Args_One:
                    result.add(((NArgs_One)nargs).get_Exp());
                    return result;
                case T_Args_Many:
                    result.add(((NArgs_Many)nargs).get_Exp());
                    nargs = ((NArgs_Many)nargs).get_Args();
                    break;
                default:
                    throw new RuntimeException("ICE: Unexpected arg type: " + nargs);
            }
        }
    }
}
