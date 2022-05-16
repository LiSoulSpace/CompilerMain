package xyz.soulspace.lexer;

/**
 * @author :lisoulspace
 * @create :2022-05-10 21:28
 */
public class Operator extends Token {
    public final static String
            PLUS = "+", MINUS = "-", MULT = "*", DIV = "/",
            LESS = "<", MORE = ">", ASSIGN = "=", LPAR = "(",
            RPAR = ")", LNND = ";", SIQUO = "'", EQU = "==",
            NLES = ">=", NMRE = "<=", NEQU = "!=", LBPAR = "{",
            RBPAR = "}";

    final String value;

    public Operator(String v) {
        super(v, Tag.OP);
        value = v;
    }

    public String getValue() {
        return value;
    }

    public String toTerminal() {
        return getValue();
    }

    public static Token isOperator(String s) {
        switch (s) {
            case EQU, NLES, NMRE, NEQU -> {
                return new Operator(s);
            }
        }

        String one = s.substring(0, 1);
        return switch (one) {
            case PLUS, MINUS, MULT, DIV, LESS, MORE, ASSIGN, LPAR, RPAR, LNND, SIQUO, LBPAR, RBPAR -> new Operator(one);
            default -> new Token(Tag.ERROR);
        };
    }

    public int width() {
        return switch (value) {
            case EQU, NLES, NMRE, NEQU -> 2;
            default -> 1;
        };
    }

    public boolean isDelimiter() {
        return switch (value) {
            case LPAR, RPAR, LBPAR, RBPAR, LNND -> true;
            default -> false;
        };
    }
}
