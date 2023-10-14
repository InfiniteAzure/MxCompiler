package ir;

import tools.TextUtils;

import java.io.PrintStream;

public class IRPrinter {
    public PrintStream print;
    public String name;

    public IRPrinter(PrintStream Print, String Name) {
        this.print = Print;
        this.name = Name;
    }
    public void print(Module module) {
        print.printf("; ModuleID = '%s'\n", name);
        print.printf("source_filename = \"%s\"\n", name);
        print.println("target datalayout = \"e-m:e-p:32:32-p270:32:32-p271:32:32-p272:64:64-f64:32:64-f80:32-n8:16:32-S128\"");
        print.println("target triple = \"i386-pc-linux-gnu\"");

        for (var func : module.functionsDeclarations) {
            this.declare(func);
        }
        for (var v : module.globalVariables) {
            print.println(v);
        }
        for (var s : module.strings) {
            print.println(s);
        }
        for (var cls : module.classes) {
            print.printf("%s = type {%s}\n", cls, TextUtils.join(cls.typeList));
        }
        for (var func : module.functions) {
            this.print(func);
        }
    }
    public void declare(Function func) {
        var type = func.type();
        print.printf("declare %s %s(%s)\n",
                type.Return, func.name,
                TextUtils.join(type.parameters));
    }

    public void print(Function func) {
        var type = func.type();
        print.printf("define %s %s(%s) {\n",
                type.Return, func.name,
                TextUtils.join(func.Op, x -> x.nameWithType()));
        for (var i: func.blocks) {
            this.print(i);
            print.println();
        }
        print.println("}\n");
    }

    public void print(BasicBlock block) {
        print.printf("%s:  ; preds = %s\n", block.name, TextUtils.join(block.prev));
        for (var i : block.phiInstructions) {
            print.println("  " + i);
        }
        for (var i : block.instructions) {
            print.println("  " + i);
        }
    }

}
