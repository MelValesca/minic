package minic;

import language_minic.*;
import minic.front.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/** Naive interpreter for a first MiniC specification. */
public class MiniC extends Walker {

	LitteralAnalysis litteralAnalysis = new LitteralAnalysis();
	ScopeAnalysis scopeAnalysis = new ScopeAnalysis();
	TypeAnalysis typeAnalysis = new TypeAnalysis(scopeAnalysis);

	/** Interpreter main method.
	 * Parse and evaluate each line from the standard input. */
	public static void main(String[] args) throws Exception {
		FileReader fr = new FileReader(args[0]);
		Parser parser = new Parser(fr);
		Node syntaxTree = parser.parse();
		MiniC interpreter = new MiniC();
		syntaxTree.apply(interpreter.litteralAnalysis);
		syntaxTree.apply(interpreter.scopeAnalysis);
		syntaxTree.apply(interpreter.typeAnalysis);
		Function main = interpreter.scopeAnalysis.functions.get("main");
		main.body.apply(interpreter);
	}

	/** Visit and evaluate a node.
	 * @param n the node to visit and evaluate.
	 * @return the evaluated value of n. */
	public int visitExp(Node n) {
		n.apply(this);
		return lastExpValue;
	}

	/** The value of the last visited expression.
	 * A visit of a node, caseXXX(), may assign this field. */
	private int lastExpValue;
	
	private final ArrayList<Integer> heap = new ArrayList<>();
	private final HashMap<Integer,Integer> blocks = new HashMap<>();

	private int malloc(int size) {
    		int base = heap.size();
    		for (int i = 0; i < size; i++) heap.add(0);
    		blocks.put(base, size);
    		return base;
	}

	private int  load (int addr)         {
		return heap.get(addr); 
	}

	private void store(int addr,int val) { 
		heap.set(addr, val);   
	}
	
	private void mfree(int base, boolean isArray) {
    		Integer sz = blocks.remove(base);
    		if (sz == null) throw new RuntimeException("pointeur invalide");
    		// Pour un delete simple, size doit être 1.
    		if (!isArray && sz != 1)
        		throw new RuntimeException("delete[] manquant pour un tableau");
    		if ( isArray && sz == 1)
        		throw new RuntimeException("delete[] sur pointeur scalaire");
		}

	/* NEW */

	@Override
	public void caseStmt_Memvar(NStmt_Memvar node) {           
    		Variable v = scopeAnalysis.variables.get(node.get_Id());
    		int addr   = malloc(1);
    		variables.put(v, addr);
	}

	@Override
	public void caseStmt_Tabassign(NStmt_Tabassign node) {
    		Variable v = scopeAnalysis.variables.get(node.get_Id());
    		int size   = litteralAnalysis.values.get(node.get_Int());
    		int addr   = malloc(size);
    		variables.put(v, addr);
	}

	/* DELETE */

	@Override
	public void caseStmt_Delete(NStmt_Delete node) {          
    		Variable v = scopeAnalysis.variables.get(node.get_Id());
    		int addr   = variables.get(v);
    		mfree(addr, false);
    		variables.put(v, 0);                                   
	}

	@Override
	public void caseStmt_Deletetab(NStmt_Deletetab node) {     
    		Variable v = scopeAnalysis.variables.get(node.get_Id());
    		int addr   = variables.get(v);
    		mfree(addr, true);
    		variables.put(v, 0);
	}

	/* DEREFERENCEMENT */
	
	@Override
	public void caseExp_Ptr(NExp_Ptr node) {
    		Variable v = scopeAnalysis.variables.get(node.get_Id());
    		int addr   = variables.get(v);
    		lastExpValue = load(addr);
	}

	@Override
	public void caseStmt_Ptrassign(NStmt_Ptrassign node) {
    		Variable v = scopeAnalysis.variables.get(node.get_Id());
    		int addr   = variables.get(v);
    		int val    = visitExp(node.get_Exp());
    		store(addr, val);
	}

	/* TABLEAU */

	@Override
	public void caseExp_Tabvar(NExp_Tabvar node) {
    		Variable v = scopeAnalysis.variables.get(node.get_Id());
   	 	int base   = variables.get(v);
    		int index  = litteralAnalysis.values.get(node.get_Int());
    		lastExpValue = load(base + index);
	}

	@Override
	public void caseStmt_Tabvar(NStmt_Tabvar node) {
    		Variable v = scopeAnalysis.variables.get(node.get_Id());
    		int base   = variables.get(v);
    		int index  = litteralAnalysis.values.get(node.get_Int());
    		int val    = visitExp(node.get_Exp());
    		store(base + index, val);
	}

	/* ADRESSE LOCALE */
	
	public void caseExp_Addr(NExp_Addr node) {
    		Variable v = scopeAnalysis.variables.get(node.get_Id());
    		Integer current = variables.get(v);
    		int addr;
    		if (current == null) {                   
        			addr = malloc(1);
        		variables.put(v, addr);              
        		store(addr, 0);                    
    		} else {
        		addr = current;                    
    		}
   	 	lastExpValue = addr;
	}

