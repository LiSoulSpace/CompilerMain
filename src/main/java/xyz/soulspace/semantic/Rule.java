package xyz.soulspace.semantic;

import xyz.soulspace.grammar.SLRItem;
import xyz.soulspace.lexer.*;
import xyz.soulspace.symbols.SymbolTable;

import java.util.*;

/**
 * @author :lisoulspace
 * @create :2022-05-16 13:17
 */
public class Rule {
    private static int labelNum = 1;
    private static int tNum = 1;

    private static final List<String> threeAddressCode = new ArrayList<>();

    public static final int VERSION = 2;

    public static int nextQuad = 0;

    public static class Node {
        public String tag;

        Map<String, String> properties;

        Map<String, Object> properties2;

        public Node(String tag) {
            this.tag = tag;
            properties = new HashMap<>();
            properties2 = new HashMap<>();
        }

        public Node(SLRItem item, List<GrammarTree.TreeNode> children) {
            this(item.left.getTag());
            String code = "", place = "", type = "", next = "";
            if (VERSION == 2) {
                System.out.println(item);
                switch (item.left.getTag()) {
                    case "P" -> {
                        if (item.rightToString().equals("DMK")) {
                            //backPatch((List<Integer>) children.get(2).getValue2("nextList"), nextQuad);
                        }
                    }
                    case "D" -> {
                        if (item.rightToString().equals("Lid;D")) {
                            SymbolTable.setItem((String) children.get(1).getValue2("name"), "type",
                                    (String) children.get(0).getValue2("type"));
                        }
                    }
                    case "L" -> {
                        switch (item.rightToString()) {
                            case "int" -> {
                                setProperties2("type", "int");
                            }
                            case "float" -> {
                                setProperties2("type", "float");
                            }
                            default -> {
                            }
                        }
                    }
                    case "S" -> {
                        switch (item.rightToString()) {
                            case "id=E;" -> {
                                String name = lookup((String) children.get(0).getValue2("name"));
                                if (name == null) System.out.println("Error while finding [" +
                                        children.get(0).getValue2("name") + "]");
                                gen(name + "=" + (String) children.get(2).getValue2("addr"));
                                setProperties2("nextList", null);
                            }
                            case "{K}" -> {
                                setProperties2("nextList", children.get(1).getValue2("nextList"));
                            }
                            case "if(C)MS" -> {
                                backPatch(Collections.unmodifiableList((List<Integer>) children.get(2).getValue2("trueList")),
                                        (int) children.get(4).getValue2("quad"));
                                setProperties2("nextList",
                                        merge((List<Integer>) children.get(2).getValue2("falseList"),
                                                (List<Integer>) children.get(5).getValue2("nextList")));
                            }
                            case "if(C)MSNelseMS" -> {
                                System.out.println(children.get(2).properties2);
                                System.out.println(children.get(4).properties2);
                                System.out.println(children.get(5).properties2);
                                System.out.println(children.get(6).properties2);
                                backPatch((List<Integer>) children.get(2).getValue2("trueList"),
                                        (int) children.get(4).getValue2("quad"));
                                backPatch((List<Integer>) children.get(2).getValue2("falseList"),
                                        (int) children.get(8).getValue2("quad"));
                                setProperties2("nextList", merge(
                                        (List<Integer>) children.get(9).getValue2("nextList"),
                                        merge((List<Integer>) children.get(6).getValue2("nextList"),
                                                (List<Integer>) children.get(5).getValue2("nextList"))));
                            }
                            case "whileM(C)MS" -> {
                                backPatch((List<Integer>) children.get(6).getValue2("nextList"),
                                        (int) children.get(1).getValue2("quad"));
                                backPatch((List<Integer>) children.get(3).getValue2("trueList"),
                                        (int) children.get(5).getValue2("quad"));
                                setProperties2("nextList", children.get(3).getValue2("falseList"));
                                gen("goto " + (int) children.get(1).getValue2("quad"));
                            }
                            default -> {
                            }
                        }
                    }
                    case "C" -> {
                        setProperties2("trueList", makeList(nextQuad));
                        setProperties2("falseList", makeList(nextQuad + 1));
                        if (item.rightToString().equals("E>E")) {
                            gen("if " + (String) children.get(0).getValue2("addr")
                                    + ">" + (String) children.get(2).getValue2("addr") + " goto ");
                        } else if (item.rightToString().equals("E<E")) {
                            gen("if " + (String) children.get(0).getValue2("addr")
                                    + "<" + (String) children.get(2).getValue2("addr") + " goto ");
                        } else if (item.rightToString().equals("E==E")) {
                            gen("if " + (String) children.get(0).getValue2("addr")
                                    + "==" + (String) children.get(2).getValue2("addr") + " goto ");
                        }
                        gen("goto ");
                    }
                    case "E" -> {
                        if (item.rightToString().equals("T")) {
                            setProperties2("addr", children.get(0).getValue2("addr"));
                        } else {
                            String addressT = genTemp();
                            setProperties2("addr", addressT);
                            if (item.rightToString().equals("E+T")) {
                                gen(addressT + "=" + children.get(0).getValue2("addr")
                                        + "+" + children.get(2).getValue2("addr"));
                            } else if (item.rightToString().equals("E-T")) {
                                gen(addressT + "=" + children.get(0).getValue2("addr")
                                        + "-" + children.get(2).getValue2("addr"));
                            }
                        }
                    }
                    case "T" -> {
                        if (item.rightToString().equals("F")) {
                            setProperties2("addr", children.get(0).getValue2("addr"));
                        } else {
                            String addressT = genTemp();
                            setProperties2("addr", addressT);
                            if (item.rightToString().equals("T*F")) {
                                gen(addressT + "=" + children.get(0).getValue2("addr")
                                        + "*" + children.get(2).getValue2("addr"));
                            } else if (item.rightToString().equals("T/F")) {
                                gen(addressT + "=" + children.get(0).getValue2("addr")
                                        + "/-" + children.get(2).getValue2("addr"));
                            }
                        }
                    }
                    case "F" -> {
                        switch (item.rightToString()) {
                            case "(E)" -> {
                                setProperties2("addr", children.get(1).getValue2("addr"));
                            }
                            case "id" -> {
                                setProperties2("addr", lookup((String) children.get(0).getValue2("name")));
                            }
                            case "digits" -> {
                                setProperties2("addr", children.get(0).getValue2("value"));
                            }
                            default -> {

                            }
                        }
                    }
                    case "K" -> {
                        switch (item.rightToString()) {
                            case "S" -> {
                                setProperties2("nextList", children.get(0).getValue2("nextList"));
                            }
                            case "KMS" -> {
                                backPatch((List<Integer>) children.get(0).getValue2("nextList"), nextQuad);
                                setProperties2("nextList", children.get(2).getValue2("nextList"));
                            }
                            default -> {
                            }
                        }
                    }
                    default -> {
                    }
                }
            } else if (VERSION == 1) {
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
        }

        public void addProperty(String property) {
            addProperty(property, "");
        }

        public Node(Token token) {
            this(token.toTerminal());
            properties = new HashMap<>();
            switch (token.getTag()) {
                case Tag.ID -> {
                    setProperty("name", token.getValue());
                    setProperties2("name", token.getValue());
                }
                case Tag.NUM -> {
                    setProperty("value", token.getValue());
                    setProperties2("value", token.getValue());
                }
                case Tag.KEYWORD -> {
                    if (Keyword.WHILE.equals(token.getValue())) {
                        setProperty("root", "");
                        setProperties2("root", "");
                    }
                }
                case "M" -> {
                    System.out.println("M");
                    setProperties2("quad", nextQuad);
                }
                case "N" -> {
                    System.out.println(nextQuad);
                    setProperties2("nextList", makeList(nextQuad));
                    gen("goto ");
                }
                default -> {
                }
            }
        }

        public void backPatch(List<Integer> list, int m) {
            if (list==null)return;
            System.out.println(Arrays.toString(list.toArray()));
            list.forEach(i -> {
                if (threeAddressCode.size() <= i) {
                } else threeAddressCode.set(i, threeAddressCode.get(i) + m);
            });
        }

        public String lookup(String s) {
            if (SymbolTable.getProperties(s) == null) {
                System.out.println("no " + s + " exist");
            }
            return s;
        }

        public List<Integer> makeList(int m) {
            List<Integer> list = new ArrayList<>();
            list.add(m);
            return list;
        }

        public List<Integer> merge(List<Integer> list1, List<Integer> list2) {
            List<Integer> list = new ArrayList<>();
            if (list1 != null) list.addAll(list1);
            if (list2 != null) list.addAll(list2);
            return list;
        }

        public void gen(String threeAddress) {
            threeAddressCode.add(threeAddress);
            nextQuad++;
        }

        public void addProperty(String property, String defaultValue) {
            properties.put(property, defaultValue);
        }

        public void setProperty(String property, String value) {
            properties.put(property, value);
        }

        public void setProperties2(String property, Object o) {
            properties2.put(property, o);
        }

        public String getValue(String property) {
            return properties.getOrDefault(property, "");
        }

        public Object getValue2(String property) {
            return properties2.getOrDefault(property, null);
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

    public static List<String> getThreeAddressCode() {
        return threeAddressCode;
    }

    public static String threeAddressToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < threeAddressCode.size(); i++) {
            sb.append(i).append(" ").append(threeAddressCode.get(i)).append('\n');
        }
        return sb.toString();
    }
}
