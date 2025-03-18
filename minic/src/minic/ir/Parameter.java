package minic.ir;

/** A function parameter.
 * To simplify some analysis, those are not registers (not assignable).
 * <br/>
 * Limitation: In order to avoid issues with machine registers, do not move or propagate them.
 */
public class Parameter extends Operand {
    int number;

    public Parameter(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "$a" + number;
    }

    @Override
    public String toMachine() {
        return "a" + number;
    }
}
