import java.io.*;
import language_calc.*;

/** Naive evaluator for a simple calculator. */
public class Calc extends Walker {

	/** Interpreter main method.
	 * Parse and evaluate each line from the standard input. */
	public static void main(String[] args) throws Exception {
		BufferedReader ir = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			try {
				System.out.print("> ");
				String line = ir.readLine();
				if (line == null || line.isEmpty()) break;
				Reader r = new StringReader(line);
				Parser parser = new Parser(r);
				Node syntaxTree = parser.parse();
				Calc evaluator = new Calc();
				int value = evaluator.visitExp(syntaxTree);
				System.out.println(" = " + value);
			} catch (LexerException e) {
				System.out.println(e.getMessage() + ".");
			} catch (ParserException e) {
				System.out.println(e.getMessage() + ".");
			} catch (EvaluationError e) {
				System.out.println(e.getMessage() + ".");
			} catch (IOException e) {
				System.out.println(e.getMessage() + ".");
				System.exit(1);
			}
		};
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
	public void caseInt(NInt node) {
		try {
			lastExpValue = Integer.parseInt(node.getText());
		} catch (NumberFormatException e) {
			throw new EvaluationError(node.getText() + ": excesses integer capacity");
		}
	}

	// NOTE: caseExp_Int and caseExp_Par are not overridden since the default behavior
	// is to evaluate their subtree which correctly assigns lastExpValue

	/**
	 * Error during an evaluation.
	 * Made a RuntimeException since the exception must pass through generated methods.
	 * **/
	public static class EvaluationError extends RuntimeException {
		public EvaluationError(String string) {
			super(string);
		}
	}
}
