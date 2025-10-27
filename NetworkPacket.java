/* Daniel Rangosch
   Dr. Steinberg
   COP3503 Fall 2025
   Programming Assignment 3
*/

import java.util.*;
import java.io.*;

public class NetworkPacket {

    // Node for Huffman tree
    private class Node implements Comparable<Node> {
        String symbol;
        int freq;
        Node left, right;
        Node(String s, int f) { symbol = s; freq = f; }
        public int compareTo(Node n) { return this.freq - n.freq; }
        boolean isLeaf() { return left == null && right == null; }
    }

    private Map<String, Integer> freqMap = new HashMap<>();
    private Map<String, String> codeMap = new HashMap<>();
    private Node root;
    private int totalSymbols = 0;

    // Count symbol frequencies
    public void analyzeFrequencies(String filename) throws IOException {
        freqMap.clear();
        codeMap.clear();
        totalSymbols = 0;

        File file = new File(filename);
        if (!file.exists()) throw new FileNotFoundException("File not found: " + filename);

        Scanner sc = new Scanner(file);
        while (sc.hasNext()) {
            String sym = sc.next();
            freqMap.put(sym, freqMap.getOrDefault(sym, 0) + 1);
            totalSymbols++;
        }
        sc.close();
    }

    // Build Huffman tree
    public void buildHuffmanTree() {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<String, Integer> e : freqMap.entrySet())
            pq.add(new Node(e.getKey(), e.getValue()));

        if (pq.isEmpty()) return;

        if (pq.size() == 1) {
            Node single = pq.poll();
            Node parent = new Node(null, single.freq);
            parent.left = single;
            root = parent;
            buildCodeMap(root, "");
            return;
        }

        while (pq.size() > 1) {
            Node a = pq.poll();
            Node b = pq.poll();
            Node parent = new Node(null, a.freq + b.freq);
            parent.left = a;
            parent.right = b;
            pq.add(parent);
        }

        root = pq.poll();
        buildCodeMap(root, "");
    }

    // Recursively assign binary codes
    private void buildCodeMap(Node node, String code) {
        if (node == null) return;
        if (node.isLeaf()) {
            codeMap.put(node.symbol, code.length() > 0 ? code : "0");
            return;
        }
        buildCodeMap(node.left, code + "0");
        buildCodeMap(node.right, code + "1");
    }

    // Encode text file
    public String encode(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        File file = new File(filename);
        if (!file.exists()) throw new FileNotFoundException("File not found: " + filename);

        Scanner sc = new Scanner(file);
        while (sc.hasNext()) {
            String sym = sc.next();
            sb.append(codeMap.get(sym));
        }
        sc.close();
        return sb.toString();
    }

    // Compute Huffman average
    public double getHuffmanAvg() {
        if (totalSymbols == 0) return 0.0;
        double total = 0.0;
        for (Map.Entry<String, Integer> e : freqMap.entrySet()) {
            double prob = (double) e.getValue() / totalSymbols;
            total += prob * codeMap.get(e.getKey()).length();
        }
        return total;
    }

    // Compute compression ratio
    public double getRatio() {
        if (freqMap.size() <= 1) return 1.0;
        int fixedBits = (int) Math.ceil(Math.log(freqMap.size()) / Math.log(2));
        double huffAvg = getHuffmanAvg();
        return fixedBits / huffAvg;
    }
}
