/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.ArrayList;
import java.util.List;

public class MoveToFront {

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {

        // Create char sequence
        List<Character> charSequence = new ArrayList<>(256);
        for (int i = 0; i < 256; i++) {
            charSequence.add(i, (char) i);
        }

        // Encode data
        while (!BinaryStdIn.isEmpty()) {
            char newChar = BinaryStdIn.readChar(8);
            byte charIndex = (byte) charSequence.indexOf(newChar);

            BinaryStdOut.write(charIndex);
            charSequence.remove(charIndex);
            charSequence.add(0, newChar);
        }

        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        // Create char sequence
        List<Character> charSequence = new ArrayList<>(256);
        for (int i = 0; i < 256; i++) {
            charSequence.add(i, (char) i);
        }

        // Decode data
        while (!BinaryStdIn.isEmpty()) {
            int newCharIndex = BinaryStdIn.readChar(8);
            char newChar = charSequence.get(newCharIndex);

            BinaryStdOut.write(newChar);
            charSequence.remove(newChar);
            charSequence.add(0, newChar);
        }

        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        String symbol = args[0];
        if (symbol.equals("-")) {
            MoveToFront.encode();
        }
        else if (symbol.equals("+")) {
            MoveToFront.decode();
        }
    }
}
