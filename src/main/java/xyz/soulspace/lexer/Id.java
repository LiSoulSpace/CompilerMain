package xyz.soulspace.lexer;
/**
 * @author :lisoulspace
 * @create :2022-05-10 21:28
 */
public class Id extends Token {
	final String name;

	public Id(String v) {
		super(v, Tag.ID);
		name=v;
	}
	
	public String getValue(){return name;}
	
	public String toTerminal(){return getTag();}
}
