package Syntax.Grammar;

import Syntax.Grammar.GrammarTable.Nonterminal;

/**
 * 
 * 继承了SLR集合
 *
 */
public class LR1Item extends SLRItem{
	public final String real_end;
	
	public LR1Item(Nonterminal l, String[] r, int pl, String end) {
		super(l, r, pl);
		real_end=end;
	}
	
	public String toString() {
		StringBuilder tmp = new StringBuilder(new String(left.TAG));
		tmp.append("->");
		for (int i = 0; i < right.length; ++i) {
			if (i == prefix_length)
				tmp.append(".");
			tmp.append(right[i]);
		}
		if (prefix_length == right.length) {
			tmp.append(".");
		}
		if(real_end!=null&& !real_end.equals("")){
			tmp.append(",").append(real_end);
		}
		return tmp.toString();
	}
}
