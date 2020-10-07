/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordNet;

    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    public String outcast(String[] nouns) {
        int dt = 0;
        String xt = "";

        for (String nounV : nouns) {
            if (!wordNet.isNoun(nounV)) continue;

            int dist = 0;

            for (String nounW : nouns) {
                if (!wordNet.isNoun(nounW)) continue;
                if (nounV.equals(nounW)) continue;

                dist += wordNet.distance(nounV, nounW);
            }

            if (dist > dt) {
                dt = dist;
                xt = nounV;
            }
        }

        return xt;
    }

    public static void main(String[] args) {
        String synsets
                = "/Users/andrey/Developer/sedgewick_algs_part2/wordnet/resources/synsets.txt";
        String hypernyms
                = "/Users/andrey/Developer/sedgewick_algs_part2/wordnet/resources/hypernyms.txt";
        WordNet wordnet = new WordNet(synsets, hypernyms);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 0; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
