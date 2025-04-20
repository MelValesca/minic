package minic.front;

import language_minic.*;

import java.util.HashMap;
import java.util.List;
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
	

    /* POINTEURS ET TABLEAUX */

    @Override
    public void caseExp_Ptr(NExp_Ptr node) {
        Variable v = scopeAnalysis.variables.get(node.get_Id());
        if (!v.type.isPtr())
            throw new RuntimeException("'*' appliqué à une variable non‑pointeur");
        expTypes.put(node, v.type.deref());
    }

    @Override
    public void caseExp_Addr(NExp_Addr node) {
        Variable v = scopeAnalysis.variables.get(node.get_Id());
        if (v.type == Type.Int)
            expTypes.put(node, Type.IntPtrScalar);
        else if (v.type == Type.Bool)
            expTypes.put(node, Type.BoolPtrScalar);
        else
            throw new RuntimeException("adresse d'un pointeur déjà !");
    }

    @Override
    public void caseStmt_Ptrassign(NStmt_Ptrassign node) {
        Variable v = scopeAnalysis.variables.get(node.get_Id());
        if (!v.type.isPtr())
            throw new RuntimeException("l‑value n’est pas un pointeur");
        visitExp(node.get_Exp(), v.type.deref());
    }

    @Override
    public void caseStmt_Memvar(NStmt_Memvar node) {
        Type lhs = getType(node.get_Typeptr()); 
        Variable var = scopeAnalysis.variables.get(node.get_Id());
        var.type = lhs;    
    }

    @Override
    public void caseStmt_Tabassign(NStmt_Tabassign node) {
        Type lhs = getType(node.get_Typeptr());
	lhs = (lhs == Type.IntPtrScalar) ? Type.IntPtrArray : Type.BoolPtrArray;
        Variable var = scopeAnalysis.variables.get(node.get_Id());
        var.type = lhs;
    }

    @Override
    public void caseExp_Tabvar(NExp_Tabvar node) {
        Variable v = scopeAnalysis.variables.get(node.get_Id());
        if (!v.type.isPtr())
            throw new RuntimeException("indexation sur non‑tableau");
        expTypes.put(node, v.type.deref());      
    }

    @Override
    public void caseStmt_Tabvar(NStmt_Tabvar node) {
        Variable v = scopeAnalysis.variables.get(node.get_Id());
        if (!v.type.isPtr())
            throw new RuntimeException("indexation sur non‑tableau");
        visitExp(node.get_Exp(), v.type.deref());
    }

    @Override
    public void caseStmt_Delete(NStmt_Delete node) {
        Variable v = scopeAnalysis.variables.get(node.get_Id());
        if (!v.type.isPtr())
	    throw new RuntimeException("delete sur non-pointeur");
	if (!v.type.isScalarPtr())
            throw new RuntimeException("delete[] requis pour ce bloc");
    }

    @Override
    public void caseStmt_Deletetab(NStmt_Deletetab node) {
        Variable v = scopeAnalysis.variables.get(node.get_Id());
	if (!v.type.isPtr())
	    throw new RuntimeException("delete[] sur non-pointeur");
        if (!v.type.isArrayPtr())
            throw new RuntimeException("delete[] sur pointeur scalaire");
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
    public void caseExp_Not(NExp_Not node) {
        visitExp(node.get_Exp(), Type.Bool);
        expTypes.put(node, Type.Bool);
    }

    @Override
    public void caseExp_And(NExp_And node) {
        visitExp(node.get_Left(), Type.Bool);
        visitExp(node.get_Right(), Type.Bool);
        expTypes.put(node, Type.Bool);
    }

    @Override
    public void caseExp_Or(NExp_Or node) {
        visitExp(node.get_Left(), Type.Bool);
        visitExp(node.get_Right(), Type.Bool);
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

    Type getType(NTypeptr node) {
        switch (node.getType()) {       
            case T_Typeptr_Ptrint:	return Type.IntPtrScalar;
            case T_Typeptr_Ptrbool:	return Type.BoolPtrScalar;
	    default:
                throw new RuntimeException("ICE: unknown typeptr: " + node.getType());
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

        List<NExp> nargs = scopeAnalysis.collectArgs(node.get_Args());
        if (nargs.size() !=  function.parameters.size())
            throw new RuntimeException("error: expected " + function.parameters.size() + "parameter(s), got " + nargs.size());
        for (int i = 0; i < function.parameters.size(); i++) {
            visitExp(nargs.get(i), function.parameters.get(i).type);
        }

        expTypes.put(node, function.returnType);
    }

    @Override
    public void caseStmt_Return(NStmt_Return node) {
        visitExp(node.get_Exp(), currentFunction.returnType);
    }
}
