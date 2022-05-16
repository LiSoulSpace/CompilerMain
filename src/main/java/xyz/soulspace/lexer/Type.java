package xyz.soulspace.lexer;

/**
 * @author :lisoulspace
 * @create :2022-05-10 21:28
 */
public class Type extends Token {

    public static final String INT = "int", FLOAT = "float";

    final String value;

    public Type(String v) {
        super(v, Tag.TYPE);
        value = v;
    }

    public String getValue() {
        return value;
    }

    public String toTerminal() {
        return getValue();
    }

    public static Token isType(String s) {
        switch (s) {
            case INT:
                return new Type(INT);
            case FLOAT:
                return new Type(FLOAT);
            default:
                return new Token(Tag.ERROR);
        }
    }
}
