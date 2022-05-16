package xyz.soulspace.grammar;

public interface LRSetInterface {
    SLRItem genRootItem();

    void genReduction4Item(int id, SLRItem item);

    ItemGroup closure(SLRItem seed);

    SLRItem step(SLRItem current);
}
