package xyz.soulspace.lexer;

/**
 * @author :lisoulspace
 * @create :2022-05-10 21:28
 */
public class FloatNumber extends Token{
    final float value;

    public FloatNumber(float v) {
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
