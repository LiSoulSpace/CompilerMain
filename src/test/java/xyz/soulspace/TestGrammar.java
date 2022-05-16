package xyz.soulspace;

import org.junit.jupiter.api.Test;
import xyz.soulspace.grammar.GrammarTable;

import java.io.IOException;

/**
 * @author :lisoulspace
 * @create :2022-05-13 10:01
 */
public class TestGrammar {
    @Test
    void GrammarTest() throws IOException {
        GrammarTable grammarTable = new GrammarTable();
        grammarTable.cookGrammar();
        grammarTable.outputNonterminal();
        grammarTable.outputGrammars();
        grammarTable.outputFirstSet();
        grammarTable.outputFollowSet();
    }
}
