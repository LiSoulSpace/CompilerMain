package xyz.soulspace.grammar;

import java.io.*;
import java.util.*;

/**
 * @author :lisoulspace
 * @create :2022-05-12 15:15
 */
public class GrammarTable {
    public static final String EMPTY = "\u03B5";
    public static final String END = "$";
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String RESOURCES_DIR = USER_DIR + "/src/main/resources";
    public static final String fir = "fir";
    public static String grammarFileName = "Grammar_semantic.txt";
//public static String grammarFileName = "Grammar_main.txt";
    public static Map<String, Nonterminal> nonterminalMap = new HashMap<>();

    public static List<String> nonterminalList = new ArrayList<>();

    public static Set<String> terminalSet = new HashSet<>();

    public static List<String> terminalList = new ArrayList<>();

    private final Grammars grammars;
    private final FirstSet firstSet;
    private final FollowSet followSet;
    private String srcBuffer;

    public GrammarTable() {
        grammars = new Grammars();
        firstSet = new FirstSet();
        followSet = new FollowSet();
    }

    public GrammarTable(String grammarFileName) {
        this();
        GrammarTable.grammarFileName = grammarFileName;
    }

    public void cookGrammar() throws IOException {
        generateFullGrammar();
        genFirstSet();
        genFollowSet();
    }

