package xyz.soulspace.grammar;

import java.util.HashMap;
import java.util.Map;

/**
 * @author :lisoulspace
 * @create :2022-05-14 11:12
 */
public class ActionTable {
    public static class Action {
        public static final String ACTION_REDUCTION = "Reduction";
        public static final String ACTION_SHIFT_IN = "ShiftIn";
        public static final String ACTION_FINISHED = "acc";
        public static final String ACTION_ERROR = "error";
        public final String mode;
        public final int groupID;
        public final SLRItem item;

        Action(String m) {
            this(m, -1);
        }

        Action(String m, int id) {
            mode = m;
            groupID = id;
            item = null;
        }

        Action(String m, SLRItem item) {
            mode = m;
            groupID = -1;
            this.item = item;
        }

        Action(int groupID, String m, SLRItem item){
            mode = m;
            this.groupID = groupID;
            this.item = item;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            switch (mode) {
                case ACTION_REDUCTION -> {
                    assert item != null;
                    sb.append("r").append("(").append(item).append(")");
                }
                case ACTION_SHIFT_IN -> {
                    sb.append("s").append(groupID);
                }
                default -> sb.append(mode);
            }
            return sb.toString();
        }
    }

    private final Map<Integer, Map<String, Action>> actionTable;

    ActionTable() {
        actionTable = new HashMap<>();
    }

    public void add(int id, String terminal, int nextID) {

        Action newAction = new Action(Action.ACTION_SHIFT_IN, nextID);
        add(id, terminal, newAction);
    }

    public void add(int id, String terminal, SLRItem item) {
        Action newAction = new Action(id, Action.ACTION_REDUCTION, item);
        add(id, terminal, newAction);
    }

    public void add(int id, String terminal) {
        add(id, terminal, new Action(Action.ACTION_FINISHED));
    }

    public void add(int id, String terminal, Action newAction) {
        if (!actionTable.containsKey(id)) {
            actionTable.put(id, new HashMap<>());
        }
        if (actionTable.get(id).containsKey(terminal)) {

        }
        actionTable.get(id).put(terminal, newAction);
    }

    public Action getAction(int id, String terminal) {
        Action action = actionTable.get(id).get(terminal);
        if (action == null)
            action = new Action(Action.ACTION_ERROR);
        return action;
    }

    public Map<Integer, Map<String, Action>> getActionTable() {
        return actionTable;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        actionTable.forEach((key, value) -> {
            value.forEach((s, action) -> {
                sb.append(key).append(" ").append(s).append(" ").append(action).append("\n");
            });
            sb.append("\n");
        });
        return sb.toString();
    }
}
