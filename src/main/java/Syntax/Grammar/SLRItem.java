package Syntax.Grammar;

import Syntax.Grammar.GrammarTable.Nonterminal;

//SLR(1)
public class SLRItem {
	public final Nonterminal left;
	public final String[] right;//字符串数组用来方便加‘.’
	public final int prefix_length;
	public SLRItem(Nonterminal l, String[] r, int pl) {
		left = l;
		right = r;
		prefix_length = pl;
	}
	
	public String toString() {
		StringBuilder tmp = new StringBuilder(new String(left.TAG));
		tmp.append("->");
		for (int i = 0; i < right.length; ++i) {
			if (i == prefix_length)
				tmp.append(".");//状态项目
			tmp.append(right[i]);
		}
		if (prefix_length == right.length) {
			tmp.append(".");//reduce规约项目
		}
		return tmp.toString();
	}
}