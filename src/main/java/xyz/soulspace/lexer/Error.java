package xyz.soulspace.lexer;

public class Error extends Token {
    public final String value;

    public Error(String tag) {
        super(tag);
        value = "";
    }

    public Error(String tag, String value) {
        super(tag);
        this.value = value;
    }

    public String getValue() {
        return value + "";
    }

    public String toTerminal() {
        return getTag();
    }
}
