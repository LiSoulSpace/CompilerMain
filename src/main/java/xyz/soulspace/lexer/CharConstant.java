package xyz.soulspace.lexer;

/**
 * @author :lisoulspace
 * @create :2022-05-11 22:09
 */
public class CharConstant extends Token{
    final String value;

    public CharConstant(String chConstant) {
        super(chConstant, Tag.CHAR);
        value = chConstant;
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }

    @Override
    public String getTag() {
        return super.getTag();
    }
}
