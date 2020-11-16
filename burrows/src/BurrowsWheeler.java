/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.LSD;

import java.util.ArrayList;
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

        int length = encodedInput.length();
        int[] chars = new int[length];
        for (int i = 0; i < length; i++) {
            chars[i] = encodedInput.charAt(i);
        }

        LSD.sort(chars);
        List<Integer> next = new ArrayList<>();
        int[] nextFromForChar = new int[256];

        // recreate next array
        for (int i = 0; i < length; i++) {
            int indexInEncoded = -1;
            char sortedChar = (char) chars[i];
            int fromIndex = nextFromForChar[sortedChar];

            indexInEncoded = encodedInput.indexOf(sortedChar, fromIndex);
            next.add(i, indexInEncoded);
            nextFromForChar[sortedChar] = indexInEncoded + 1;
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
