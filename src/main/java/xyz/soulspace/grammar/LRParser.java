package xyz.soulspace.grammar;

import xyz.soulspace.lexer.Token;
import xyz.soulspace.grammar.ActionTable.Action;

import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * @author :lisoulspace
 * @create :2022-05-14 23:32
 */
public class LRParser {
    private Stack<Integer> statusStack;
    private Stack<String> symbolStack;
    private List<Token> input;
    private LRSet lrSet;

    public LRParser(LRSet set) {
        lrSet = set;
        statusStack = new Stack<>();
        symbolStack = new Stack<>();
    }

    public boolean parse(List<Token> tokens) {
        input = tokens;
        statusStack.clear();
        symbolStack.clear();
        statusStack.add(0);
        symbolStack.add(GrammarTable.END);

        int cursor = 0;
        String terminal = input.get(cursor++).toTerminal();
        boolean finished = false;

        do {
            Action action = lrSet.actionTable.getAction(statusStack.peek(), terminal);
            switch (action.mode) {
                case Action.ACTION_SHIFT_IN -> {
                    statusStack.push(action.groupID);
                    symbolStack.push(terminal);
                    if (cursor < input.size()) {
                        terminal = input.get(cursor++).toTerminal();
                    } else terminal = GrammarTable.END;
                }
                case Action.ACTION_REDUCTION -> {
                    assert action.item != null;
                    if (Objects.equals(action.item.right[0], GrammarTable.EMPTY)) {
                        System.out.println(action.groupID);
                        statusStack.push(lrSet.gotoTable.go2(statusStack.peek(), action.item.left));
                        symbolStack.push(action.item.left.getTag());
                        break;
                    } else {
                        System.out.println(statusStack.peek());
                        for (int i = 0; i < Objects.requireNonNull(action.item).right.length; ++i) {
                            statusStack.pop();
                            symbolStack.pop();
                        }
                    }
                    System.out.println(action.item.left);
                    System.out.printf("%s", "[" + statusStack.peek() + "-" + action.item.left + ']');
                    System.out.println(lrSet.gotoTable.go2(statusStack.peek(), action.item.left));
                    statusStack.push(lrSet.gotoTable.go2(statusStack.peek(), action.item.left));
                    symbolStack.push(action.item.left.getTag());
                }
                case Action.ACTION_FINISHED -> {
                    finished = true;
                }
                case Action.ACTION_ERROR -> {
                    System.out.println("Error terminal:" + terminal + "@" + (cursor - 1));

                }
            }
        } while (!finished);
        return true;
    }



    public Stack<Integer> getStatusStack() {
        return statusStack;
    }

    public void setStatusStack(Stack<Integer> statusStack) {
        this.statusStack = statusStack;
    }

    public Stack<String> getSymbolStack() {
        return symbolStack;
    }

    public void setSymbolStack(Stack<String> symbolStack) {
        this.symbolStack = symbolStack;
    }

    public LRSet getLrSet() {
        return lrSet;
    }

    public void setLrSet(LRSet lrSet) {
        this.lrSet = lrSet;
    }
}
