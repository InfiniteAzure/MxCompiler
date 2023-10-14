package backend;

import asm.Block;

public class LANode implements Comparable<LANode>{
    public int dfn;
    public Block block;

    public LANode(int dfn, Block block) {
        this.dfn = dfn;
        this.block = block;
    }

    public int compareTo(LANode o) {
        return Integer.compare(this.dfn, o.dfn);
    }

}
