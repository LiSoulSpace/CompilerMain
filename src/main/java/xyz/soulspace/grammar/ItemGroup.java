package xyz.soulspace.grammar;

import java.util.Set;

/**
 * @author :lisoulspace
 * @create :2022-05-14 16:39
 */
public class ItemGroup {
    private int ID;
    private Set<SLRItem> items;

    ItemGroup(Set<SLRItem> items) {
        this.items = items;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public void setItems(Set<SLRItem> items) {
        this.items = items;
    }

    public Set<SLRItem> getItems() {
        return items;
    }
}
