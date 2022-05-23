package xyz.soulspace.grammar;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LR1SetTest {

    @Test
    void closure() throws IOException {
        GrammarTable grammarTable = new GrammarTable();
        grammarTable.cookGrammar();
        LR1Set lr1Set = new LR1Set(grammarTable.getGrammars(), grammarTable.getFirstSet(), grammarTable.getFollowSet());

    }

    @Test
    void closureItr() {
    }

    @Test
    void calEnd4() throws IOException {
        GrammarTable grammarTable = new GrammarTable();
        grammarTable.cookGrammar();

        LR1Item lr1Item =new LR1Item(GrammarTable.nonterminalMap.get("D"), new String[]{"L", "id", ";", "D"}, 2, "$");
        LR1Set lr1Set = new LR1Set(grammarTable.getGrammars(), grammarTable.getFirstSet(), grammarTable.getFollowSet());
        Set<String> strings = lr1Set.calEnd4(lr1Item);
        System.out.println(strings);
    }
}