    /**
     * 读取文法文件并且获取文法
     *
     * @throws IOException IOException
     */
    private void generateFullGrammar() throws IOException {
        String grammarFilePath = RESOURCES_DIR + '/' + grammarFileName;
        File f = new File(grammarFilePath);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
            String temp = "";
            List<String> grammarList = new ArrayList<>();
            while (true) {
                temp = bufferedReader.readLine();
                if (temp == null) break;
                grammarList.add(temp);
            }
            for (String s : grammarList) {
                String[] res = s.split("->");
                String left = res[0].trim();
                Nonterminal n = new Nonterminal(left);
            }
            for (String s : grammarList) {
                String[] res = s.split("->");
                String left = res[0].trim();
                String[] right = res[1].trim().split("\\|");
                for (int i = 0; i < right.length; i++) {
                    String[] split = right[i].trim().split(" ");
                    if (split.length == 0) continue;
                    grammars.put(new Nonterminal(left),
                            grammars.getOrDefault(new Nonterminal(left),
                                    new GrammarFomula(new Nonterminal(left), new GrammarRightSide(split))));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从增广文法头部开始 fir 计算FirstSet
     */
    private void genFirstSet() {
        String[] strS = grammars.get(new Nonterminal("fir")).right.rightSides.get(0);
        //获取S的First集
        firstSet.put(Nonterminal.getInstanceByTag(strS[0]), genFirstSet(new Nonterminal(strS[0])));
        //对于每一个文法语句，获取文法左侧非终结符对应的First集
        grammars.values().forEach(grammarFomula -> {
            if (!firstSet.containsKey(grammarFomula.left)) {
                firstSet.put(grammarFomula.left, genFirstSet(grammarFomula.left));
            }
        });
    }

    /**
     * 计算某一个非终结符的First集
     *
     * @param n 非终结符
     * @return n的First集
     */
    private Set<String> genFirstSet(Nonterminal n) {
        if (firstSet.get(n) != null) {
            return firstSet.get(n);
        }
        Set<String> tempFirst = new HashSet<>();
        GrammarRightSide rightSide = getRightSides(n);
        rightSide.rightSides.forEach(gen -> {
            Nonterminal nonterminal = Nonterminal.getInstanceByTag(gen[0]);
            if (gen[0].equals(EMPTY)) {
                tempFirst.add(EMPTY);
            } else if (nonterminal != null) {
                boolean isEmpty = nonterminal.hasEmpty();
                int i = 0;
                do {
                    Nonterminal eleI = Nonterminal.getInstanceByTag(gen[i++]);
                    if (eleI == null) {
                        tempFirst.add(gen[i - 1]);
                        break;
                    }
                    if (eleI != n) {
                        tempFirst.addAll(genFirstSet(eleI));
                    }
                    isEmpty = eleI.hasEmpty();
                    //System.out.println("CeShi:{" + n.tag + "}  " + eleI.tag + ":" + tempFirst + " " + isEmpty);

                } while (i < gen.length && isEmpty);
            } else {
                tempFirst.add(gen[0]);
            }
        });
        firstSet.put(n, tempFirst);
        return firstSet.get(n);
    }

    private void genFollowSet() {
        List<String> igFollow = new ArrayList<>();
        igFollow.add(END);
        Map<Nonterminal, Set<Nonterminal>> depends = new HashMap<>();
        FollowSet tempFollow = new FollowSet();
        grammars.values().forEach(grammarFomula -> {
            tempFollow.put(grammarFomula.left, new HashSet<>());
            depends.put(grammarFomula.left, new HashSet<>());
        });
        tempFollow.get(Nonterminal.getInstanceByTag(fir)).add(END);
        //计算FollowSet依赖
        grammars.values().forEach(grammarFomula -> {
            calFollowSetDepends(depends, tempFollow, grammarFomula);
        });

        outputDepends(depends);

        while (!depends.isEmpty()) {
            Nonterminal root = getRootFromDepends(depends);
            genFollowSet(depends, tempFollow, root);
            depends.remove(root);
        }

        tempFollow.keySet().forEach(nonterminal -> {
            followSet.put(nonterminal, tempFollow.get(nonterminal));
        });
    }

    private void genFollowSet(Map<Nonterminal, Set<Nonterminal>> depends, FollowSet tempFollow, Nonterminal root) {
        if (root == null) return;
        for (Nonterminal child : depends.get(root)) {
            tempFollow.get(child).addAll(tempFollow.get(root));
            genFollowSet(depends, tempFollow, child);
        }
    }

    public void calFollowSetDepends(Map<Nonterminal, Set<Nonterminal>> depends, FollowSet tempFollow, GrammarFomula fomula) {
        GrammarRightSide rights = fomula.right;
        for (String[] STR : rights.rightSides) {
            Nonterminal nonterminal = null;
            for (int i = 0; i < STR.length; ++i) {
                boolean CBL = false;
                nonterminal = Nonterminal.getInstanceByTag(STR[i]);
                if (nonterminal == null) continue;
                else if (i == STR.length - 1) {
                    CBL = true;
                }
                for (int j = i + 1; j < STR.length; ++j) {
                    Nonterminal next = Nonterminal.getInstanceByTag(STR[j]);
                    if (next == null) {
                        tempFollow.get(nonterminal).add(STR[i + 1]);
                        CBL = false;
                        break;
                    } else {
                        tempFollow.get(nonterminal).addAll(firstSet.get(next));
                        if (!next.hasEmpty()) {
                            CBL = false;
                            break;
                        }
                    }
                }
                if (CBL && !Objects.equals(fomula.left, nonterminal)) {
                    depends.get(fomula.left).add(nonterminal);
                }
            }
        }
    }

    public Nonterminal getRootFromDepends(Map<Nonterminal, Set<Nonterminal>> depends) {
        Nonterminal root = null;
        for (Nonterminal parent : depends.keySet()) {
            root = parent;
            break;
        }

        Nonterminal temp = getParent(depends, root);
        while (temp != null) {
            root = temp;
            temp = getParent(depends, root);
        }
        return root;
    }

    public Nonterminal getParent(Map<Nonterminal, Set<Nonterminal>> depends, Nonterminal child) {
        for (Nonterminal parent : depends.keySet()) {
            if (depends.get(parent).contains(child))
                return parent;
        }
        return null;
    }

    /**
     * 非终结符
     */
    public class Nonterminal {
        private final String tag;

        public Nonterminal(String tag) {
            this.tag = tag;
            if (!nonterminalMap.containsKey(tag))
                nonterminalList.add(tag);
            nonterminalMap.put(tag, this);
        }

        public String getTag() {
            return tag;
        }

        public boolean hasEmpty() {
            //产生式右部为空返回
            return Objects.requireNonNull(getRightSides(this)).hasEmpty();
        }

        public static Nonterminal getInstanceByTag(String tag) {
            return nonterminalMap.getOrDefault(tag, null);
        }

        @Override
        public String toString() {
            return tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Nonterminal that = (Nonterminal) o;
            return Objects.equals(tag, that.tag);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tag);
        }
    }

    /**
     * 语法表达式
     */
    public class GrammarFomula {
        public Nonterminal left;
        public GrammarRightSide right;

        public GrammarFomula(Nonterminal l, GrammarRightSide r) {
            if (grammars.containsKey(l)) {
                grammars.get(l).right.addParams(r.rightSides.get(0));
            } else {
                left = l;
                right = r;
            }
        }

        public GrammarFomula addRight(String[] params) {
            right.addParams(params);
            return this;
        }

        @Override
        public String toString() {
            return left.toString() + "->" + right.toString();
        }
    }


    /**
     * 语法表达式右侧
     */
    public static class GrammarRightSide {
        public List<String[]> rightSides = new ArrayList<>();

        public GrammarRightSide(String[]... params) {
            rightSides.addAll(Arrays.asList(params));
        }

        public GrammarRightSide(String[] params) {
            rightSides.add(params);
            Arrays.stream(params).forEach(s -> {
                if (!nonterminalMap.containsKey(s) && !terminalSet.contains(s)) {
                    terminalSet.add(s);
                    terminalList.add(s);
                }
            });
        }

        public GrammarRightSide() {
        }

        public void addParams(String[] params) {
            rightSides.add(params);
        }

        public boolean hasEmpty() {
            for (String[] gen : rightSides) {
                //System.out.println("from has Empty : " + gen[0] + " " + EMPTY);
                if (Objects.equals(gen[0], EMPTY)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @param pos 位置
         * @return 返回列表pos处的产生式
         */
        public String[] get(int pos) {
            return rightSides.get(pos);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            rightSides.forEach(strings -> sb.append(Arrays.toString(strings)).append('|'));
            sb.delete(sb.length() - 1, sb.length());
            return sb.toString();
        }
    }

    public static class FirstSet extends HashMap<Nonterminal, Set<String>> {
    }

    public static class FollowSet extends HashMap<Nonterminal, Set<String>> {
    }

    public static class Grammars extends HashMap<Nonterminal, GrammarFomula> {
    }

    public void outputGrammars() {
        grammars.values().forEach(System.out::println);
    }

    public String firstSetToString() {
        StringBuilder sb = new StringBuilder();
        firstSet.forEach((nonterminal, strings) -> {
            sb.append(nonterminal.toString()).append(":").append(Arrays.toString(strings.toArray())).append("\n");
        });
        return sb.toString();
    }

    public String followSetToString() {
        StringBuilder sb = new StringBuilder();
        followSet.forEach((nonterminal, strings) -> {
            sb.append(nonterminal.toString()).append(":").append(Arrays.toString(strings.toArray())).append("\n");
        });
        return sb.toString();
    }

    public GrammarRightSide getRightSides(Nonterminal n) {
        if (grammars.get(n) == null) return new GrammarRightSide();
        return grammars.get(n).right;
    }

    public FollowSet getFollowSet() {
        return followSet;
    }

    public FirstSet getFirstSet() {
        return firstSet;
    }

    public Grammars getGrammars() {
        return grammars;
    }

    public void outputFirstSet() {
        System.out.println("First Set: \n" + firstSetToString() + "\n");
    }

    public void outputFollowSet() {
        System.out.println("Follow Set : \n" + followSetToString() + "\n");
    }

    public void outputNonterminal() {
        System.out.println(nonterminalMap.keySet());
    }

    public void outputDepends(Map<Nonterminal, Set<Nonterminal>> data) {
        StringBuilder temp = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        for (Nonterminal parent : data.keySet()) {
            temp = new StringBuilder(parent.tag + "->");
            for (Nonterminal child : data.get(parent)) {
                temp.append(child.tag).append(" | ");
            }
            sb.append(temp).append('\n');
        }
        System.out.println(sb.toString());
    }

    public static List<String> getTerminalList() {
        return terminalList;
    }

    public static List<String> getNonterminalList() {
        return nonterminalList;
    }
}
