package minic.ir;

/** A virtual register (virtual variable) of the intermediate representation.
 * It can be mapped to a real machine register once needed. */
public class Register extends Operand {
    public int id;
    public String name;

    public Register(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /** Is mapped to a machine register? */
    public boolean isMachine() {
        return machineRegister >= 0;
    }

    /** Number of the machine register */
    public int machineRegister = -1;
    /** Use a saved machine register */
    public boolean machineSaved;

    @Override
    public String toString() {
        if (isMachine())
            return name + ":" + toMachine();
        return name;
    }

    public String toMachine() {
        if (!isMachine())
            return "???" + this;
        else if (machineSaved)
            return "s" + machineRegister;
        else
            return "t" + machineRegister;
    }
}
