package xyz.soulspace.grammar;

import xyz.soulspace.grammar.GrammarTable.Grammars;
import xyz.soulspace.grammar.GrammarTable.Nonterminal;
import xyz.soulspace.grammar.GrammarTable.FollowSet;
import xyz.soulspace.grammar.GrammarTable.FirstSet;
import xyz.soulspace.grammar.GrammarTable.GrammarRightSide;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author :lisoulspace
 * @create :2022-05-14 19:06
 */
public class LR1Set extends LRSet implements LRSetInterface {

    public LR1Set(Grammars grammars, FirstSet firstSet, FollowSet followSet) {
        super(grammars, firstSet, followSet);
        super.genMoveForItem = this;
    }

    public List<LR1Item> instances = new LinkedList<>();

    /**
     * 计算 项seed 的闭包
     *
     * @param seed 要计算的项
     * @return seed对应的闭包
     */
    @Override
    public ItemGroup closure(SLRItem seed) {
        Set<SLRItem> items = new HashSet<>();
        items.add(seed);
        boolean isChanged = true;
        while (isChanged) {
            if (seed.prefixLength < seed.right.length) {
                Nonterminal nonterminal = Nonterminal.getInstanceByTag(seed.right[seed.prefixLength]);
                if (nonterminal != null) {
                    isChanged = closureItr(items, nonterminal, calEnd4((LR1Item) seed));
                } else isChanged = false;
            } else isChanged = false;
        }
        return new ItemGroup(items);
    }

    /**
     * 将【项seed】点后非终结符对应的新项加入到项集中
     *
     * @param group 项集
     * @param seed  项
     * @param ends  向前看终结符号集合
     */
    public boolean closureItr(Set<SLRItem> group, Nonterminal seed, Set<String> ends) {
        AtomicBoolean isChanged = new AtomicBoolean(false);
        Set<SLRItem> newItems = new HashSet<>();
        getRightSides(seed).rightSides.forEach(right -> {
            ends.forEach(end -> {
                if (!end.equals(GrammarTable.EMPTY)) {
                    LR1Item item = getItemInstance(seed, right, 0, end);
                    if (!group.contains(item)) {
                        newItems.add(item);
                    }
                }
            });
        });
        if (newItems.size() == 0) {
            return false;
        } else isChanged.set(true);
        AtomicBoolean isOver = new AtomicBoolean(true);
        boolean b1 = group.addAll(newItems);
        if (!b1) return false;
        newItems.forEach(item -> {
            Nonterminal nonterminal = Nonterminal.getInstanceByTag(item.right[0]);
            if (nonterminal != null) {
                boolean b = closureItr(group, nonterminal, calEnd4((LR1Item) item));
                if (b) isOver.set(false);
            }
        });
        if (isOver.get()) isChanged.set(false);
        return isChanged.get();
    }

    /**
     * 获取【项seed】对应的向前看终结符号
     *
     * @param seed 项
     * @return 项对应的向前看终结符号集合
     */
    public Set<String> calEnd4(LR1Item seed) {
        Set<String> ends;
        if (seed.prefixLength < seed.right.length - 1) {
            Nonterminal rear = Nonterminal.getInstanceByTag(seed.right[seed.prefixLength + 1]);
            if (rear != null) {
                if (rear.getTag().equals("M") || rear.getTag().equals("N")) {
                    Nonterminal rear2 = Nonterminal.getInstanceByTag(seed.right[seed.prefixLength + 2]);
                    if (rear2 != null)
                        ends = firstSet.get(Nonterminal.getInstanceByTag(seed.right[seed.prefixLength + 2]));
                    else {
                        ends = new HashSet<>();
                        ends.add(seed.right[seed.prefixLength + 2]);
                    }
                } else {
                    ends = firstSet.get(rear);
                }
            } else {
                ends = new HashSet<>();
                ends.add(seed.right[seed.prefixLength + 1]);

            }
        } else {
            ends = new HashSet<>();
            ends.add(seed.realEnd);
        }
        return ends;
    }

    public Set<String> calEnd4(Nonterminal seed, String[] right, int prefixLength, String end) {
        return calEnd4(getItemInstance(seed, right, 0, end));
    }

    public LR1Item getItemInstance(Nonterminal left, String[] right, int prefixLength, String end) {
        for (LR1Item item : instances) {
            if (item.right == right && item.left == left && item.prefixLength == prefixLength && item.realEnd.equals(end)) {
                return item;
            }
        }

        LR1Item item = new LR1Item(left, right, prefixLength, end);
        instances.add(item);
        return item;
    }

    public GrammarRightSide getRightSides(Nonterminal n) {
        return grammars.get(n).right;
    }

    @Override
    public SLRItem genRootItem() {
        Nonterminal ig = Nonterminal.getInstanceByTag(GrammarTable.fir);
        return new LR1Item(ig, getRightSides(ig).get(0), 0, GrammarTable.END);
    }

    @Override
    public void genReduction4Item(int id, SLRItem item) {
        if (item.left == Nonterminal.getInstanceByTag(GrammarTable.fir))
            actionTable.add(id, GrammarTable.END);
        else actionTable.add(id, ((LR1Item) item).realEnd, item);
    }


    @Override
    public SLRItem step(SLRItem current) {
        return getItemInstance(current.left, current.right,
                current.prefixLength + 1, ((LR1Item) current).realEnd);
    }
}
