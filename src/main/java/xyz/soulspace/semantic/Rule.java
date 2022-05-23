package xyz.soulspace.semantic;

import xyz.soulspace.grammar.SLRItem;
import xyz.soulspace.lexer.*;
import xyz.soulspace.symbols.SymbolTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :lisoulspace
 * @create :2022-05-16 13:17
 */
public class Rule {
    private static int labelNum = 1;
    private static int tNum = 1;

    public static class Node {
        public String tag;

        Map<String, String> properties;

        public Node(String tag) {
            this.tag = tag;
            properties = new HashMap<>();
        }

        public Node(SLRItem item, List<GrammarTree.TreeNode> children) {
            this(item.left.getTag());
            String code = "", place = "", type = "", next = "";
            switch (item.left.getTag()) {
                case "P" -> {
                    if (equalStrings(item.right, new String[]{"D", "K"})) {
                        code = children.get(0).getValue("code") +
                                children.get(1).getValue("code") +
                                children.get(1).getValue("next");
                    }
                    setProperty("code", code);
                }
                case "D" -> {
                    code = "";
                    String name = children.get(1).getValue("name");
                    type = children.get(0).getValue("code");
                    code += type + " " + name + '\n';
                    if (equalStrings(item.right, new String[]{"L", Tag.ID, Operator.LNND, "D"})) {
                        code += children.get(3).getValue("code");
                    }
                    SymbolTable.setItem(name, "type", type);
                    setProperty("code", code);
                }
                case "L" -> {
                    if (equalStrings(item.right, new String[]{Type.INT})) {
                        type = "int";
                    } else if (equalStrings(item.right, new String[]{Type.FLOAT})) {
                        type = "float";
                    }
                    setProperty("code", type);
                }
                case "S" -> {
                    if (equalStrings(item.right, new String[]{Tag.ID, Operator.ASSIGN, "E", Operator.LNND})) {
                        code = children.get(2).getValue("code") + children.get(0).getValue("name")
                                + "=" + children.get(2).getValue("place") + '\n';
                    } else if (equalStrings(item.right, new String[]{Keyword.IF, Operator.LPAR, "C", Operator.RPAR, "S"})) {
                        //children.get(2).setProperty("true", genLabel());
                        code = children.get(2).getValue("code") +
                                children.get(2).getValue("true") +
                                children.get(4).getValue("code") + "L" + String.valueOf(labelNum - 1) + ":";
                    } else if (equalStrings(item.right, new String[]{Keyword.IF, Operator.LPAR, "C", Operator.RPAR, "S",
                            Keyword.ELSE, "S"})) {
                        next = genLabel();
                        code = children.get(2).getValue("code")
                                + children.get(2).getValue("true")
                                + children.get(4).getValue("code")
                                + "goto " + next + '\n' + children.get(2).getValue("false")
                                + children.get(6).getValue("code");
                    } else if (equalStrings(item.right, new String[]{Keyword.WHILE, Operator.LPAR, "C", Operator.RPAR, "S"})) {
                        String root = genLabel();
                        children.get(4).setProperty("next", root);
                        code = root + children.get(2).getValue("code") + children.get(2).getValue("true") +
                                children.get(4).getValue("code") + "goto " + root + '\n'
                                + children.get(2).getValue("false");
                    } else if (equalStrings(item.right, new String[]{Operator.LBPAR, "K", Operator.RBPAR})) {
                        code = children.get(1).getValue("code");
                    }
                    setProperty("code", code);
                    setProperty("next", next);
                }
                case "K" -> {
                    if (equalStrings(item.right, new String[]{"S"})) {
                        code = children.get(0).getValue("code");
                    } else if (equalStrings(item.right, new String[]{"K", "S"})) {
                        code = children.get(0).getValue("code")
                                + children.get(1).getValue("code")
                                + children.get(1).getValue("next");
                    }
                    setProperty("code", code);
                }
                case "C" -> {
                    code = children.get(0).getValue("code") + children.get(2).getValue("code")
                            + "if(" + children.get(0).getValue("place");
                    setProperty("true", genLabel());
                    setProperty("false", genLabel());
                    if (equalStrings(item.right, new String[]{"E", Operator.MORE, "E"})) {
                        code += Operator.MORE;
                    } else if (equalStrings(item.right, new String[]{"E", Operator.LESS, "E"})) {
                        code += Operator.LESS;
                    } else if (equalStrings(item.right, new String[]{"E", Operator.EQU, "E"})) {
                        code += Operator.EQU;
                    }
                    code += children.get(2).getValue("place") + ") goto " + getValue("true") + "\ngoto "
                            + getValue("false") + '\n';
                    setProperty("code", code);
                }
                case "E" -> {
                    if (equalStrings(item.right, new String[]{"T"})) {
                        place = children.get(0).getValue("place");
                        code = children.get(0).getValue("code");
                        type = children.get(0).getValue("type");
                    } else {
                        place = genTemp();
                        code = children.get(0).getValue("code")
                                + children.get(2).getValue("code")
                                + place + "=" + children.get(0).getValue("place");
                        if (equalStrings(item.right, new String[]{"E", Operator.PLUS, "T"})) {
                            code += "+";
                        } else if (equalStrings(item.right, new String[]{"E", Operator.MINUS, "T"})) {
                            code += "-";
                        }
                        code += children.get(2).getValue("place") + '\n';
                        type = getLonger(children.get(0).getValue("type"), children.get(2).getValue("type"));
                        SymbolTable.setItem(place, "type", type);
                    }
                    setProperty("type", type);
                    setProperty("place", place);
                    setProperty("code", code);
                }
                case "T" -> {
                    if (equalStrings(item.right, new String[]{"F"})) {
                        place = children.get(0).getValue("place");
                        code = children.get(0).getValue("code");
                        type = children.get(0).getValue("type");
                    } else {
                        place = genTemp();
                        code = children.get(0).getValue("code")
                                + children.get(2).getValue("code") + place
                                + "=" + children.get(0).getValue("place");
                        if (equalStrings(item.right, new String[]{"T", Operator.MULT, "F"})) {
                            code += "*";
                        } else if (equalStrings(item.right, new String[]{"T", Operator.DIV, "F"})) {
                            code += "/";
                        }
                        code += children.get(2).getValue("place") + '\n';
                        type = getLonger(children.get(0).getValue("type"), children.get(2).getValue("type"));
                        SymbolTable.setItem(place, "type", type);
                    }
                    setProperty("type", type);
                    setProperty("place", place);
                    setProperty("code", code);
                }
                case "F" -> {
                    if (equalStrings(item.right, new String[]{Operator.LPAR, "E", Operator.RPAR})) {
                        place = children.get(1).getValue("place");
                        code = children.get(1).getValue("code");
                        type = children.get(1).getValue("type");
                    } else if (equalStrings(item.right, new String[]{Tag.ID})) {
                        place = children.get(0).getValue("name");
                        type = SymbolTable.getProperty(place, "type");
                        code = "";
                    } else if (equalStrings(item.right, new String[]{Tag.NUM})) {
                        place = children.get(0).getValue("value");
                        if (place.contains(".")) type = "float";
                        else type = "int";
                        code = "";
                    }
                    setProperty("place", place);
                    setProperty("code", code);
                    setProperty("type", type);
                }
                default -> {

                }
            }

        }

