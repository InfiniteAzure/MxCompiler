package asm;

import asm.operand.GlobalVariable;
import asm.operand.StringConst;

import java.util.ArrayList;

public class asmModule {
    public ArrayList<Function> funcs = new ArrayList<>();
    public ArrayList<GlobalVariable> globalVars = new ArrayList<>();
    public ArrayList<StringConst> stringConsts = new ArrayList<>();

}