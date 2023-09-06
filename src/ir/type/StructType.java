package ir.type;

import java.util.ArrayList;

public class StructType extends BasicType{
    public String name;
    public ArrayList<BasicType> typeList = new ArrayList<>();

    public StructType (String Name) {
        this.name = Name;
    }

    public int size() {
        return 4 * typeList.size();
    }

    public String toString() {
        return this.name;
    }
}
