package xyz.soulspace.lexer;

import xyz.soulspace.symbols.SymbolTable;

/**
 * @author :lisoulspace
 * @create :2022-05-10 21:28
 */
public class Token {
    private final String tag;

    public Token(String tag) {
        this.tag = tag;
    }

    public Token(String id, String tag) {
        this(tag);
        SymbolTable.setItem(id, " ", tag);
    }

    public String getTag() {
        return tag;
    }

    public String getValue() {
        return "";
    }

    @Override
    public String toString() {
        return "Token{" +
                "tag='" + tag + '\'' +
                '}';
    }

    public String toTerminal(){
        if(tag.equals("keyword"))return getValue();
        else if (tag.equals("op"))return getValue();
        else return tag;
    }
}
