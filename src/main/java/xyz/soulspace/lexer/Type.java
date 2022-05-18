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
        return switch (s) {
            case INT -> new Type(INT);
            case FLOAT -> new Type(FLOAT);
            default -> new Token(Tag.ERROR);
        };
    }
}
