package xyz.soulspace.grammar;

import java.util.HashMap;
import java.util.Map;

import xyz.soulspace.grammar.GrammarTable.Nonterminal;

/**
 * @author :lisoulspace
 * @create :2022-05-14 11:12
 */
public class GotoTable {
    private final Map<Integer, Map<Nonterminal, Integer>> gotoTable;

    GotoTable() {
        gotoTable = new HashMap<>();
    }

    public Map<Integer, Map<Nonterminal, Integer>> getGotoTable() {
        return gotoTable;
    }

    public void add(int current, Nonterminal nonterminal, int next) {
        if (!gotoTable.containsKey(current)) {
            gotoTable.put(current, new HashMap<>());
        }
        gotoTable.get(current).put(nonterminal, next);
    }

    public int go2(int current, Nonterminal nonterminal) {
        return gotoTable.get(current).get(nonterminal);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()  ;
        gotoTable.forEach((id, map) -> {
            map.forEach((nonterminal, id2) -> {
                sb.append(id).append(" ").append(nonterminal).append(" ").append(id2).append("\n");
            });
            sb.append("\n");
        });
        return sb.toString();
    }
}
