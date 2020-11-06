/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.TST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoggleSolver {

    private final TST<Integer> words;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        words = new TST<Integer>();
        for (String word : dictionary)
            if (word.length() > 2) words.put(word, 0);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) throw new IllegalArgumentException("Board can't be null");
        List<String> allValidWords = getAllValidWordsFor(board);
        Collections.sort(allValidWords);
        return allValidWords;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null) throw new IllegalArgumentException("Word can't be null");
        return getScoreOf(word);
    }

    public static void main(String[] args) {
        Stopwatch stopwatch = new Stopwatch();
        String basePath = "/Users/andrey/Developer/sedgewick_algs_part2/boggle/resources/";
        In in = new In(basePath + args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(basePath + args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
        StdOut.println("Time = " + stopwatch.elapsedTime());
    }

    private int getScoreOf(String word) {
        int length = word.length();
        if (length < 3) return 0;
        if (!words.contains(word)) return 0;

        if (length < 5) return 1;
        else if (length == 5) return 2;
        else if (length == 6) return 3;
        else if (length == 7) return 5;
        else return 11;
    }

    private List<String> getAllValidWordsFor(BoggleBoard board) {
        // Thoughts
        // Iterate over all letters, checking each letter and next neighbours in Trie
        // Like using dfs search probably

        List<String> validWords = new ArrayList<>();
        int rows = board.rows();
        int cols = board.cols();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Set<Integer> visited = new HashSet<>();
                dfsValidWords(i, j, visited, "", board, validWords);
            }
        }

        return validWords;
    }

    private void dfsValidWords(int row, int col, Set<Integer> visited, String prefix,
                               BoggleBoard board, List<String> validWords) {

        // Check if this row col not visited
        int index = getIndexFrom(row, col, board);
        if (visited.contains(index)) return;
        visited.add(index);

        // Check if prefix concatenated with current char is allowed by Trie
        String newLetter = String.valueOf(board.getLetter(row, col));
        if (newLetter.equals("Q")) {
            newLetter = newLetter.concat("U");
        }
        String concatPrefix = prefix.concat(newLetter);
        if (!words.keysWithPrefix(concatPrefix).iterator().hasNext()) return;

        // Iff allowed -> check if this forms a valid word
        // Iff forms -> add it to list of words
        if (words.contains(concatPrefix) && !validWords.contains(concatPrefix))
            validWords.add(concatPrefix);

        // Run dfs for all neighbours
        // Left
        if (col >= 1)
            dfsValidWords(row, col - 1, createCopyOf(visited), concatPrefix, board, validWords);

        // Top
        if (row > 0)
            dfsValidWords(row - 1, col, createCopyOf(visited), concatPrefix, board, validWords);

        // Top Left
        if (col >= 1 && row > 0)
            dfsValidWords(row - 1, col - 1, createCopyOf(visited), concatPrefix, board, validWords);

        // Right
        if (col < board.cols() - 1)
            dfsValidWords(row, col + 1, createCopyOf(visited), concatPrefix, board, validWords);

        // Top Right
        if (col < board.cols() - 1 && row > 0)
            dfsValidWords(row - 1, col + 1, createCopyOf(visited), concatPrefix, board, validWords);

        // Bottom
        if (row < board.rows() - 1)
            dfsValidWords(row + 1, col, createCopyOf(visited), concatPrefix, board, validWords);

        // Bottom Left
        if (col >= 1 && row < board.rows() - 1)
            dfsValidWords(row + 1, col - 1, createCopyOf(visited), concatPrefix, board, validWords);

        // Bottom Right
        if (col < board.cols() - 1 && row < board.rows() - 1)
            dfsValidWords(row + 1, col + 1, createCopyOf(visited), concatPrefix, board, validWords);
    }

    private Set<Integer> createCopyOf(Set<Integer> source) {
        return new HashSet<>(source);
    }

    private int getIndexFrom(int row, int col, BoggleBoard board) {
        return row * board.cols() + col;
    }
}
