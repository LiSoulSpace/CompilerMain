package xyz.soulspace.lexer;

/**
 * @author :lisoulspace
 * @create :2022-05-10 21:28
 */
public class Keyword extends Token {
    public static final String
            IF = "if", FLOAT = "float", ELSE = "else",
            WHILE = "while", DO = "do", INT = "int";

    final String value;

    public Keyword(String v) {
        super(v, Tag.KEYWORD);
        value = v;
    }

    public String getValue() {
        return value;
    }

    public String toTerminal() {
        return value;
    }

    public static Token isKeyword(String s) {
        return switch (s) {
            case IF -> new Keyword(IF);
            case ELSE -> new Keyword(ELSE);
            case DO -> new Keyword(DO);
            case WHILE -> new Keyword(WHILE);
            case INT -> new Keyword(INT);
            case FLOAT -> new Keyword(FLOAT);
            default -> new Token(Tag.ERROR);
        };
    }
}