	@Override
	public void caseExp_Add(NExp_Add node) {
		int l = visitExp(node.get_Left());
		int r = visitExp(node.get_Right());
		lastExpValue = l + r;
	}

	@Override
	public void caseExp_Sub(NExp_Sub node) {
		int l = visitExp(node.get_Left());
		int r = visitExp(node.get_Right());
		lastExpValue = l - r;
	}

	@Override
	public void caseExp_Mul(NExp_Mul node) {
		int l = visitExp(node.get_Left());
		int r = visitExp(node.get_Right());
		lastExpValue = l * r;
	}

	@Override
	public void caseExp_Lt(NExp_Lt node) {
		int l = visitExp(node.get_Left());
		int r = visitExp(node.get_Right());
		lastExpValue = l < r ? 1 : 0;
	}

	@Override
	public void caseExp_Not(NExp_Not node) {
		int v = visitExp(node.get_Exp());
		lastExpValue = (v!=0) ? 0 : 1;
	}

	@Override
	public void caseExp_And(NExp_And node) {
		int left = visitExp(node.get_Left());
		if (left != 0) visitExp(node.get_Right());
	}

	@Override
	public void caseExp_Or(NExp_Or node) {
		int left = visitExp(node.get_Left());
		if (left == 0) visitExp(node.get_Right());
	}

	@Override
	public void caseInt(NInt node) {
		lastExpValue = litteralAnalysis.values.get(node);
	}

	@Override
	public void caseExp_True(NExp_True node) {
		lastExpValue = 1;
	}

	@Override
	public void caseExp_False(NExp_False node) {
		lastExpValue = 0;
	}

	private HashMap<Variable, Integer> variables = new HashMap<>();

	void setVariable(NId nid, int value) {
		Variable var = scopeAnalysis.variables.get(nid);
		variables.put(var, value);
	}

	@Override
	public void caseStmt_Assign(NStmt_Assign node) {
		int val = visitExp(node.get_Exp());
		setVariable(node.get_Id(), val);
	}

	@Override
	public void caseStmt_Var(NStmt_Var node) {
		int val = visitExp(node.get_Exp());
		setVariable(node.get_Id(), val);
	}

	@Override
	public void caseExp_Var(NExp_Var node) {
		Variable var = scopeAnalysis.variables.get(node.get_Id());
		lastExpValue = variables.get(var);
	}

	@Override
	public void caseStmt_If(NStmt_If node) {
		int cnd = visitExp(node.get_Exp());
		if (cnd != 0) {
			node.get_Block().apply(this);
		}
	}

	@Override
	public void caseStmt_Ifelse(NStmt_Ifelse node) {
		int cnd = visitExp(node.get_Exp());
		if (cnd != 0) {
			node.get_Block().apply(this);
		} else {
			node.get_Else().apply(this);
		}
	}

	@Override
	public void caseStmt_While(NStmt_While node) {
		for (;;) {
			int cnd = visitExp(node.get_Exp());
			if (cnd == 0) break;
			node.get_Block().apply(this);
		}
	}

	@Override
	public void caseStmt_Printint(NStmt_Printint node) {
		int val = visitExp(node.get_Exp());
		System.out.print(val);
	}

	@Override
	public void caseStmt_Printbool(NStmt_Printbool node) {
		int val = visitExp(node.get_Exp());
		System.out.print(val != 0 ? "true" : "false");
	}

	@Override
	public void caseStmt_Println(NStmt_Println node) {
		System.out.println();
	}

	void call(NId nid, NArgs nargs) {
		Function function = scopeAnalysis.functions.get(nid.getText());
		HashMap<Variable, Integer> oldFrame = variables;
		HashMap<Variable, Integer> newFrame = new HashMap<>();

		List<NExp> exps = scopeAnalysis.collectArgs(nargs);
		for (int i = 0; i < exps.size(); i++) {
			int value = visitExp(exps.get(i));
			Variable param = function.parameters.get(i);
			newFrame.put(param, value);
		}

		variables = newFrame;
		try {
			function.body.apply(this);
			lastExpValue = 0;
		} catch (ReturnLongJump e) {}
		variables = oldFrame;
	}

	@Override
	public void caseStmt_Call(NStmt_Call node) {
		call(node.get_Id(), node.get_Args());
	}

	@Override
	public void caseExp_Call(NExp_Call node) {
		call(node.get_Id(), node.get_Args());
	}

	@Override
	public void caseStmt_Return(NStmt_Return node) {
		visitExp(node.get_Exp());
		throw new ReturnLongJump();
	}

	private static class ReturnLongJump extends RuntimeException {}

	// NOTE: caseExp_Int and caseExp_Par are not overridden since the default behavior
	// is to evaluate their subtree which correctly assigns lastExpValue
}
