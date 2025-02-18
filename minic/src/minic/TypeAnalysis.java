package minic;

import minic.language_minic.*;

import java.util.HashMap;
import java.util.Map;

public class TypeAnalysis extends Walker {

    ScopeAnalysis scopeAnalysis;

    public TypeAnalysis(ScopeAnalysis scopeAnalysis) {
        this.scopeAnalysis = scopeAnalysis;
    }

    Function currentFunction;

    @Override
    public void caseFun(NFun node) {
        Type returnType = getType(node.get_Type());
        Function function = scopeAnalysis.functions.get(node.get_Id().getText());
        currentFunction = function;
        function.returnType = returnType;
        super.caseFun(node);
        currentFunction = null;
    }

    Map<NExp, Type> expTypes = new HashMap<>();

    void visitExp(NExp node, Type type) {
        node.apply(this);
        Type expType = expTypes.get(node);
        if (expType != type)
            throw new RuntimeException("Type mismatch. Got "+expType+" but expected "+type);
    }

    void visitArithOp(NExp node, NExp left, NExp right) {
        visitExp(left, Type.Int);
        visitExp(right, Type.Int);
        expTypes.put(node, Type.Int);
    }

    @Override
    public void caseExp_Add(NExp_Add node) {
        visitArithOp(node, node.get_Left(), node.get_Right());
    }

    @Override
    public void caseExp_Sub(NExp_Sub node) {
        visitArithOp(node, node.get_Left(), node.get_Right());
    }

    @Override
    public void caseExp_Mul(NExp_Mul node) {
        visitArithOp(node, node.get_Left(), node.get_Right());
    }

    @Override
    public void caseExp_Lt(NExp_Lt node) {
        visitExp(node.get_Left(), Type.Int);
        visitExp(node.get_Right(), Type.Int);
        expTypes.put(node, Type.Bool);
    }

    @Override
    public void caseExp_Par(NExp_Par node) {
        super.caseExp_Par(node);
        expTypes.put(node, expTypes.get(node.get_Exp()));
    }

    @Override
    public void caseExp_Int(NExp_Int node) {
        expTypes.put(node, Type.Int);
    }

    @Override
    public void caseExp_True(NExp_True node) {
        expTypes.put(node, Type.Bool);
    }

    @Override
    public void caseExp_False(NExp_False node) {
        expTypes.put(node, Type.Bool);
    }

    Type getType(NType node) {
        switch (node.getType()) {
            case T_Type_Int: return Type.Int;
            case T_Type_Bool: return Type.Bool;
            default: throw new RuntimeException("ICE: unknown type: " + node.getType());
        }
    }

    @Override
    public void caseStmt_Var(NStmt_Var node) {
        Type type = getType(node.get_Type());
        visitExp(node.get_Exp(), type);
        Variable variable = scopeAnalysis.variables.get(node.get_Id());
        variable.type = type;
    }

    @Override
    public void caseExp_Var(NExp_Var node) {
        Variable variable = scopeAnalysis.variables.get(node.get_Id());
        expTypes.put(node, variable.type);
    }

    @Override
    public void caseStmt_Assign(NStmt_Assign node) {
        Variable variable = scopeAnalysis.variables.get(node.get_Id());
        visitExp(node.get_Exp(), variable.type);
    }

    @Override
    public void caseParam(NParam node) {
        Type type = getType(node.get_Type());
        Variable variable = scopeAnalysis.variables.get(node.get_Id());
        variable.type = type;
    }

    @Override
    public void caseStmt_If(NStmt_If node) {
        visitExp(node.get_Exp(), Type.Bool);
        node.get_Block().apply(this);
    }

    public void caseStmt_Ifelse(NStmt_Ifelse node) {
        visitExp(node.get_Exp(), Type.Bool);
        node.get_Block().apply(this);
        node.get_Else().apply(this);
    }

    public void caseStmt_While(NStmt_While node) {
        visitExp(node.get_Exp(), Type.Bool);
        node.get_Block().apply(this);
    }

    @Override
    public void caseStmt_Printint(NStmt_Printint node) {
        visitExp(node.get_Exp(), Type.Int);
    }

    public void caseStmt_Printbool(NStmt_Printbool node) {
        visitExp(node.get_Exp(), Type.Bool);
    }

    @Override
    public void caseExp_Call(NExp_Call node) {
        Function function = scopeAnalysis.functions.get(node.get_Id().getText());

        NArgs nargs = node.get_Args();
        switch(nargs.getType()) {
            case T_Args_Many: throw new RuntimeException("ICE: not implemented yet multiple args");
            case T_Args_One: {
                if (function.parameters.size() != 1)
                    throw new RuntimeException("error: expected " + function.parameters.size() + "parameter(s), got one.");
                visitExp(((NArgs_One) nargs).get_Exp(), function.parameters.get(0).type);
                break;
            }
            case T_Args_None: // nothing
                if (!function.parameters.isEmpty())
                    throw new RuntimeException("error: expected " + function.parameters.size() + "parameter(s), got none.");
                break;
            default: throw new RuntimeException("Unknown arg type: " + nargs.getType());
        }

        expTypes.put(node, function.returnType);
    }

    @Override
    public void caseStmt_Return(NStmt_Return node) {
        visitExp(node.get_Exp(), currentFunction.returnType);
    }
}
