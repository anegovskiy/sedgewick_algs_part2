/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class WordNet {
    private static final String COMMA_DELIMITER = ",";
    private static final String SPACE_DELIMITER = " ";

    private final TreeMap<String, List<Integer>> nounsBySynset;
    private final SAP sap;
    private final TreeMap<Integer, String> synsetsById;


    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        verifyParams(synsets, hypernyms);

        this.synsetsById = parseSynsets(synsets);
        this.nounsBySynset = parseNouns(this.synsetsById);

        Digraph digraph = createDigraph(this.synsetsById.size(), hypernyms);
        DirectedCycle cycle = new DirectedCycle(digraph);
        if (cycle.hasCycle()) throw new IllegalArgumentException("Isn't a DAG");
        this.sap = new SAP(digraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        Set<String> nouns = new HashSet<>(nounsBySynset.keySet());
        return nouns;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        verifyParam(word);
        return nounsBySynset.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        verifyParams(nounA, nounB);

        List<Integer> v = nounsBySynset.get(nounA);
        List<Integer> w = nounsBySynset.get(nounB);
        return sap.length(v, w);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        verifyParams(nounA, nounB);

        List<Integer> v = nounsBySynset.get(nounA);
        List<Integer> w = nounsBySynset.get(nounB);
        int ancestor = sap.ancestor(v, w);

        return synsetsById.get(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        String synsets
                = "/Users/andrey/Developer/sedgewick_algs_part2/wordnet/resources/synsets.txt";
        String hypernyms
                = "/Users/andrey/Developer/sedgewick_algs_part2/wordnet/resources/hypernyms.txt";
        WordNet wordNet = new WordNet(synsets, hypernyms);

        String wormBirdSap = wordNet.sap("worm", "bird");
        System.out.println(wormBirdSap);
        assert wormBirdSap.equals("animal animate_being beast brute creature fauna");

        int result = wordNet.distance("Brown_Swiss", "barrel_roll");
        System.out.println(result);
        assert result == 29;

        result = wordNet.distance("white_marlin", "mileage");
        System.out.println(result);
        assert result == 23;

        result = wordNet.distance("Black_Plague", "black_marlin");
        System.out.println(result);
        assert result == 33;

        result = wordNet.distance("American_water_spaniel", "histology");
        System.out.println(result);
        assert result == 27;
    }

    // Verification
    private void verifyParams(Object paramA, Object paramB) {
        verifyParam(paramA);
        verifyParam(paramB);
    }

    private void verifyParam(Object param) {
        if (param == null) throw new IllegalArgumentException("Param can't be null");
    }

    // Parsing

    private TreeMap<String, List<Integer>> parseNouns(TreeMap<Integer, String> synsets) {
        TreeMap<String, List<Integer>> nouns = new TreeMap<>();

        for (Map.Entry<Integer, String> entry : synsets.entrySet()) {
            String[] entryNouns = entry.getValue().split(SPACE_DELIMITER);
            int nounIndex = entry.getKey();
            for (String entryNoun : entryNouns) {
                if (nouns.containsKey(entryNoun)) {
                    nouns.get(entryNoun).add(nounIndex);
                }
                else {
                    List<Integer> indexes = new ArrayList<>();
                    indexes.add(nounIndex);
                    nouns.put(entryNoun, indexes);
                }
            }
        }

        return nouns;
    }


    private Digraph createDigraph(int size, String hypernyms) {
        Digraph graph = new Digraph(size);
        In in = new In(hypernyms);
        while (!in.isEmpty()) {
            String line = in.readLine();
            String[] values = line.split(COMMA_DELIMITER);

            if (values.length < 2) continue;
            int indexOfHypernym = Integer.parseInt(values[0]);
            for (int i = 1; i < values.length; i++) {
                graph.addEdge(indexOfHypernym, Integer.parseInt(values[i]));
            }
        }

        return graph;
    }

    private TreeMap<Integer, String> parseSynsets(String synsets) {
        TreeMap<Integer, String> treeMap = new TreeMap<>();

        In in = new In(synsets);
        while (!in.isEmpty()) {
            String line = in.readLine();
            String[] values = line.split(COMMA_DELIMITER);

            int indexOfSynset = Integer.parseInt(values[0]);
            String synset = values[1];
            treeMap.put(indexOfSynset, synset);
        }

        return treeMap;
    }
}
