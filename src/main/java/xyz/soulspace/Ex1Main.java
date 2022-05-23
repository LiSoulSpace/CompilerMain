package xyz.soulspace;

import xyz.soulspace.lexer.Lexer;
import xyz.soulspace.symbols.SymbolTable;

import java.io.File;
import java.io.IOException;

/**
 * @author :lisoulspace
 * @create :2022-05-10 21:41
 */
public class Ex1Main {
    public static final String INPUT_FILE_NAME = "srcinput.txt";
    public static final String MAIN_DIR = System.getProperty("user.dir");
    public static final String RESOURCES_DIR = MAIN_DIR + "/src/main/resources";

    public static void main(String[] args) throws IOException {
        File f = new File(RESOURCES_DIR + '/' + INPUT_FILE_NAME);
        Lexer lexer = new Lexer(f);
        lexer.parse2();
        System.out.println(lexer.getTokensToString());
        System.out.println(SymbolTable.printSymbolTableToString());
    }
}
