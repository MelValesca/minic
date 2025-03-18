package minic.lib;

import minic.ir.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/** Basic *old-school* dataflow analysis framework.
 * <br>
 * Implement {@link #gen(BasicBlock,IR)} and {@link #kill(BasicBlock,IR)}.
 * Setup flags {@link #backward}, {@link #must} and/or {@link #onlyBlocks}.
 * @param <T> Type of flown data.
 */
public abstract class Dataflow<T> extends Pass {
    private HashMap<Object, Node> nodes = new HashMap<>();
    private ArrayList<Node> order = new ArrayList<>();
    private List<T> fullSet = new ArrayList<>();

    public Dataflow(CFG cfg) {
        super(cfg);
    }

    // to redefine in subclasses
    protected Collection<T> gen(BasicBlock block, IR ir) { return null; }
    protected Collection<T> kill(BasicBlock block, IR ir) { return null; }
    public String stringOf(T item) { return item.toString(); }

    // Settings
    public boolean backward = false;
    public boolean must = false;
    public boolean onlyBlocks = false;

    public List<T> getIn(Object item) {
        Node n = nodes.get(item);
        return bitSetToCollection(n.in);
    }

    public List<T> getOut(Object item) {
        Node n = nodes.get(item);
        return bitSetToCollection(n.out);
    }

    private ArrayList oldOrder = new ArrayList();

    public void process() {
        oldOrder = order;
        nodes.clear();
        order = new ArrayList<>();
        fullSet.clear();
        if (onlyBlocks)
            fillBB();
        else
            fillIR();
        work();
    }

    @Override
    public boolean changed() {
        return !oldOrder.equals(order);
    }

    private Node newNode(Object object) {
        Node node = new Node(object);
        node.id = order.size();
        order.add(node);
        nodes.put(object, node);
        return node;
    }

    BitSet collectionToBitSet(Collection<T> collection) {
        BitSet bitSet = new BitSet();
        if (collection == null) return bitSet;
        for (T item : collection) {
            int index = fullSet.indexOf(item);
            if (index == -1) {
                index = fullSet.size();
                fullSet.add(item);
            }
            bitSet.set(index);
        }
        return bitSet;
    }

