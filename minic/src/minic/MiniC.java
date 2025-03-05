package minic;

import language_minic.*;
import minic.front.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;

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
