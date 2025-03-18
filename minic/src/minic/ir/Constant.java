package minic.ir;

import java.util.Objects;

/** A simple immediate value. */
public class Constant extends Operand {
    public int value;

    public Constant(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean isMachineImmediate() {
        return value != 0;
    }

    @Override
    public String toMachine() {
        if (value == 0) return "zero";
        else return toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Constant constant = (Constant) object;
        return value == constant.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}