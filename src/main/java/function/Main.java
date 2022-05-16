package function;

import Syntax.Grammar.SymbolTable;
import Syntax.Lex.Lexer;
import Syntax.Grammar.GrammarTable;
import Syntax.Grammar.LR1Set;
import Syntax.Grammar.LRParser;
import semantic.grammar;
import word_scanner.wordscanner;

import java.io.File;
import java.net.URI;
import java.util.Scanner;

public class Main {
    private static final boolean IS_DEBUG = false;

    private static final Boolean USE_LR1 = true;
    private static final Boolean GEN_GRAMMAR_TREE = true;

    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String RESOURCES_DIR = USER_DIR + "/src/main/resources";

    private static final String DEBUG_INPUT_FILE_NAME = RESOURCES_DIR + "/srcinput.txt";

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        try {
            System.out.println("开始扫描srcinput.txt");
            Scanner scanner = new Scanner(System.in);
            File inputFile;
            inputFile = new File(DEBUG_INPUT_FILE_NAME);
            scanner.close();

            wordscanner word = new wordscanner();
            word.scanner();
            Lexer l = new Lexer(inputFile);
            if (!l.parse())
                return;

            GrammarTable.cookGramma();
            GrammarTable.printGrammaTableToFile(RESOURCES_DIR + "/Grammar_before.txt");
            GrammarTable.genFirstSet();
            GrammarTable.genFollowSet();
            //结果存入txt文件中
            Utils.printStringToFile(RESOURCES_DIR + "/result/Grammar.txt", GrammarTable.printGrammaTableToString(),
                    GrammarTable.printFirstSetToString(), GrammarTable.printFollowSetToString());
            Utils.printStringToFile(RESOURCES_DIR + "/result/FIRST_SET.txt", GrammarTable.printFirstSetToString());
            Utils.printStringToFile(RESOURCES_DIR + "/result/FOLLOW_SET.txt", GrammarTable.printFollowSetToString());

            grammar grammarTree = null;
            if (GEN_GRAMMAR_TREE)
                grammarTree = new grammar();

            boolean result = false;
            if (USE_LR1) {
                LR1Set lr1Set = new LR1Set(GrammarTable.grammars, GrammarTable.first, GrammarTable.follow);
                lr1Set.genItemGroups();
                Utils.printStringToFile(RESOURCES_DIR + "/result/LR1_Term.txt", lr1Set.printItemGroupsToString());
                Utils.printStringToFile(RESOURCES_DIR + "/result/GoTo_Table.txt", lr1Set.gotoTable.printGotoTableToString());
                Utils.printStringToFile(RESOURCES_DIR + "/result/Action_Table.txt", lr1Set.actionTable.printActionTableToString());

                LRParser lp = new LRParser(lr1Set);
                result = lp.parse(l.getTokens(), grammarTree);
                lp = null;
            }

            if (!result)
                return;

            if (GEN_GRAMMAR_TREE) {
                Utils.printStringToFile(RESOURCES_DIR + "/result/Three_Address_Code.txt", grammarTree.getFullcode());
            }
            Utils.printStringToFile(RESOURCES_DIR + "/result/Symbol_Table.txt", SymbolTable.printSymbolTableToString());
            System.out.println("分析完成，请在result文件夹下观察结果");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

