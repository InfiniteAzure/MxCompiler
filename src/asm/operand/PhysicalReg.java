package asm.operand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PhysicalReg extends SimpleReg {
    public String name;

    public PhysicalReg (String Name) {
        this.name = Name;
    }

    public static PhysicalReg zero, ra, sp;
    public static HashMap<String, PhysicalReg> regMap = new HashMap<>();
    public static ArrayList<PhysicalReg> Caller = new ArrayList<>();
    public static ArrayList<PhysicalReg> Callee = new ArrayList<>();
    public static HashSet<PhysicalReg> assignable = new HashSet<>();

    public static PhysicalReg reg(String name) {
        return regMap.get(name);
    }
    public static PhysicalReg regA(int i) { return reg("a" + i); }
    public static PhysicalReg regS(int i) { return reg("s" + i); }
    public static PhysicalReg regT(int i) { return reg("t" + i); }

    static {
        regMap.put("zero", new PhysicalReg("zero"));
        regMap.put("ra", new PhysicalReg("ra"));
        regMap.put("sp", new PhysicalReg("sp"));
        for (int i = 0; i < 7; ++i) {
            var reg = new PhysicalReg("t" + i);
            regMap.put("t" + i, reg);
            Caller.add(reg);
        }
        for (int i = 0; i < 8; ++i) {
            var reg = new PhysicalReg("a" + i);
            regMap.put("a" + i, reg);
            Caller.add(reg);
        }
        for (int i = 0; i < 12; ++i) {
            var reg = new PhysicalReg("s" + i);
            regMap.put("s" + i, reg);
            Callee.add(reg);
        }

        assignable.addAll(Caller);
        assignable.addAll(Callee);

        zero = new PhysicalReg("zero");
        ra = new PhysicalReg("ra");
        sp = new PhysicalReg("sp");
        regMap.put("zero", zero);
        regMap.put("ra", ra);
        regMap.put("sp", sp);
        Caller.add(ra);
        Callee.add(sp);
    }

    public String toString() {
        return name;
    }
}
