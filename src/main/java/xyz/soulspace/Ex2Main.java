package xyz.soulspace;

import xyz.soulspace.grammar.LRParser;
import xyz.soulspace.grammar.LR1Set;
import xyz.soulspace.lexer.Lexer;
import xyz.soulspace.grammar.GrammarTable;
import xyz.soulspace.utils.FileOperation;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author :lisoulspace
 * @create :2022-05-14 20:32
 */
public class Ex2Main {
    public static final String INPUT_FILE_NAME = "srcinput.txt";
    public static final String MAIN_DIR = System.getProperty("user.dir");
    public static final String RESOURCES_DIR = MAIN_DIR + "/src/main/resources";

    public static final String RESULT2_DIR = RESOURCES_DIR+"/result2";

    public static final String ITEM_GROUPS = "item_groups.txt";

    public static final String ACTION_TABLE = "action_table.txt";

    public static final String GOTO_TABLE = "goto_table.txt";

    public static void main(String[] args) throws IOException {
        File f = new File(RESOURCES_DIR + '/' + INPUT_FILE_NAME);
        Lexer lexer = new Lexer(f);
        lexer.parse();
        String tokensToString = lexer.getTokensToString();
        System.out.println(tokensToString);

        GrammarTable grammarTable = new GrammarTable();
        grammarTable.cookGrammar();
        List<String> nonterminalList = GrammarTable.getNonterminalList();
        System.out.println(nonterminalList);
        List<String> terminalList = GrammarTable.getTerminalList();
        System.out.println(terminalList);

        LR1Set lr1Set = new LR1Set(grammarTable.getGrammars(), grammarTable.getFirstSet(), grammarTable.getFollowSet());
        lr1Set.genItemGroups();
        FileOperation.printStringToFile(RESULT2_DIR+"/"+ITEM_GROUPS, lr1Set.itemGroupToString());
        FileOperation.printStringToFile(RESULT2_DIR+"/"+ACTION_TABLE, lr1Set.actionTableToString());
        FileOperation.printStringToFile(RESULT2_DIR+"/"+GOTO_TABLE, lr1Set.gotoTableToString());

        System.out.println(lr1Set.itemGroupToString());
        LRParser lp = new LRParser(lr1Set);
        boolean result = lp.parse(lexer.getTokenList());
        System.out.println(result);

    }
}
