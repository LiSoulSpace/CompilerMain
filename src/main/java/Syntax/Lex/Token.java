package Syntax.Lex;

import Syntax.Grammar.SymbolTable;

public class Token {
	public final String tag;
	
	public Token(String t){
		tag=t;
	}
	
	public Token(String id,String t){
		this(t);
		SymbolTable.setItem(id, "   ", tag);
	}
	public String toString(){return tag;}
	public String getTag(){return tag;}
	public String getValue(){return "";}
	public String toTerminal(){return tag;}
}