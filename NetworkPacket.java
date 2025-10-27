/* Daniel Rangosch
   Dr. Steinberg
   COP3503 Fall 2025
   Programming Assignment 3
*/

import java.util.*;

public class NetworkPacket {

    // Node class for Huffman tree
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
    public void analyzeFrequencies(ArrayList<String> symbols) {
        freqMap.clear();
        totalSymbols = symbols.size();
        for (String sym : symbols)
            freqMap.put(sym, freqMap.getOrDefault(sym, 0) + 1);
    }

    // Build Huffman tree
    public void buildHuffmanTree() {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<String, Integer> e : freqMap.entrySet())
            pq.add(new Node(e.getKey(), e.getValue()));
        if (pq.isEmpty()) return;
        while (pq.size() > 1) {
            Node a = pq.poll();
            Node b = pq.poll();
            Node parent = new Node(null, a.freq + b.freq);
            parent.left = a; parent.right = b;
            pq.add(parent);
        }
        root = pq.poll();
        codeMap.clear();
        buildCodeMap(root, "");
    }

    // Build binary codes
    private void buildCodeMap(Node node, String code) {
        if (node == null) return;
        if (node.isLeaf()) {
            codeMap.put(node.symbol, code.length() > 0 ? code : "0");
            return;
        }
        buildCodeMap(node.left, code + "0");
        buildCodeMap(node.right, code + "1");
    }

    // Encode symbols
    public String encode(ArrayList<String> symbols) {
        StringBuilder sb = new StringBuilder();
        for (String sym : symbols)
            sb.append(codeMap.get(sym));
        return sb.toString();
    }

    // Compute Huffman average bits
    public double getHuffmanAvg(ArrayList<String> symbols) {
        double total = 0.0;
        for (Map.Entry<String, Integer> e : freqMap.entrySet()) {
            double prob = (double) e.getValue() / totalSymbols;
            total += prob * codeMap.get(e.getKey()).length();
        }
        return total;
    }

    // Compute compression ratio
    public double getRatio(double huffmanAvg) {
        int fixedBits = (int) Math.ceil(Math.log(freqMap.size()) / Math.log(2));
        return fixedBits / huffmanAvg;
    }
}
