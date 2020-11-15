/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String inputString = BinaryStdIn.readString();
        int length = inputString.length();

        // Build circular suffixes
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(inputString);

        int first = -1;
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            int index = circularSuffixArray.index(i);
            if (index == 0) first = i;
            if (index < 1) index = length + index;
            char character = inputString.charAt(index - 1);
            chars[i] = character;
        }

        BinaryStdOut.write(first);
        BinaryStdOut.write(String.valueOf(chars));
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {

        // invert the message from t[] and first

        int first = BinaryStdIn.readInt();
        String encodedInput = BinaryStdIn.readString();

        char[] chars = encodedInput.toCharArray();
        Arrays.sort(chars);
        String sortedInput = String.valueOf(chars);
        int length = encodedInput.length();
        List<Integer> next = new ArrayList<>();

        // recreate next array
        for (int i = 0; i < length; i++) {
            boolean exists = true;
            int fromIndex = 0;
            int indexInEncoded = -1;
            char sortedChar = sortedInput.charAt(i);

            while (exists) {
                indexInEncoded = encodedInput.indexOf(sortedChar, fromIndex);
                exists = next.contains(indexInEncoded);
                fromIndex = indexInEncoded + 1;
            }

            next.add(i, indexInEncoded);
        }

        int count = 0;
        int nextIndex = next.get(first);
        while (count++ < length) {
            char character = encodedInput.charAt(nextIndex);
            BinaryStdOut.write(character);
            nextIndex = next.get(nextIndex);
        }

        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        String symbol = args[0];
        if (symbol.equals("-")) {
            BurrowsWheeler.transform();
        }
        else if (symbol.equals("+")) {
            BurrowsWheeler.inverseTransform();
        }
    }
}
