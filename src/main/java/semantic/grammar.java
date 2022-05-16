package semantic;
import Syntax.Lex.Token;
import Syntax.Grammar.SLRItem;
import semantic.rule.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class grammar {
	public static final String TAG=grammar.class.getSimpleName();
	
	public static class TreeNode extends Node{
		public List<TreeNode> childs;
		
		public TreeNode(SLRItem item,List<TreeNode> _childs){
			//����Node�Ĺ��캯�����г�ʼ��
			super(item,_childs);
			childs=new ArrayList<>();
			if(_childs!=null)
				childs.addAll(_childs);
		}
		
		public TreeNode(Token token){
			super(token);
			childs=new ArrayList<>();
		}
		
		public String toString(){
			String buff="";
			for(TreeNode child:childs){
				buff+=super.toString()+",";
			}
			return "TreeNode:"+super.toString()+"|childs:"+buff;
		}
	}
	
	TreeNode rootNode;
	Stack<TreeNode> genStack;
	List<TreeNode> workspace;
	
	public grammar(){
		genStack=new Stack<>();
		workspace=new ArrayList<>();
	}
	
	public void pushGenStack(TreeNode treeNode){
		genStack.push(treeNode);
	}
	
	public TreeNode popGenStack(){
		return genStack.pop();
	}
	
	public void popGenStack2Workspace(){
		workspace.add(0,popGenStack());
	}
	
	public void addParent4Workspace(SLRItem item){
		TreeNode newParent= new TreeNode(item,workspace);
		genStack.push(newParent);
		workspace.clear();
	}
	
	public void popGenStackToRoot(){
		rootNode=popGenStack();
	}

	public String getFullcode(){
		return rootNode.getValue("code");
	}
}
