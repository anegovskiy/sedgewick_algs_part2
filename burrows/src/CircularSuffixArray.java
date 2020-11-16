/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import java.util.ArrayList;
import java.util.List;

public class CircularSuffixArray {

    private class CircularSuffix implements Comparable<CircularSuffix> {
        private final String inputString;
        private final int startIndex;

        CircularSuffix(String inputString, int startIndex) {
            this.inputString = inputString;
            this.startIndex = startIndex;
        }

        public char getChar(int index) {
            int nextIndex = (startIndex + index) % length;
            return inputString.charAt(nextIndex);
        }

        public int compareTo(CircularSuffix o) {
            // chars compare
            int count = 0;
            int res = 0;

            while (count < length && res == 0) {
                char ownCh = getChar(count);
                char theirsCh = o.getChar(count);
                res = Character.compare(ownCh, theirsCh);
                count++;
            }

            return res;
        }

        public String getValue() {
            return inputString.substring(0, startIndex) + inputString.substring(startIndex, length);
        }
    }

    // private final List<CircularSuffix> originalSuffixes;
    // private final List<CircularSuffix> sortedSuffixes;
    private final int[] indexes;
    private final int length;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("s can't be null");

        String inputString = new String(s);
        length = inputString.length();

        List<CircularSuffix> originalSuffixes = new ArrayList<CircularSuffix>(length);
        for (int i = 0; i < length; i++) {
            CircularSuffix circularSuffix = new CircularSuffix(inputString, i);
            originalSuffixes.add(i, circularSuffix);
        }

        List<CircularSuffix> sortedSuffixes = new ArrayList<CircularSuffix>(originalSuffixes);
        sortedSuffixes.sort(CircularSuffix::compareTo);

        indexes = new int[length];
        for (int i = 0; i < length; i++) {
            indexes[i] = originalSuffixes.indexOf(sortedSuffixes.get(i));
        }
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length()) throw new IllegalArgumentException("index out of bounds");
        return indexes[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        
    }
}