    List<T> bitSetToCollection(BitSet bits) {
        List<T> result = new ArrayList<>();
        for(int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i+1)) {
            result.add(fullSet.get(i));
        }
        return result;
    }

    public void fillBB() {
        for (BasicBlock block : cfg.blocks) {
            Node node = newNode(block);
            node.gen = collectionToBitSet(gen(block, null));
            node.kill = collectionToBitSet(kill(block, null));
        }
        for (BasicBlock block : cfg.blocks) {
            Node node = nodes.get(block);
            if (block.instructions.isEmpty()) continue;
            for (BasicBlock nextBlock : block.getOut()) {
                Node nextNode = nodes.get(nextBlock);
                node.next.add(nextNode);
                nextNode.prev.add(node);
            }
        }
    }

    public void fillIR() {
        for (BasicBlock block : cfg.blocks) {
            for (IR ir : block.instructions) {
                Node node = newNode(ir);
                node.gen = collectionToBitSet(gen(block, ir));
                node.kill = collectionToBitSet(kill(block, ir));
            }
        }
        for (BasicBlock block : cfg.blocks) {
            for (int i = 0; i < block.instructions.size() - 1; i++) {
                IR ir = block.instructions.get(i);
                Node node = nodes.get(ir);
                IR nextIr = block.instructions.get(i + 1);
                Node nextNode = nodes.get(nextIr);
                node.next.add(nextNode);
                nextNode.prev.add(node);
            }
            IR terminator = block.getTerminator();
            Node lastNode = nodes.get(terminator);
            for (BasicBlock nextBlock : terminator.labels) {
                Node nextNode = nodes.get(nextBlock.instructions.get(0));
                lastNode.next.add(nextNode);
                nextNode.prev.add(lastNode);
            }
        }
    }

    public void work() {
        if(backward) {
            for (Node i : nodes.values()) {
                List<Node> tmp = i.next;
                i.next = i.prev;
                i.prev = tmp;
            }
        }
        if(must) {
            BitSet initialOut = new BitSet();
            for (Node node : nodes.values()) {
                BitSet gen = node.gen;
                if (gen != null) initialOut.or(gen);
            }
            for (Node node : nodes.values()) {
                node.out = initialOut;
            }
        }

        List<Node> worklist = new ArrayList<>(order);

        while(!worklist.isEmpty()) {
            Node info = worklist.remove(worklist.size()-1);
            BitSet bs = null;
            for (Node p : info.prev) {
                if (bs == null)
                    bs = (BitSet)p.out.clone();
                else if (must)
                    bs.and(p.out);
                else
                    bs.or(p.out);
            }
            if (bs == null) {
                bs = new BitSet();
            }
            info.in = (BitSet) bs.clone();
            if (info.kill != null)
                bs.andNot(info.kill);
            if (info.gen != null)
                bs.or(info.gen);
            if (!info.out.equals(bs)) {
                info.out = bs;
                worklist.addAll(info.next);
            }
        }

        if(backward) {
            for (Node i : nodes.values()) {
                BitSet tmp = i.in;
                i.in = i.out;
                i.out = tmp;
            }
        }
    }

    String dotEscape(BitSet bs) {
        StringBuilder s = new StringBuilder();
        for (int b : bs.stream().toArray()) {
            if (!s.isEmpty())
                s.append(", ");
            s.append(stringOf(fullSet.get(b)));
        }
        return CFGraphviz.escape(s.toString());
    }

    public void dumpDataFlow(String filename) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write("digraph {\n");
            for (Node i : order) {
                fw.write("N" + i.id + "[shape=box,label=<" + CFGraphviz.escape(i.object.toString()) +
                        "<BR ALIGN=\"LEFT\"/>in=" + (i.in.equals(i.out) ? "out=" : "") + dotEscape(i.in) +
                        (i.gen != null ? "<BR ALIGN=\"LEFT\"/>gen=" + dotEscape(i.gen) : "") +
                        (i.kill != null ? "<BR ALIGN=\"LEFT\"/>kill=" + dotEscape(i.kill) : "") +
                        (i.in.equals(i.out) ? "" : "<BR ALIGN=\"LEFT\"/>out=" + dotEscape(i.out) ) +
                        "<BR ALIGN=\"LEFT\"/>>];\n");
                for (Node n : i.next) {
                    if (backward)
                        fw.write("N" + n.id + " -> N" + i.id + ";\n");
                    else
                        fw.write("N" + i.id + " -> N" + n.id + ";\n");
                }
            }
            fw.write("}\n");
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dumpCFG(String filename) {
        CFGraphviz viz = new CFGraphviz(cfg) {
            BitSet prevBits = null;

            @Override
            protected void writeNodeContent(BasicBlock block) {
                if (onlyBlocks) {
                    Node node = nodes.get(block);
                    write("<FONT COLOR=\"darkblue\"><I>{" + dotEscape(node.in) + "}</I></FONT><BR ALIGN=\"RIGHT\"/></TD></TR><TR><TD>");
                } else {
                    Node node = nodes.get(block.instructions.get(0));
                    prevBits = node.in;
                    write("<FONT COLOR=\"darkblue\"><I>{" + dotEscape(node.in) + "}</I></FONT><BR ALIGN=\"RIGHT\"/>");
                }
                super.writeNodeContent(block);
                if (onlyBlocks) {
                    Node node = nodes.get(block);
                    write("</TD></TR><TR><TD><FONT COLOR=\"darkblue\"><I>{" + dotEscape(node.in) + "}</I></FONT><BR ALIGN=\"RIGHT\"/>");
                }
            }

            @Override
            protected void writeInstruction(BasicBlock block, IR instruction, int number) {
                super.writeInstruction(block, instruction, number);
                if (!onlyBlocks) {
                    Node node = nodes.get(instruction);
                    if (!node.out.equals(prevBits)) {
                        write("<FONT COLOR=\"darkblue\"><I>{" + dotEscape(node.out) + "}</I></FONT><BR ALIGN=\"RIGHT\"/>");
                        prevBits = node.out;
                    }
                }
            }
        };

        viz.dumpDot(filename);
    }

    @Override
    public void dump(String prefix) {
        dumpDataFlow(prefix + ".dataflow.dot");
        dumpCFG(prefix + ".cfg.dot");
    }

    static class Node {
        Object object;
        int id;
        BitSet in = new BitSet();
        BitSet out = new BitSet();
        BitSet gen = new BitSet();
        BitSet kill = new BitSet();
        List<Node> prev = new ArrayList<>();
        List<Node> next = new ArrayList<>();

        public String toString() {
            return object.toString() + " in=" + in + " gen=" + gen + " kill=" + kill + " out=" + out;
        }

        Node(Object object) {
            this.object = object;
        }

        @Override
        public boolean equals(Object object1) {
            if (this == object1) return true;
            if (object1 == null || getClass() != object1.getClass()) return false;
            Node node = (Node) object1;
            return id == node.id && Objects.equals(object, node.object) && Objects.equals(in, node.in) && Objects.equals(out, node.out) && Objects.equals(gen, node.gen) && Objects.equals(kill, node.kill);
        }

        @Override
        public int hashCode() {
            return Objects.hash(object, id, in, out, gen, kill);
        }
    }
}