package minic.front;

import language_minic.NBlock;

import java.util.ArrayList;
import java.util.List;

public class Function {
    public String name;
    public List<Variable> parameters = new ArrayList<>();
    public Type returnType;
    public NBlock body;
}
