package minic;

import minic.language_minic.NStmt_Var;

public class Variable {
    String name;
    NStmt_Var declaration;
    public Variable(String name, NStmt_Var declaration) {
        this.name = name;
        this.declaration = declaration;
    }

    Type type;
}
