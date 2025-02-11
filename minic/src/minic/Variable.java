package minic;

import minic.language_minic.Node;

public class Variable {
    String name;
    Node declaration;
    public Variable(String name, Node declaration) {
        this.name = name;
        this.declaration = declaration;
    }

    Type type;
}
