package xyz.soulspace;

import xyz.soulspace.grammar.GrammarTable;
import xyz.soulspace.grammar.LR1Set;
import xyz.soulspace.grammar.LRParser;
import xyz.soulspace.lexer.Lexer;
import xyz.soulspace.semantic.GrammarTree;
import xyz.soulspace.semantic.Rule;
import xyz.soulspace.symbols.SymbolTable;
import xyz.soulspace.utils.FileOperation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author :lisoulspace
 * @create :2022-05-16 20:11
 */
public class Ex3Main {
    public static final String INPUT_FILE_NAME = "srcinput.txt";
    public static final String MAIN_DIR = System.getProperty("user.dir");
    public static final String RESOURCES_DIR = MAIN_DIR + "/src/main/resources";
    public static final String RESULT2_DIR = RESOURCES_DIR + "/result2";
    public static final String TOKEN_FILE = "tokens.txt";
    public static final String ITEM_GROUPS = "item_groups.txt";
    public static final String ACTION_TABLE = "action_table.txt";
    public static final String GOTO_TABLE = "goto_table.txt";
    public static final String FIRST_SET = "first_set.txt";
    public static final String FOLLOW_SET = "follow_set.txt";
    public static final String THREE_ADDRESS_CODE = "three_address_code.txt";

    public static void main(String[] args) throws IOException {
        File f = new File(RESOURCES_DIR + '/' + INPUT_FILE_NAME);
        Lexer lexer = new Lexer(f);
        lexer.parse();
        String tokensToString = lexer.getTokensToString();
        System.out.println(tokensToString);
        FileOperation.printStringToFile(RESULT2_DIR + "/" + TOKEN_FILE, tokensToString);

        GrammarTable grammarTable = new GrammarTable();
        grammarTable.cookGrammar();
        List<String> nonterminalList = GrammarTable.getNonterminalList();
        System.out.println(nonterminalList);
        List<String> terminalList = GrammarTable.getTerminalList();
        System.out.println(terminalList);
        FileOperation.printStringToFile(RESULT2_DIR + "/" + FIRST_SET, grammarTable.firstSetToString());
        FileOperation.printStringToFile(RESULT2_DIR + "/" + FOLLOW_SET, grammarTable.followSetToString());

        LR1Set lr1Set = new LR1Set(grammarTable.getGrammars(), grammarTable.getFirstSet(), grammarTable.getFollowSet());
        lr1Set.genItemGroups();
        FileOperation.printStringToFile(RESULT2_DIR + "/" + ITEM_GROUPS, lr1Set.itemGroupToString());
        FileOperation.printStringToFile(RESULT2_DIR + "/" + ACTION_TABLE, lr1Set.actionTableToString());
        FileOperation.printStringToFile(RESULT2_DIR + "/" + GOTO_TABLE, lr1Set.gotoTableToString());

        GrammarTree grammarTree = new GrammarTree();

        System.out.println(lr1Set.itemGroupToString());
        LRParser lp = new LRParser(lr1Set);
        boolean result = lp.parse(lexer.getTokenList(), grammarTree);
        System.out.println(result);
        System.out.println(grammarTree.genFullCode());
        FileOperation.printStringToFile(RESULT2_DIR + "/" + THREE_ADDRESS_CODE, grammarTree.genFullCode());
        System.out.println(SymbolTable.printSymbolTableToString());
        System.out.println(Rule.getThreeAddressCode());
    }
}
