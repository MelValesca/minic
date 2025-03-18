package minic;

import language_minic.*;
import minic.back.AsmEmitter;
import minic.front.*;
import minic.lib.PassManager;
import minic.ir.*;

import java.io.*;
import java.util.*;

public class MiniCC2 extends Walker {

	LitteralAnalysis litteralAnalysis = new LitteralAnalysis();
	ScopeAnalysis scopeAnalysis = new ScopeAnalysis();
	TypeAnalysis typeAnalysis = new TypeAnalysis(scopeAnalysis);

	String name;
	AsmEmitter writer;

	/** Compiler main method. */
	public static void main(String[] args) throws Exception {
		MiniCC2 compiler = new MiniCC2();
		PassManager passManager = compiler.passManager;
		int argi = 0;
		while (argi < args.length && args[argi].startsWith("-")) {
			String[] splitOption = args[argi].split("=", 2);
			String optionName = splitOption[0];
			String optionValue = splitOption.length > 1 ? splitOption[1] : "";
			switch (optionName) {
				case "-O1": passManager.opt1 = true; break;
				case "-kempe": passManager.kempe = true; break;
				case "-ramax": passManager.ramax = Integer.parseInt(optionValue); break;
				case "-repeat": passManager.repeat = Integer.parseInt(optionValue); break;
				case "-subrepeat": passManager.subrepeat = Integer.parseInt(optionValue); break;
				case "-small-steps": passManager.smallSteps = true; break;
				default: throw new Error("Unknown option: " + args[argi]);
			}
			argi++;
		}
		Reader r = new FileReader(args[argi]);
		Parser parser = new Parser(r);
		Node syntaxTree = parser.parse();
		syntaxTree.apply(compiler.litteralAnalysis);
		syntaxTree.apply(compiler.scopeAnalysis);
		syntaxTree.apply(compiler.typeAnalysis);

		compiler.name = new File(args[argi]).getName();
		compiler.writer = new AsmEmitter("minicc2.out.s");
		syntaxTree.apply(compiler);
		compiler.processPasses();
		compiler.writer.close();
	}

	PassManager passManager = new PassManager();

	void processPasses() {
		for (Function function : scopeAnalysis.functions.values()) {
			CFG cfg = function.cfg;
			function.file = name;
			System.err.println("*** Function " + function.name);
			passManager.processPasses(cfg);
			writer.generate(cfg);
		}
	}

	private CFG currentCFG;
	private BasicBlock currentBlock;
	private Register returnRegister;

	private void add(IR ir) {
		if (currentBlock == null) return;
		currentBlock.add(ir);
	}

	private IR add(Instr instr, Register dst, Operand... src) {
		IR ir = new IR(instr, dst, src);
		add(ir);
		return ir;
	}

	private BasicBlock newBlock() {
		BasicBlock block = currentCFG.newBlock();
		currentCFG.blocks.add(block);
		return block;
	}

	private Register newRegister(String name) {
		if (hintName != null && name.isEmpty())
			name = hintName;
		hintName = null;
		return currentCFG.newRegister(name);
	}

	@Override
	public void caseFun(NFun node) {
		Function fun = scopeAnalysis.functions.get(node.get_Id().getText());
		currentCFG = new CFG(fun);
		fun.cfg = currentCFG;
		currentCFG.entryBlock = currentBlock = newBlock();
		returnRegister = newRegister("return");
		currentCFG.exitBlock = newBlock();

		super.caseFun(node);

		/* Try to merge exitblock to have nice CFG*/
		List<BasicBlock> in = currentCFG.exitBlock.getIn();
		if(currentBlock != null) {
			add(Instr.Mv, returnRegister, new Constant(0));
			if (in.isEmpty()) {
				currentCFG.blocks.remove(currentCFG.exitBlock);
				currentCFG.exitBlock = currentBlock;
			} else {
				add(Instr.Jmp, null).setEdges(currentCFG.exitBlock);
			}
		}

		if (in.size() == 1) {
			BasicBlock block = in.get(0);
			IR last = block.instructions.get(block.instructions.size()-1);
			if (last.instr == Instr.Jmp && last.labels[0] == currentCFG.exitBlock) {
				block.instructions.remove(block.instructions.size()-1);
				currentCFG.exitBlock = block;
			}
		}

		currentBlock = currentCFG.exitBlock;
		add(Instr.Ret, null, returnRegister).setEdges();

		cleanUpCFG();
	}

	void cleanUpCFG() {
		currentCFG.reorder();
		for (int i=0; i<currentCFG.blocks.size(); i++)
			currentCFG.blocks.get(i).id = i;
	}

	@Override
	public void caseParam(NParam node) {
		Variable var = scopeAnalysis.variables.get(node.get_Id());
		Register reg = newRegister(var.name);
		variables.put(var, reg);
		Parameter parameter = new Parameter(currentCFG.parameters.size());
		currentCFG.parameters.add(reg);
		add(Instr.Mv, reg, parameter);
	}

	Operand visitExp(Node n) {
		n.apply(this);
		return lastOperand;
	}

	Operand lastOperand;

	private void addBinOp(Instr instr, NExp left, NExp right) {
		Register register = newRegister("");
		add(instr, register, visitExp(left), visitExp(right));
		lastOperand = register;
	}

	@Override
	public void caseExp_Add(NExp_Add node) {
		addBinOp(Instr.Add, node.get_Left(), node.get_Right());
	}

