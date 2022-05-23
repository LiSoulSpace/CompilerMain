package xyz.soulspace.grammar;

import org.junit.jupiter.api.Test;

import java.io.IOException;

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
    void calEnd4() {
    }
}