        public void addProperty(String property) {
            addProperty(property, "");
        }

        public Node(Token token) {
            this(token.toTerminal());
            properties = new HashMap<>();
            switch (token.getTag()) {
                case Tag.ID -> setProperty("name", token.getValue());
                case Tag.NUM -> setProperty("value", token.getValue());
                case Tag.KEYWORD -> {
                    if (Keyword.WHILE.equals(token.getValue())) {
                        setProperty("root", "");
                    }
                }
                default -> {
                }
            }
        }

        public void addProperty(String property, String defaultValue) {
            properties.put(property, defaultValue);
        }

        public void setProperty(String property, String value) {
            properties.put(property, value);
        }

        public String getValue(String property) {
            return properties.getOrDefault(property, "");
        }

        @Override
        public String toString() {
            return tag;
        }
    }

    public static boolean equalStrings(String[] left, String[] right) {
        if (left.length != right.length) return false;
        for (int i = 0; i < left.length; ++i) {
            if (!left[i].equals(right[i])) return false;
        }
        return true;
    }

    private static String genLabel() {
        return "L" + (labelNum++) + ":";
    }

    private static String genTemp() {
        SymbolTable.setItem("t" + String.valueOf(tNum), "type", "int");
        return "t" + (tNum++);
    }

    private static String getLonger(String a, String b) {
        if (a.equals("float") || b.equals("float")) return "float";
        else return "int";
    }

    public static int getLabelNum() {
        return labelNum;
    }

    public static int gettNum() {
        return tNum;
    }
}
