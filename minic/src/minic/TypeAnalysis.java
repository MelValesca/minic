package minic;

import minic.language_minic.*;

import java.util.HashMap;
import java.util.Map;

public class TypeAnalysis extends Walker {

    ScopeAnalysis scopeAnalysis;

    public TypeAnalysis(ScopeAnalysis scopeAnalysis) {
        this.scopeAnalysis = scopeAnalysis;
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
}
