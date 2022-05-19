package xyz.soulspace.grammar;

import xyz.soulspace.grammar.GrammarTable.Grammars;
import xyz.soulspace.grammar.GrammarTable.FollowSet;
import xyz.soulspace.grammar.GrammarTable.FirstSet;
import xyz.soulspace.grammar.GrammarTable.Nonterminal;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author :lisoulspace
 * @create :2022-05-14 11:01
 */
public class LRSet {
    Grammars grammars;
    FirstSet firstSet;
    FollowSet followSet;
    LRSetInterface genMoveForItem;

    public List<ItemGroup> itemGroups;
    public Set<Integer> finishedGroups;
    public ActionTable actionTable;
    public GotoTable gotoTable;

    private final Map<Integer, Integer> integerSet = new HashMap<>();

    LRSet(Grammars grammars, FirstSet firstSet, FollowSet followSet) {
        this.grammars = grammars;
        this.followSet = followSet;
        this.firstSet = firstSet;
        itemGroups = new ArrayList<>();
        finishedGroups = new HashSet<>();
        actionTable = new ActionTable();
        gotoTable = new GotoTable();
    }

    public void genItemGroups() {
        //获取增广文法中FIR对应的项集闭包
        addGroup(genMoveForItem.closure(genMoveForItem.genRootItem()));
        //从0开始生成项集族
        genItemGroups(0);
    }

    /**
     * 生成id对应的项集放入到finishedGroups
     *
     * @param id 项集id
     */
    public void genItemGroups(int id) {
        if (integerSet.getOrDefault(id, 0) > 3) return;
        integerSet.put(id, integerSet.getOrDefault(id, 0) + 1);
        if (finishedGroups.contains(id)) return;
        AtomicReference<ItemGroup> tempGroup = new AtomicReference<>();
        Map<String, Set<SLRItem>> edgeNGroup = new HashMap<>();
        List<Integer> nextIDs = new LinkedList<>();

        itemGroups.get(id).getItems().forEach(item -> {
            if (item.prefixLength == item.right.length) {
                genMoveForItem.genReduction4Item(id, item);
            } else {
                String edge = item.right[item.prefixLength];
                tempGroup.set(genMoveForItem.closure(genMoveForItem.step(item)));
                if (edgeNGroup.get(edge) != null) {
                    edgeNGroup.get(edge).addAll(tempGroup.get().getItems());
                } else {
                    edgeNGroup.put(edge, tempGroup.get().getItems());
                }
            }
        });

        GrammarTable.getNonterminalList().forEach(edge -> {
            if (edgeNGroup.containsKey(edge)) {
                Nonterminal nonterminal = Nonterminal.getInstanceByTag(edge);
                tempGroup.set(new ItemGroup(edgeNGroup.get(edge)));
                int nextID = addGroup(tempGroup.get());
                if (nonterminal == null) {
                    actionTable.add(id, edge, nextID);
                } else {
                    gotoTable.add(id, nonterminal, nextID);
                }
                nextIDs.add(nextID);
            }
        });

        GrammarTable.getTerminalList().forEach(edge -> {
            if (edgeNGroup.containsKey(edge)) {
                if (Objects.equals(edge, GrammarTable.EMPTY)) {
                    tempGroup.set(new ItemGroup(edgeNGroup.get(edge)));
                    int nextID = addGroup(tempGroup.get());
                    edgeNGroup.get(edge).forEach(item -> {
                        actionTable.add(id, ((LR1Item) item).realEnd, item);
                    });
                } else {
                    Nonterminal nonterminal = Nonterminal.getInstanceByTag(edge);
                    tempGroup.set(new ItemGroup(edgeNGroup.get(edge)));
                    int nextID = addGroup(tempGroup.get());
                    if (nonterminal == null) {
                        actionTable.add(id, edge, nextID);
                    } else {
                        gotoTable.add(id, nonterminal, nextID);
                    }
                    nextIDs.add(nextID);
                }
            }
        });

        nextIDs.forEach(this::genItemGroups);
    }

    public int addGroup(ItemGroup newGroup) {
        for (ItemGroup group : itemGroups) {
            if (group.getItems().containsAll(newGroup.getItems())) {
                if (group.getItems().size() != newGroup.getItems().size()) continue;
                return group.getID();
            }
        }
        itemGroups.add(newGroup);
        newGroup.setID(itemGroups.size() - 1);
        return newGroup.getID();
    }

    public String itemGroupToString() {
        StringBuilder sb = new StringBuilder();
        itemGroups.forEach(group -> {
            sb.append("Group ").append(group.getID()).append(":\n");
            group.getItems().forEach(item -> sb.append(item.toString()).append("\n"));
            //sb.delete(sb.length() - 3, sb.length());
            sb.append("\n");
        });
        return sb.toString();
    }

    public String actionTableToString() {
        return actionTable.toString();
    }

    public String gotoTableToString() {
        return gotoTable.toString();
    }
}
