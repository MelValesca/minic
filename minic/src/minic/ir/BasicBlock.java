package minic.ir;

import java.util.ArrayList;
import java.util.List;

/** A basic block of a CFG.
 * Can only enter at the first instruction, and exit at the last one (called terminator).
 * Note: most operations are not cached. This is a bad idea in a real compiler but makes the code far simpler (no cache invalidation).
 */

public class BasicBlock {
    public CFG cfg;
    public int id;
    public List<IR> instructions = new ArrayList<>();

    public BasicBlock(CFG cfg, int id) {
        this.cfg = cfg;
        this.id = id;
    }

    public List<BasicBlock> getOut() {
        if (instructions.isEmpty()) return new ArrayList<>();
        IR terminator = getTerminator();
        if (terminator.labels == null) return new ArrayList<>();
        return List.of(terminator.labels);
    }

    public List<BasicBlock> getIn() {
        List<BasicBlock> result = new ArrayList<>();
        for (BasicBlock block : cfg.blocks)
            if (block.getOut().contains(this))
                result.add(block);
        return result;
    }

    public IR getTerminator() {
        return instructions.get(instructions.size() - 1);
    }

    public BasicBlock nextLinearBlock() {
        int i = cfg.blocks.indexOf(this) + 1;
        if (i < cfg.blocks.size())
            return cfg.blocks.get(i);
        else
            return null;
    }

    @Override
    public String toString() {
        return "L" + id;
    }

    public void add(IR instruction) {
        instructions.add(instruction);
        instruction.setNew();
    }

    public void addBeforeTerminator(IR instruction) {
        instructions.add(instructions.size()-1, instruction);
        instruction.setNew();
    }
}