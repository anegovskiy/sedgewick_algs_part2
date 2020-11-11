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
        // initialize an ordered sequence of 256 characters, where extended ASCII character i appears ith in the sequence
        List<Character> charSequence = new ArrayList<>(256);
        for (int i = 0; i < 256; i++) {
            charSequence.add(i, (char) i);
        }

        // decode data
        while (!BinaryStdIn.isEmpty()) {
            // read each 8-bit character i (but treat it as an integer between 0 and 255) from standard input one at a time
            int newCharIndex = BinaryStdIn.readInt(8);
            char newChar = charSequence.get(newCharIndex);
            
            // write the ith character in the sequence;
            BinaryStdOut.write(newChar);

            // move that character to the front
            charSequence.remove(Character.valueOf(newChar));
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
