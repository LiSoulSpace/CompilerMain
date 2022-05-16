package xyz.soulspace.grammar;

import java.util.LinkedList;
import java.util.List;

import xyz.soulspace.grammar.GrammarTable.Nonterminal;


/**
 * @author :lisoulspace
 * @create :2022-05-14 17:38
 */
public class SLRSet extends LRSet implements LRSetInterface {

    public SLRSet(GrammarTable.Grammars grammars, GrammarTable.FirstSet firstSet, GrammarTable.FollowSet followSet) {
        super(grammars, firstSet, followSet);
        super.genMoveForItem = this;
    }

    public static List<SLRItem> instances = new LinkedList<>();

    public static SLRItem getItemInstance(Nonterminal left, String[] right, int prefixLength) {
        for (SLRItem item : instances) {
            if (item.right == right && item.left == left && item.prefixLength == prefixLength) {
                return item;
            }
        }
        SLRItem item = new SLRItem(left, right, prefixLength);
        instances.add(item);
        return item;
    }



    @Override
    public SLRItem genRootItem() {
        return null;
    }

    @Override
    public void genReduction4Item(int id, SLRItem item) {

    }

    @Override
    public ItemGroup closure(SLRItem seed) {
        return null;
    }

    @Override
    public SLRItem step(SLRItem current) {
        return null;
    }
}
