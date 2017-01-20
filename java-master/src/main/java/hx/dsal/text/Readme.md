Stanford CS166 http://web.stanford.edu/class/cs166/
CS97SI http://web.stanford.edu/class/cs97si/10-string-algorithms.pdf

A trie is a tree that stores a collection of strings over some alphabet Σ.
Tries are sometimes called prefix trees, since each node in a trie corresponds
to a prefix of one of the words in the trie.

The Aho-Corasick string matching algorithm is an algorithm for finding all
occurrences of a set of strings P1, ..., Pk inside a string T, which is
great for the cases where the patterns are fixed and the text to search changes.

A suffix trie of T is a trie (with suffix link) of all the suffixes of T.
=> P is a substring of a T if and only if P is a prefix of some suffix of T.
=> Given any pattern P, we can check in time O(|P|) whether P is a substring of T by seeing whether P is a prefix in T's suffix trie.
=> Given a large fixed string T to search and changing patterns P1, ..., Pk,  find all matches of those patterns in T.

Suffix tree

Suffix array