package minic.front;

import language_minic.NBlock;
import minic.ir.CFG;

import java.util.ArrayList;
import java.util.List;

public class Function {
    public String file;
    public String name;
    public List<Variable> parameters = new ArrayList<>();
    public Type returnType;
    public NBlock body;

    Function(String name, NBlock body) {
        this.name = name;
        this.body = body;
    }

    public CFG cfg;
}
