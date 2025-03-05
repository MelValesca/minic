package minic.front;

import language_minic.Node;

public class Variable {
    public String name;
    public Node declaration;
    public Variable(String name, Node declaration) {
        this.name = name;
        this.declaration = declaration;
    }

    public Type type;
}
