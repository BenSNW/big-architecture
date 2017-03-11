package hx.dsal.text;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Created by BenSNW on Oct 12, 2016
 *
 * @see https://github.com/mission-peace/interview/blob/master/src/com/interview/suffixprefix/Trie.java
 */
public class HashMapTrie {

	private final TrieNode root;
	private int maxTransitions;
	// http://stackoverflow.com/questions/6720396/different-types-of-thread-safe-sets-in-java
	private final ConcurrentSkipListSet<String> keys;
	
	public HashMapTrie() {
		this(8);
	}
	
	public HashMapTrie(int maxTransitions) {
		root = new TrieNode(maxTransitions);
		this.maxTransitions = maxTransitions;
		keys = new ConcurrentSkipListSet<>();
	}

	/**
	 * number of all words
     */
	public int size() {
		return keys.size();
	}
	
	/**
	 * number of all non-empty nodes, namely character nodes
	 */
	public int nodeCount() {
		return root.nodeSize();
	}

	// String -> Stream<Character>
	// http://stackoverflow.com/questions/26320910/how-to-convert-a-string-to-a-java-8-stream-of-characters
	public long characterCount() {
		return keys.stream().flatMap(word -> word.chars().mapToObj(code -> (char) code))
				.distinct().count();
	}

	public String matchFirst(String text) {
		if (StringUtils.isBlank(text))
			return "";
		StringBuilder sb = new StringBuilder();
		for (int index = 0; index < text.length(); index++) {
			TrieNode node = root;
			for (int i = index; i < text.length(); i++) {
				if (node.transitions.containsKey(text.charAt(i))) {
					sb.append(text.charAt(i));
					node = node.transitions.get(text.charAt(i));
					if (node.endOfWord)
						return sb.toString();
				} else {
					sb.setLength(0);
					break;
				}
			}
		}
		// the whole text has been traversed over
		return "";
	}
	
	public Set<String> matchAll(String text) {
		Set<String> match = new HashSet<>();
		if (StringUtils.isBlank(text))
			return match;
		StringBuilder sb = new StringBuilder();
		for (int index = 0; index < text.length(); index++) {
			TrieNode node = root;
			for (int i = index; i < text.length(); i++) {
				if (node.transitions.containsKey(text.charAt(i))) {
					sb.append(text.charAt(i));
					node = node.transitions.get(text.charAt(i));
					if (node.endOfWord)
						match.add(sb.toString());
				} else {
					sb.setLength(0);
					break;
				}
			}
		}
		return match;
	}

	public boolean insert(String word) {
		if (!keys.add(word))
			return false;
		TrieNode node = root;
		for (int i = 0; i < word.length(); i++) {
			// theoretically the transitions should not be null
			// allocate transition size dynamically according to node depth
			int size = Math.max(2, maxTransitions - i); // maxTransitions / ( i/2 + 1)
			node.transitions.putIfAbsent(word.charAt(i), new TrieNode(size));
			node = node.transitions.get(word.charAt(i));
		}
		node.endOfWord = true;
		return true;
	}

	public boolean delete(String word) {
		if (!keys.remove(word))
			return false;
		// first detect if this word is prefix of another word
		List<TrieNode> nodes = new ArrayList<>(word.length() + 1);
		TrieNode node = root;
		nodes.add(node);
		for (char c : word.toCharArray()) {
			node = node.transitions.get(c);
			nodes.add(node);
		}
		// the last node is not empty -> this word is a prefix
		if (nodes.get(word.length()).transitions.size() > 0) {
			nodes.get(word.length()).endOfWord = false;
			return true;
		}
			
		for (int i = word.length() - 1; i >= 0; i--) {
			nodes.get(i).transitions.remove(word.charAt(i));
			// the previous node still has another transition
			if ( !nodes.get(i).transitions.isEmpty())				
				return true;
		}
		
		return true;
	}

	public Stream<String> keyStream() {
//		keys.toArray(new String[keys.size()]);
		return Collections.unmodifiableSet(keys).stream();
	}

	@Override
	public String toString() {
		return Arrays.toString(keyStream().toArray());
	}
	
	static class TrieNode {
		boolean endOfWord;	// is previous node endOfWord, can also be achieved by retrieving the pattern set
		ConcurrentMap<Character, TrieNode> transitions;	// transitions of previous node
		
		TrieNode(int size) {
			// allocate size dynamically according to node depth
			transitions = new ConcurrentHashMap<>(size);
		}
		
		int nodeSize() {
			if (transitions == null || transitions.isEmpty())
				return 0;
			return transitions.size() + transitions.values().stream().collect(
					Collectors.summingInt(node->node.nodeSize()));
		}

	}
	
	public static void main(String[] args) {
		HashMapTrie trie = new HashMapTrie();
		Stream.of("word", "words", "trie", "weekend").forEach(trie::insert);		
		System.out.println(trie.matchFirst("keywords"));
		System.out.println(trie.matchAll("keywords trie is not tree"));
		System.out.println(trie.root.nodeSize());
		
		trie.delete("word"); trie.delete("trie");
		System.out.println(trie.matchFirst("keyword"));
		System.out.println(trie.matchFirst("keywords"));
		System.out.println(trie.matchAll("keywords trie is not tree"));
		System.out.println(trie.root.nodeSize());
		
		Stream.of("weak", "trie", "try").forEach(trie::insert);
		System.out.println(trie.keys);
		System.out.println(trie.matchAll("weak after weekend"));
		System.out.println(trie.root.nodeSize());
		
		trie.delete("weak"); trie.delete("words");
		System.out.println(trie.matchAll("weak after weekend"));
		System.out.println(trie.root.nodeSize());
		
		System.out.println(trie.matchAll("no try, no die"));
		System.out.println(trie.root.nodeSize());

		Stream.of("中国平安", "平安", "中国平安发展银行", "催收").forEach(trie::insert);
		System.out.println(trie.matchAll("中国平安发展银行催收"));
		System.out.println(trie);
		System.out.println(trie.nodeCount() + " " + trie.characterCount());
	}

}
