package xyz.soulspace.semantic;

import xyz.soulspace.grammar.SLRItem;
import xyz.soulspace.lexer.Token;
import xyz.soulspace.semantic.Rule.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author :lisoulspace
 * @create :2022-05-16 13:15
 */
public class GrammarTree {
    public static class TreeNode extends Node {
        public List<TreeNode> children;
        private TreeNode father;

        public TreeNode(SLRItem item, List<TreeNode> children) {
            super(item, children);
            this.children = new ArrayList<>();
            if (children != null) {
                this.children.addAll(children);
            }
        }

        public void setFather(TreeNode father) {
            this.father = father;
        }

        public TreeNode getFather() {
            return father;
        }

        public TreeNode(Token token) {
            super(token);
            children = new ArrayList<>();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (TreeNode child : children) {
                sb.append(child).append(",");
            }
            return "TreeNode:" + super.toString() + "| children:" + sb;
        }
    }

    TreeNode rootNode;
    Stack<TreeNode> genStack;
    List<TreeNode> workspace;

    public GrammarTree() {
        genStack = new Stack<>();
        workspace = new ArrayList<>();
    }

    public void pushGenStack(TreeNode treeNode) {
        genStack.push(treeNode);
    }

    public TreeNode popGenStack() {
        return genStack.pop();
    }

    public void popGenStack2Workspace() {
        workspace.add(0, popGenStack());
    }

    public void addParent4Workspace(SLRItem item) {
        TreeNode parent = new TreeNode(item, workspace);
        genStack.push(parent);
        workspace.clear();
    }

    public void popGenStackToRoot() {
        rootNode = popGenStack();
    }

    public String genFullCode() {
        return rootNode.getValue("code");
    }
}
