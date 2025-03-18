package minic.back;

import minic.ir.*;
import minic.lib.Pass;
import minic.opt.LiveVariables;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/** Classic linear-scan register allocator.
 * TODO: register spilling.
 */
public class RegisterAllocationLinearScan extends Pass {
    LiveVariables liveVariables;
    int ramax;

    public RegisterAllocationLinearScan(LiveVariables liveVariables, int ramax) {
        super(liveVariables.cfg);
        this.liveVariables = liveVariables;
        this.ramax = ramax;
    }

    HashMap<Register, Interval> intervalsByRegister;
    ArrayList<Interval> intervalsByStart;

    public void process() {
        buildIntervals();
        allocateRegisters();
    }

    void buildIntervals() {
        intervalsByRegister = new HashMap<>();
        intervalsByStart = new ArrayList<>(cfg.registers.size());

        for (Register reg : cfg.registers)
            intervalsByRegister.put(reg, new Interval(reg));

        int n = 0;
        for (BasicBlock block : cfg.blocks) {
            for (IR ir : block.instructions) {
                for (Register reg : cfg.registers) {
                    Interval interval = intervalsByRegister.get(reg);
                    if (liveVariables.needSavingAt(ir, reg))
                        reg.machineSaved = true;
                    if (liveVariables.isLiveAt(ir, reg)) {
                        if (interval.start == -1) {
                            interval.start = n;
                            intervalsByStart.add(interval);
                        }
                        interval.stop = n;
                    }
                }
                n++;
            }
        }

        beforeColorint = intervalTikz();
    }

    private String beforeColorint;

    void allocateRegisters() {
        BitSet usedS = new BitSet();
        BitSet usedT = new BitSet();

        int nbsav=0;
        int nbtmp=0;

        HashSet<Interval> active = new HashSet<>();
        for (Interval interval : intervalsByStart) {
            // Expire
            for (Iterator<Interval> ia = active.iterator(); ia.hasNext();) {
                Interval a = ia.next();
                if (interval.start > a.stop) {
                    ia.remove();
                    BitSet used = a.register.machineSaved ? usedS : usedT;
                    used.clear(a.register.machineRegister);
                }
            }

            active.add(interval);
            BitSet used;
            int registerCount;
            if (interval.register.machineSaved) {
                used = usedS;
                registerCount = Math.min(12, ramax); //s0 to s11
            } else {
                used = usedT;
                registerCount = Math.min(7, ramax); //t0 to t6
            }
            int assign = used.nextClearBit(0);
            if (assign >= registerCount) {
                throw new RuntimeException("ICE TODO Tâche 4.1 Devoir 3");
            }
            interval.register.machineRegister = assign;
            used.set(assign);
            if (interval.register.machineSaved) {
                if (assign >= nbsav) nbsav = assign+1;
            } else {
                if (assign >= nbtmp) nbtmp = assign+1;
            }
            //log(interval + " reg=" + interval.register);
        }

        log("Registers allocated: " + nbtmp + "tmp " + nbsav + "sav ");
    }

    @Override
    public void dump(String prefix) {
        dumpCFG(prefix + ".cfg.dot");
        liveVariables.dumpInterference(prefix + ".coloring.dot");
        dumpInterval(prefix + ".interval.pgf", beforeColorint);
        dumpInterval(prefix + ".interval2.pgf", intervalTikz());
    }

    String intervalTikz() {
        String s = "\\begin{tikzpicture}\n";
        Set<Integer> tics = new HashSet<>();
        int i = 0;
        int n = 0;
        for (Interval interval : intervalsByStart) {
            tics.add(interval.start);
            tics.add(interval.stop+1);
        }
        String[] colors = new String[]{"blue", "brown", "cyan", "green", "lime", "magenta", "olive", "orange", "pink", "purple", "red", "teal", "violet", "yellow"};
        for (int tic : tics) {
            s += "\\draw[color=gray,dotted] ("+(intervalsByStart.size() + 4)+",-" + (tic) + ") -- (0.5,-" + (tic) + ") node[left] {" + (tic-1) + "};\n";
        }
        HashMap<String, String> reg2colors = new HashMap<>();
        for (Interval interval : intervalsByStart) {
            String color;
            if (interval.register.isMachine()) {
                String reg = interval.register.toMachine();
                color = reg2colors.get(reg);
                if (color == null) {
                    color = colors[reg2colors.size() % colors.length];
                    reg2colors.put(reg, color);
                }
            } else {
                color = "gray";
            }
            i++;
            tics.add(interval.start);
            tics.add(interval.stop);
            s += "\\draw (" + i + ",-" + interval.start + ") -- (" + i + ",-" + (interval.stop+1) + ");\n";
            s += "\\draw[fill="+color+"] (" + i + ",-" + interval.start + ") circle (0.25);\n";
            s += "\\draw[align=left] (" + i + ",-" + (interval.start) + ") node[anchor=south west] {" + interval.register + "};\n";
            s += "\\draw[fill="+color+"] (" + i + ",-" + (interval.stop+1) + ") circle (0.25);\n";
            if (interval.stop > n)
                n = interval.stop;
        }
        i = 0;
        for(BasicBlock block : cfg.blocks) {
            s += "\\draw[align=left,anchor=west] (" + (intervalsByStart.size()+2) + ",-" + (i+0.5) + ") node {" + block.toString() + ":};\n";
            for (IR ir : block.instructions) {
                s += "\\draw[align=left,anchor=west] (" + (intervalsByStart.size()+4) + ",-" + (i+1) + ") node {\\tt " + ir.toString().replaceAll("\\$", "\\\\\\$") + "};\n";
                i++;
            }
        }
        s += "\\end{tikzpicture}\n";
        return s;
    }

    public void dumpInterval(String filename, String content) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static private class Interval {
        Register register;
        int start = -1;
        int stop = -1;

        Interval(Register register) {
            this.register = register;
        }

        @Override
        public String toString() {
            return start + ".." + stop;
        }
    }
}