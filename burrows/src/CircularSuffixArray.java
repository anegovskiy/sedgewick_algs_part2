/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CircularSuffixArray {

    private final List<String> originalSuffixes;
    private final List<String> sortedSuffixes;
    private final int[] indexes;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("s can't be null");

        int length = s.length();
        originalSuffixes = new ArrayList<String>(length);
        StringBuilder rotatedStringBuilder = new StringBuilder(s);
        for (int i = 0; i < length; i++) {
            String rotatedString = rotatedStringBuilder.toString();
            originalSuffixes.add(i, rotatedString);

            rotatedStringBuilder.append(rotatedString.charAt(0));
            rotatedStringBuilder.deleteCharAt(0);
        }

        sortedSuffixes = new ArrayList<String>(originalSuffixes);
        Collections.sort(sortedSuffixes);

        indexes = new int[length];
        for (int i = 0; i < length; i++) {
            indexes[i] = originalSuffixes.indexOf(sortedSuffixes.get(i));
        }
    }

    // length of s
    public int length() {
        return originalSuffixes.size();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length()) throw new IllegalArgumentException("index out of bounds");
        return indexes[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray("ABRACADABRA!");
        System.out.println(circularSuffixArray.originalSuffixes);
        System.out.println(circularSuffixArray.sortedSuffixes);
        System.out.println(Arrays.toString(circularSuffixArray.indexes));
    }

}
