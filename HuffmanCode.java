import java.util.*;
import java.io.*;

// a class that represents a Huffman Tree:
// consists of nodes which have a character value, a frequency, and a left and right node.
// nodes with the lowest frequencies are leaves and the nodes with higher frequencies are parents.
public class HuffmanCode {
    // private fields
    private HuffmanNode root;

    // constructor which initializes a new HuffmanCode object from given array of frequencies
    // which stores the count of the character with ASCII value i at index i.
    public HuffmanCode(int[] frequencies) {
        // create nodes corresponding to frequencies and place into priority queue
        Queue<HuffmanNode> pq = new PriorityQueue<>();
        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] >= 1) {
                pq.add(new HuffmanNode((Integer) i, frequencies[i]));
            }
        }

        while (pq.size() > 1) {
            HuffmanNode first = pq.remove();
            HuffmanNode second = pq.remove();
            HuffmanNode merge = new HuffmanNode(first, second);
            pq.add(merge);
        }

        this.root = pq.peek();
    }

    
    // constructor that initializes a new Huffman Code object by reading info for a sequence
    // of Nodes from a file/given Scanner input. The format of the given file should be 
    // in pairs lines with the first line containing the ascii of the character and the 
    // second line containing the Huffman Encoding.
    public HuffmanCode(Scanner input) {
        // initialize the overall root
        while (input.hasNextLine()) {
            int ascii = Integer.parseInt(input.nextLine());
            String code = input.nextLine();
            this.root = createTree(ascii, code, this.root);
        }
    }

    // private helper method for constructor taking in Scanner as parameter.
    // returns a HuffmanNode as per the x = change(x) pattern to construct the tree.
    // Traverses the tree while the given binary digit is more than one digit.
    // If the given binary's first digit is a 0, we traverse to the left side of the 
    // given current node, and if it is a 1, we traverse to the right side. If there
    // is no node at the point we traverse to, create a new node with no associated ascii.
    // Once the binary value is has no more digits, we know a leaf node has been reached,
    // so return a new node with the ascii value.
    private HuffmanNode createTree(int ascii, String binary, HuffmanNode curr) {            
        if (binary.length() == 0) {
            return new HuffmanNode(ascii, 0);
        } else if (curr == null) { // create a branching node and recurse
            return createTree(ascii, binary, new HuffmanNode(null, 0));
        } else {
            if (binary.charAt(0) == '0') {
                curr.left = createTree(ascii, binary.substring(1), curr.left);
            } else {
                curr.right = createTree(ascii, binary.substring(1), curr.right);
            }
            return curr;
        }
    }


    // instance methods

    // stores the current state of the HuffmanCode Object in the given output stream
    // in the standard format:
    // pairs lines with the first line containing the ascii of the character and the 
    // second line containing the Huffman Encoding.
    public void save(PrintStream output) {
        this.save(output, this.root, "");
    }

    // private helper method for save.
    // stores the current state of the HuffmanCode Object in the given output stream
    // in the standard format:
    // pairs lines with the first line containing the ascii of the character and the 
    // second line containing the Huffman Encoding.
    // Takes in a string soFar which a 0 is appended to when traversing left, and
    // 1 is appended to when traversing right. Uses Node called curr to keep track
    // of position in tree.
    private void save(PrintStream output, HuffmanNode curr, String soFar) {
        if (curr != null) {
            if (curr.ascii != null) {
                output.println(curr.ascii.intValue());
                output.println(soFar);
            } else {
                this.save(output, curr.left, soFar + "0");
                this.save(output, curr.right, soFar + "1");
            }
        }
    }


    // translates the given encoding through given input to a message which is 
    // printed in the given output stream.
    public void translate(BitInputStream input, PrintStream output) {
        HuffmanNode curr = this.root;
        while (input.hasNextBit() && curr != null) {
            int bit = input.nextBit();
            if (bit == 0) { // go left 
                curr = curr.left;
            } else { // go right 
                curr = curr.right;
            }

            if (curr.ascii != null) { // leaf node is reached
                output.write((char) curr.ascii.intValue());
                // reset curr to root
                curr = this.root;
            }
        }
    }



    // private static class belonging to HuffmanCode which represents a node in the 
    // binary tree created by Huffman Code
    // implements the Comparable interface
    private static class HuffmanNode implements Comparable<HuffmanNode> {
        // fields
        public Integer ascii;
        public int frequency;
        public HuffmanNode left;
        public HuffmanNode right;

        // constructor which initializes fields to their respective given parameter
        public HuffmanNode(Integer ascii, int freq, HuffmanNode left, HuffmanNode right) {
            this.ascii = ascii;
            this.frequency = freq;
            this.left = left;
            this.right = right;
        }


        // constructor which initializes given ascii to object's ascii field
        // and given freq to object's freq field and left and right field to null
        public HuffmanNode(Integer ascii, int freq) {
            this(ascii, freq, null, null);
        }

        // constructor which initializes given left to object's left field
        // and given right to object's right field and character field to null
        // and the object's frequency field to the sum of the given left node's
        // frequency and the given right node's frequency
        public HuffmanNode(HuffmanNode left, HuffmanNode right) {  
            this(null, left.frequency + right.frequency, left, right);
        }

        // instance methods
        public int compareTo(HuffmanNode other) {
            return this.frequency - other.frequency;
        }
    }
}
