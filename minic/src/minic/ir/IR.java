package minic.ir;

import java.util.Arrays;
import java.util.Objects;

/** A basic instruction in the intermediate representation in 3 address code.
 * The implementation is homogeneous (no subclasses) thus the class fields and methods fit all kind of instruction.
 * Consistent uses of IR is the clients' duty. */
public class IR implements Cloneable {

	public Instr instr;
	public Register dst;
	public Operand[] src;
	public BasicBlock[] labels;
	public String symbol;

	public IR(Instr instr, Register dst, Operand... src) {
		this.instr = instr;
		this.dst = dst;
		this.src = src;
	}

	/** Only used at construction. */
	public IR setEdges(BasicBlock... labels) {
		this.labels = labels;
		return this;
	}

	public boolean isArith() {
		switch (instr) {
			case Add:
			case Sub:
			case Mul:
			case Lt:
				return true;
			default:
				return false;
		}
	}

	public boolean isPure() {
		return isArith() || instr == Instr.Mv;
	}

	public boolean uses(Operand op) {
		for (Operand operand : src)
			if (operand == op)
				return true;
		return false;
	}

	public boolean replaceSrc(Operand oldOp, Operand newOp) {
		touch();
		boolean result = false;
		for (int i = 0; i < src.length; i++)
			if (src[i] == oldOp) {
				src[i] = newOp;
				result = true;
			}
		return result;
	}

	public void replaceLabel(BasicBlock oldBlock, BasicBlock newBlock) {
		touch();
		for (int i=0; i<labels.length; i++)
			if (labels[i] == oldBlock)
				labels[i] = newBlock;
	}

	/** Mark as new. Used for CFG diff. */
	public void setNew() {
		if (was != null) return;
		was = new IR(null, null);
	}

	/** Mark as removed. Used for CFG diff.
	 * Result is a copy of this, if one want to move it somewhere.  */
	public IR remove() {
        IR result = clone();
        touch();
		instr = null;
		dst = null;
		src = new Operand[0];
		labels = null;
		symbol = null;
		return result;
	}

	/** Change the whole instruction. Used for CFG diff. */
	public IR replaceWith(IR ir) {
		IR result = clone();
		touch();
		instr = ir.instr;
		dst = ir.dst;
		src = ir.src;
		labels = ir.labels;
		symbol = ir.symbol;
		return result;
	}

	/** Mark that something changed in the IR. Used for CFG diff.*/
	public void touch() {
		if (was != null) return;
		was = clone();
	}

	public String toStringPlain() {
		StringBuilder sb = new StringBuilder();
		if (dst != null) {
			sb.append(dst);
			sb.append(" = ");
		}
		sb.append(instr);
		if (symbol != null) {
			sb.append(" ");
			sb.append(symbol);
		}
		if (src.length > 0) {
			sb.append(Arrays.toString(src));
		}
		if (labels != null && labels.length > 0) {
			sb.append(" ");
			sb.append(Arrays.toString(labels));
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		if (instr == null) return "null";
		StringBuilder sb = new StringBuilder();
		if (dst != null) {
			sb.append(dst);
			sb.append(" = ");
		}

		switch (instr) {
			case Mv: sb.append(src[0]); break;

			case Add: toStringArith(sb, " + "); break;
			case Sub: toStringArith(sb, " - "); break;
			case Mul: toStringArith(sb, " * "); break;
			case Lt: toStringArith(sb, " < "); break;

			case Jmp: sb.append("jmp ").append(labels[0]); break;

			case If:
				sb.append("if (").append(src[0]).append(") ").append(labels[0]).append(" ").append(labels[1]); break;
			case Iflt:
				sb.append("if (").append(src[0]).append(" < ").append(src[1]).append(") ").append(labels[0]).append(" ").append(labels[1]); break;

			case Call:
				sb.append(symbol).append("(");
				appendJoin(sb, ",", src);
				sb.append(")"); break;
			case Ret:
				sb.append("ret ").append(src[0]); break;

			default: throw new RuntimeException("ICE: unknown instruction " + instr);
		}
		return sb.toString();
	}

	void toStringArith(StringBuilder sb, String operator) {
		sb.append(src[0]).append(operator).append(src[1]);
	}

	StringBuilder appendJoin(StringBuilder sb, String separator, Object[] array) {
		boolean first = true;
		for (Object o : array) {
			if (first) first = false;
			else sb.append(separator);
			sb.append(o);
		}
		return sb;
	}

	public IR was = null;

	@Override
	public IR clone() {
        try {
            IR result = (IR) super.clone();
			result.src = src.clone();
			if (labels != null)
				result.labels = labels.clone();
			return result;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean same(IR ir) {
		if (this == ir) return true;
		if (ir == null) return false;
		return instr == ir.instr && Objects.equals(dst, ir.dst) && Objects.deepEquals(src, ir.src) && Objects.deepEquals(labels, ir.labels) && Objects.equals(symbol, ir.symbol);
	}
}