	@Override
	public void caseExp_Sub(NExp_Sub node) {
		addBinOp(Instr.Sub, node.get_Left(), node.get_Right());
	}

	@Override
	public void caseExp_Mul(NExp_Mul node) {
		addBinOp(Instr.Mul, node.get_Left(), node.get_Right());
	}

	@Override
	public void caseExp_Lt(NExp_Lt node) {
		addBinOp(Instr.Lt, node.get_Left(), node.get_Right());
	}

	private void constant(int value) {
		lastOperand = new Constant(value);
	}

	@Override
	public void caseInt(NInt node) {
		constant(litteralAnalysis.values.get(node));
    }

	@Override
	public void caseExp_True(NExp_True node) {
		constant(1);
    }

	@Override
	public void caseExp_False(NExp_False node) {
		constant(0);
	}

	private HashMap<Variable, Register> variables = new HashMap<>();

	String hintName = null;

	void setVariable(NId nid, NExp value) {
		Variable var = scopeAnalysis.variables.get(nid);
		Register register = variables.get(var);
		hintName = var.name;
		Operand op = visitExp(value);
		hintName = null;
		add(Instr.Mv, register, op);
	}

	@Override
	public void caseStmt_Assign(NStmt_Assign node) {
		setVariable(node.get_Id(), node.get_Exp());
	}

	@Override
	public void caseStmt_Var(NStmt_Var node) {
		Variable var = scopeAnalysis.variables.get(node.get_Id());
		Register op = newRegister(var.name);
		variables.put(var, op);
		setVariable(node.get_Id(), node.get_Exp());
	}

	@Override
	public void caseExp_Var(NExp_Var node) {
		lastOperand = variables.get(scopeAnalysis.variables.get(node.get_Id()));
	}

	private void addIf(Operand op, BasicBlock thenBlock, BasicBlock elseBlock) {
		if (currentBlock == null) return;
		add(Instr.If, null, op).setEdges(thenBlock, elseBlock);
		currentBlock = null;
	}

	private void addJmp(BasicBlock label) {
		if (currentBlock == null) return;
		add(Instr.Jmp, null).setEdges(label);
		currentBlock = null;
	}

	@Override
	public void caseStmt_If(NStmt_If node) {
		BasicBlock thenBlock = newBlock();
		BasicBlock endBlock = newBlock();

		hintName = "cond";
		Operand op = visitExp(node.get_Exp());
		addIf(op, thenBlock, endBlock);

		currentBlock = thenBlock;
		node.get_Block().apply(this);
		addJmp(endBlock);

		currentBlock = endBlock;
	}

	@Override
	public void caseStmt_Ifelse(NStmt_Ifelse node) {
		BasicBlock thenBlock = newBlock();
		BasicBlock elseBlock = newBlock();
		BasicBlock endBlock = newBlock();

		hintName = "cond";
		Operand op = visitExp(node.get_Exp());
		addIf(op, thenBlock, elseBlock);

		currentBlock = thenBlock;
		node.get_Block().apply(this);
		addJmp(endBlock);

		currentBlock = elseBlock;
		node.get_Else().apply(this);
		addJmp(endBlock);

		currentBlock = endBlock;
	}

	@Override
	public void caseStmt_While(NStmt_While node) {
		BasicBlock loopBlock = newBlock();
		BasicBlock bodyBlock = newBlock();
		BasicBlock endBlock = newBlock();
		addJmp(loopBlock);

		currentBlock = loopBlock;
		hintName = "cond";
		Operand op = visitExp(node.get_Exp());
		addIf(op, bodyBlock, endBlock);

		currentBlock = bodyBlock;
		node.get_Block().apply(this);
		addJmp(loopBlock);

		currentBlock = endBlock;
	}

	IR addCall(String symbol, Register dst, Operand... arg) {
		IR ir = new IR(Instr.Call, dst, arg);
		ir.symbol = symbol;
		add(ir);
		return ir;
	}

	@Override
	public void caseStmt_Printint(NStmt_Printint node) {
		Operand op = visitExp(node.get_Exp());
		addCall("printint", null, op);
	}

	@Override
	public void caseStmt_Printbool(NStmt_Printbool node) {
		Operand op = visitExp(node.get_Exp());
		addCall("printbool", null, op);
	}

	@Override
	public void caseStmt_Println(NStmt_Println node) {
		addCall("println", null);
	}

	void call(NId nid, Register dst, NArgs nargs) {
		List<NExp> exps = scopeAnalysis.collectArgs(nargs);
		Operand[] operands = new Operand[exps.size()];
		for (int i = 0; i < exps.size(); i++) {
			operands[i] = visitExp(exps.get(i));
		}
		addCall(nid.getText(), dst, operands);
	}

	@Override
	public void caseStmt_Call(NStmt_Call node) {
		call(node.get_Id(), null, node.get_Args());
	}

	@Override
	public void caseExp_Call(NExp_Call node) {
		Register dst = newRegister("");
		call(node.get_Id(), dst, node.get_Args());
		lastOperand = dst;
	}

	@Override
	public void caseStmt_Return(NStmt_Return node) {
		Operand op = visitExp(node.get_Exp());
		add(Instr.Mv, returnRegister, op);
		addJmp(currentCFG.exitBlock);
		currentBlock = newBlock();
	}
}
