package minic;

import language_minic.*;
import minic.front.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;

/** Naive interpreter for a first MiniC specification. */
public class MiniCC extends Walker {

	LitteralAnalysis litteralAnalysis = new LitteralAnalysis();
	ScopeAnalysis scopeAnalysis = new ScopeAnalysis();
	TypeAnalysis typeAnalysis = new TypeAnalysis(scopeAnalysis);

	Writer writer;

	/** Compiler main method.
	 * Parse and evaluate each line from the standard input. */
	public static void main(String[] args) throws Exception {
		FileReader fr = new FileReader(args[0]);
		Parser parser = new Parser(fr);
		Node syntaxTree = parser.parse();
		MiniCC compiler = new MiniCC();
		syntaxTree.apply(compiler.litteralAnalysis);
		syntaxTree.apply(compiler.scopeAnalysis);
		syntaxTree.apply(compiler.typeAnalysis);

		compiler.writer = new FileWriter("minicc.out.s");
		syntaxTree.apply(compiler);
		compiler.writer.close();
	}

	void write(String string) {
        try {
            writer.write(string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	String currentRetLabel;

	@Override
	public void caseFun(NFun node) {
		String label = node.get_Id().getText();
		currentRetLabel = "." + label + ".ret";
		write("\t.globl " + label + "\n");
		write(label + ":\n");
		write("\taddi sp, sp, -112\n");
		write("\tsd ra, 0(sp)\n");
		for(int i=0; i<12; i++)
			write("\tsd s" + i + ", " + ((i*8)+8) + "(sp)\n");
		super.caseFun(node);

		write("\tli a0, 0\n");
		write(currentRetLabel + ":\n");
		write("\tld ra, 0(sp)\n");
		for(int i=0; i<12; i++)
			write("\tld s" + i + ", " + ((i*8)+8) + "(sp)\n");
		write("\taddi sp, sp, 112\n");
		write("\tret\n");
		currentRetLabel = null;
	}

	@Override
	public void caseParam(NParam node) {
		Variable var = scopeAnalysis.variables.get(node.get_Id());
		variables.put(var, lastRegister);
		write("\tmv s" + lastRegister + ", a" + lastRegister + "\n");
		lastRegister++;
	}

	@Override
	public void caseBlock(NBlock node) {
		int oldLastRegister = lastRegister;
		super.caseBlock(node);
		lastRegister = oldLastRegister;
	}

	public int visitExp(Node n) {
		n.apply(this);
		return lastRegister;
	}

	private int lastRegister;

	@Override
	public void caseExp_Add(NExp_Add node) {
		int l = visitExp(node.get_Left());
		lastRegister++;
		int r = visitExp(node.get_Right());
		lastRegister--;
		write("\tadd s" + lastRegister + ", s" + l + ", s" + r + "\n");
	}

	@Override
	public void caseExp_Sub(NExp_Sub node) {
		int l = visitExp(node.get_Left());
		lastRegister++;
		int r = visitExp(node.get_Right());
		lastRegister--;
		write("\tsub s" + lastRegister + ", s" + l + ", s" + r + "\n");
	}

	@Override
	public void caseExp_Mul(NExp_Mul node) {
		int l = visitExp(node.get_Left());
		lastRegister++;
		int r = visitExp(node.get_Right());
		lastRegister--;
		write("\tmul s" + lastRegister + ", s" + l + ", s" + r + "\n");
	}

	@Override
	public void caseExp_Lt(NExp_Lt node) {
		int l = visitExp(node.get_Left());
		lastRegister++;
		int r = visitExp(node.get_Right());
		lastRegister--;
		write("\tslt s" + lastRegister + ", s" + l + ", s" + r + "\n");
	}

	@Override
	public void caseExp_Not(NExp_Not node) {
		int r = visitExp(node.get_Exp());
		write("\tsbeqz " + r + ", s" + r + "\n");
	}

	@Override
	public void caseExp_And(NExp_And node) {
		int l = visitExp(node.get_Left());
		String label = genLabel();
		write("\tbeqz s" + l + ", " + label + "\n");
		visitExp(node.get_Right());
		write(label + ":\n");
	}

	@Override
	public void caseExp_Or(NExp_Or node) {
		int l = visitExp(node.get_Left());
		String label = genLabel();
		write("\tbnez s" + l + ", " + label + "\n");
		visitExp(node.get_Right());
		write(label + ":\n");
	}

	@Override
	public void caseInt(NInt node) {
		int v = litteralAnalysis.values.get(node);
		write("\tli s"+lastRegister + ", " + v + "\n");
	}

	@Override
	public void caseExp_True(NExp_True node) {
		write("\tli s"+lastRegister + ", 1\n");
	}

	@Override
	public void caseExp_False(NExp_False node) {
		write("\tli s"+lastRegister + ", 0\n");
	}

	private HashMap<Variable, Integer> variables = new HashMap<>();

	void setVariable(NId nid, int regexp) {
		Variable var = scopeAnalysis.variables.get(nid);
		int regvar = variables.get(var);
		write("\tmv s" + regvar + ", s" + regexp + "\n");
	}

	@Override
	public void caseStmt_Assign(NStmt_Assign node) {
		int val = visitExp(node.get_Exp());
		setVariable(node.get_Id(), val);
	}

	@Override
	public void caseStmt_Var(NStmt_Var node) {
		Variable var = scopeAnalysis.variables.get(node.get_Id());
		variables.put(var, lastRegister);
		lastRegister++;

		int val = visitExp(node.get_Exp());
		setVariable(node.get_Id(), val);
	}

	@Override
	public void caseExp_Var(NExp_Var node) {
		Variable var = scopeAnalysis.variables.get(node.get_Id());
		int regvar = variables.get(var);
		write("\tmv s" + lastRegister + ", s" + regvar + "\n");
	}

	int lastLabel = 0;

	String genLabel() { return ".L" + lastLabel++ ;}

	@Override
	public void caseStmt_If(NStmt_If node) {
		int cnd = visitExp(node.get_Exp());
		String end = genLabel();
		write("\tbeqz s" + cnd + ", " + end + "\n");
		node.get_Block().apply(this);
		write(end + ":\n");
	}

	@Override
	public void caseStmt_Ifelse(NStmt_Ifelse node) {
		int cnd = visitExp(node.get_Exp());
		String elselab = genLabel();
		String end = genLabel();
		write("\tbeqz s" + cnd + ", " + elselab + "\n");
		node.get_Block().apply(this);
		write("\tj " + end + "\n");
		write(elselab + ":\n");
		node.get_Else().apply(this);
		write(end + ":\n");
	}

	@Override
	public void caseStmt_While(NStmt_While node) {
		String loop = genLabel();
		write(loop + ":\n");
		int cnd = visitExp(node.get_Exp());
		String end = genLabel();
		write("\tbeqz s" + cnd + ", " + end + "\n");
		node.get_Block().apply(this);
		write( "\tj " + loop + "\n");
		write(end + ":\n");
	}

	@Override
	public void caseStmt_Printint(NStmt_Printint node) {
		int r = visitExp(node.get_Exp());
		write("\tmv a0, s" + r + "\n");
		write("\tcall printint\n");
	}

	@Override
	public void caseStmt_Printbool(NStmt_Printbool node) {
		int r = visitExp(node.get_Exp());
		write("\tmv a0, s" + r + "\n");
		write("\tcall printbool\n");
	}

	@Override
	public void caseStmt_Println(NStmt_Println node) {
		write("\tcall println\n");
	}

	void call(NId nid, NArgs nargs) {
		List<NExp> exps = scopeAnalysis.collectArgs(nargs);
		for(int i = 0; i < exps.size(); i++) {
			int r = visitExp(exps.get(i));
			write("\tmv a" + i + ", s" + r + "\n");
		}
		write("\tcall " + nid.getText() + "\n");
	}

	@Override
	public void caseStmt_Call(NStmt_Call node) {
		call(node.get_Id(), node.get_Args());
	}

	@Override
	public void caseExp_Call(NExp_Call node) {
		call(node.get_Id(), node.get_Args());
		write("\tmv s" + lastRegister + ", a0\n");
	}

	@Override
	public void caseStmt_Return(NStmt_Return node) {
		int r = visitExp(node.get_Exp());
		write("\tmv a0, s" + r + "\n");
		write("\tj " + currentRetLabel + "\n");
	}
}
