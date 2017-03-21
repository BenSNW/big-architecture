package hx.nlp.util;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhipeng.wang on 03/20 2017.
 */
public class CoreNLPUtils {

    public static boolean isEntityTree(Tree tree) {
        return true;
    }

    public static boolean isCompositeNode(Tree tree) {
        return tree.size() > tree.depth();
    }

    public static boolean isDateTimeTree(Tree tree) {
        return true;
    }

    public static int treeStartIndex(Tree tree) {
        while (!tree.isLeaf())
            tree = tree.firstChild();
        CoreLabel label = (CoreLabel) tree.label();
        return label.get(CoreAnnotations.IndexAnnotation.class);
    }

    public static int treeEndIndex(Tree tree) {
        while (!tree.isLeaf())
            tree = tree.lastChild();
        CoreLabel label = (CoreLabel) tree.label();
        return label.get(CoreAnnotations.IndexAnnotation.class);
    }

    public static int treeIndex(Tree tree) {
        if (!tree.isLeaf())
            throw new UnsupportedOperationException("only leaf node has index annotation");
        CoreLabel label = (CoreLabel) tree.label();
        return label.get(CoreAnnotations.IndexAnnotation.class);
    }

    public static int treeSafeIndex(Tree tree) {
        return isOneLeafTree(tree) ? treeStartIndex(tree) : -1;
    }

    public static boolean isOneLeafTree(Tree tree) {
        return tree.depth() + 1 == tree.size();
    }

    public static List<CoreLabel> treeSpaningTokens(Tree tree) {
        if (tree.isLeaf())
            return Arrays.asList((CoreLabel) tree.label());
        if (tree.isPreTerminal())
            return Arrays.asList((CoreLabel) tree.firstChild().label());
        List<CoreLabel> leaves = new ArrayList<>();
        for (Tree child: tree.children())
            leaves.addAll(treeSpaningTokens(child));
        return leaves;
    }

    public static List<CoreLabel> treeSpaningLabels(Tree tree) {
        List<CoreLabel> labels = new ArrayList<>();
        addTreeLabels(tree, labels);
        return labels;
    }

    private static void addTreeLabels(Tree tree, List<CoreLabel> labels) {
        if (tree.isLeaf()) {
            labels.add((CoreLabel) tree.label());
            return;
        }
        if (tree.isPreTerminal()) {
            labels.add((CoreLabel) tree.firstChild().label());
            return;
        }
        for (Tree child: tree.children())
            addTreeLabels(child, labels);
    }

    public static String treeSpaningString(Tree tree) {
        if (tree.isLeaf())
            return tree.nodeString();
        if (tree.isPreTerminal())
            return tree.firstChild().nodeString();
        StringBuilder sb = new StringBuilder();
        for (Tree child: tree.children())
            sb.append(treeSpaningString(child));
        return sb.toString();
    }
    
}
