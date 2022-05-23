package xyz.soulspace.grammar;

import xyz.soulspace.grammar.GrammarTable.Nonterminal;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author :lisoulspace
 * @create :2022-05-14 11:51
 */
public class SLRItem {
    public final Nonterminal left;
    public final String[] right;
    public final int prefixLength;

    public SLRItem(Nonterminal left, String[] right, int prefixLength) {
        this.left = left;
        this.right = right;
        this.prefixLength = prefixLength;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(left.getTag());
        sb.append("->");
        for (int i = 0; i < right.length; i++) {
            if (i == prefixLength) {
                sb.append(".");
            }
            sb.append(right[i]);
        }
        if (prefixLength == right.length) {
            sb.append(".");
        }
        return sb.toString();
    }

    public String rightToString() {
        StringBuffer sb = new StringBuffer();
        for (String s : right) {
            sb.append(s);
        }
        return sb.toString();

    }
}
