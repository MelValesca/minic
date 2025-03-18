package minic.ir;

/** An operand of an instruction in the intermediate representation. */
public abstract class Operand {

    /** Should this be used as in immediate RISC-V operand? */
    public boolean isMachineImmediate() {
        return false;
    }

    /** The representation of the operand in RISC-V */
    public abstract String toMachine();
}
