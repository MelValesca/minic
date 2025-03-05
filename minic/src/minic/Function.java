package minic;

import language_minic.NBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;

public class Function {
    public List<Variable> parameters = new ArrayList<>();
    Type returnType;
    NBlock body;
}
