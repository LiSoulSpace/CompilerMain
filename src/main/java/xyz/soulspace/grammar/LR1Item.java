package xyz.soulspace.grammar;

import xyz.soulspace.grammar.GrammarTable.Nonterminal;

/**
 * @author :lisoulspace
 * @create :2022-05-14 11:57
 */
public class LR1Item extends SLRItem {
    public final String realEnd;

    public LR1Item(Nonterminal left, String[] right, int prefixLength, String end) {
        super(left, right, prefixLength);
        realEnd = end;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(left.getTag());
        sb.append("->");
        for (int i = 0; i < right.length; i++) {
            if (i == prefixLength) sb.append(".");
            sb.append(right[i]);
        }
        if (prefixLength == right.length) {
            sb.append(".");
        }
        if (realEnd != null && !realEnd.equals("")) {
            sb.append("  [").append(realEnd).append("]");
        }
        return sb.toString();
    }
}
