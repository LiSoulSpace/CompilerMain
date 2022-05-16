package xyz.soulspace.lexer;
/**
 * @author :lisoulspace
 * @create :2022-05-10 21:28
 */
public class IntNumber extends Token {
    final int value;

    public IntNumber(int v) {
        super(v + "", Tag.NUM);
        value = v;
    }

    public String getValue() {
        return value + "";
    }

    public String toTerminal() {
        return getTag();
    }
}
