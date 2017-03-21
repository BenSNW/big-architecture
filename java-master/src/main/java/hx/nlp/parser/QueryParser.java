package hx.nlp.parser;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.parser.lexparser.ChineseTreebankParserParams;
import edu.stanford.nlp.parser.lexparser.TreebankLangParserParams;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphFactory;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.CoreMap;
import hx.nlp.util.CoreNLPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by zhipeng.wang on 03/20 2017.
 */
public class QueryParser {

    private static TreebankLangParserParams params = new ChineseTreebankParserParams();
    private static TreebankLanguagePack tlp = params.treebankLanguagePack();
    private static HeadFinder headFinder = params.headFinder(); // new ChineseHeadFinder();
    private static GrammaticalStructureFactory gsf = params.treebankLanguagePack()
            .grammaticalStructureFactory(tlp.punctuationWordRejectFilter(), headFinder);

    private final String query;
    private final Tree parserTree;
    private final SemanticGraph semanticGraph;
    private final List<CoreMap> entities;
    private final Map<Integer, IndexedWord> indexedWordMap;

    public QueryParser(String query) {
        this.query = query;
        parserTree = Tree.valueOf(query);
        GrammaticalStructure gs = gsf.newGrammaticalStructure(parserTree);
        semanticGraph = SemanticGraphFactory.generateEnhancedDependencies(gs);

        entities = new ArrayList<>();
        indexedWordMap = semanticGraph.vertexSet().stream().collect(
                Collectors.toMap(IndexedWord::index, Function.identity()));
    }

    public void parse() {
        IndexedWord root = semanticGraph.getFirstRoot();
        System.out.println(root.word() + " " + root.tag());

        treeHeadIndexedWord(parserTree.headTerminal(headFinder));
        System.out.println(CoreNLPUtils.treeIndex(parserTree.headTerminal(headFinder)));
        System.out.println(indexedWordMap.get(CoreNLPUtils.treeIndex(parserTree.headTerminal(headFinder))));

        parseTree(parserTree);
    }

    public void parseTree(Tree tree) {
        System.out.println(tree.toString());
        for (Tree child: tree.children()) {
            switch (child.nodeString()) {
                case "NP":
                    parseNPTree(child);
                    break;
                case "VP":
                    parseVPTree(child);
                    break;
                default:
                    parseTree(child);
                    System.out.println(child.toString());
            }
        }
    }

    public DateTimeParser isDateTimeTree(Tree tree) {

        return null;
    }

    protected void parseNPTree(Tree npTree) {
        System.out.println(npTree.toString() + " " + npTree.depth());
        System.out.println(npTree.headTerminal(headFinder));
        if (npTree.depth() < 3) {
            System.out.println(CoreNLPUtils.treeSpaningString(npTree));
            String text = CoreNLPUtils.treeSpaningString(npTree);
            List<String> targetList = new ArrayList<>(2);
            StringBuilder buffer = new StringBuilder(4);
            for (Tree child: npTree.children()) {
                if (child.nodeString().startsWith("N")) {
                    buffer.append(child.firstChild().value());
                } else {
                    targetList.add(buffer.toString());
                    buffer.setLength(0);
                }
            }
            targetList.add(buffer.toString());
            System.out.println(targetList);
        } else {
            parseTree(npTree);
        }
    }

    protected void parseDPTree(Tree npTree) {
        System.out.println(npTree.toString() + " " + npTree.depth());
        if (npTree.depth() < 3) {
            System.out.println(CoreNLPUtils.treeSpaningString(npTree));
            String text = CoreNLPUtils.treeSpaningString(npTree);
            List<String> targetList = new ArrayList<>(2);
            StringBuilder buffer = new StringBuilder(4);
            for (Tree child: npTree.children()) {
                if (child.nodeString().startsWith("N")) {
                    buffer.append(child.firstChild().value());
                } else {    // CC OR PU
                    targetList.add(buffer.toString());
                    buffer.setLength(0);
                }
            }
            targetList.add(buffer.toString());
            System.out.println(targetList);
        } else {
            parseTree(npTree);
        }
    }

    protected void parseQPTree(Tree npTree) {
        System.out.println(npTree.toString() + " " + npTree.depth());
        if (npTree.depth() < 3) {
            System.out.println(CoreNLPUtils.treeSpaningString(npTree));
            String text = CoreNLPUtils.treeSpaningString(npTree);
            List<String> targetList = new ArrayList<>(2);
            StringBuilder buffer = new StringBuilder(4);
            for (Tree child: npTree.children()) {
                if (child.nodeString().startsWith("N")) {
                    buffer.append(child.firstChild().value());
                } else {
                    targetList.add(buffer.toString());
                    buffer.setLength(0);
                }
            }
            targetList.add(buffer.toString());
            System.out.println(targetList);
        } else {
            parseTree(npTree);
        }
    }

    protected void parseVPTree(Tree vpTree) {
        System.out.println(vpTree.depth());
    }

    private IndexedWord treeHeadIndexedWord(Tree treeHead) {
        CoreLabel label = (CoreLabel) treeHead.label();
        return indexedWordMap.get(label.index());
    }

    public static void main(String[] args) {
        new QueryParser("(IP (NP (DNP (NP (NN 工商) (NN 银行)) (DEG 的)) (NP (NN 股价) (CC 和) (NN 市盈率))) (VP (ADVP (AD 分别)) (VP (VC 是) (QP (CD 多少)))))").parse();

        new QueryParser("(NP (NP (NN 工商) (NN 银行)) (PP (P 到) (LCP (NP (NT 3月) (NT 5日)) (LC 为止))) (DNP (NP (DP (DT 前) (QP (CD 一) (CLP (M 个)))) (NP (NN 月))) (DEG 的)) (NP (NN 股价) (NN 走势)))").parse();
    }
}
