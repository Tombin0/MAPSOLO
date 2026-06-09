package tree;

import java.util.Scanner;
import java.util.TreeSet;
import java.io.*;
import data.Attribute;
import data.Data;
import data.DiscreteAttribute;

public class RegressionTree implements java.io.Serializable {
    private static final Scanner scanner = new Scanner(System.in);
    private static final long serialVersionUID = 1L;
    private Node root;

    /**
     * Costruisce l'albero di regressione a partire dal dataset di training.
     */
    public RegressionTree(Data trainingSet) {
        Attribute[] availableAttributes = new Attribute[trainingSet.getNumberOfExplanatoryAttributes()];
        for (int i = 0; i < availableAttributes.length; i++) {
            availableAttributes[i] = trainingSet.getExplanatoryAttribute(i);
        }
        int numberOfExamplesPerLeaf = Math.max(1, (int) Math.ceil(trainingSet.getNumberOfExamples() * 0.1));
        root = learnTree(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, numberOfExamplesPerLeaf, availableAttributes);
    }

    /**
     * Determina se il nodo deve diventare una foglia in base alla dimensione minima.
     */
    private boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        return (end - begin + 1) <= numberOfExamplesPerLeaf;
    }

    /**
     * Valuta tutti gli attributi disponibili e restituisce lo split migliore usando TreeSet.
     */
    private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end, Attribute[] availableAttributes) {
        TreeSet<SplitNode> splitCandidates = new TreeSet<>();

        for (Attribute attribute : availableAttributes) {
            SplitNode candidate;
            if (attribute instanceof DiscreteAttribute) {
                candidate = new DiscreteNode(trainingSet, begin, end, attribute);
            } else {
                candidate = new ContinuousNode(trainingSet, begin, end, attribute);
            }
            if (candidate.getNumberOfChildren() > 1) {
                splitCandidates.add(candidate);
            }
        }

        // TreeSet è ordinato per compareTo(), quindi il primo elemento ha la varianza minima
        return splitCandidates.isEmpty() ? null : splitCandidates.first();
    }

    /**
     * Costruisce l'albero ricorsivamente, creando foglie o nodi di split.
     */
    private Node learnTree(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf, Attribute[] availableAttributes) {
        if (begin > end) {
            return null;
        }
        LeafNode leaf = new LeafNode(trainingSet, begin, end);
        if (begin == end || availableAttributes.length == 0 || isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)) {
            return leaf;
        }

        SplitNode bestSplit = determineBestSplitNode(trainingSet, begin, end, availableAttributes);
        if (bestSplit == null) {
            return leaf;
        }

        Attribute[] remainingAttributes = removeAttribute(availableAttributes, bestSplit.getAttribute());
        Node[] children = new Node[bestSplit.getNumberOfChildren()];
        for (int i = 0; i < bestSplit.getNumberOfChildren(); i++) {
            SplitNode.SplitInfo info = bestSplit.getSplitInfo(i);
            children[i] = learnTree(trainingSet, info.getBeginindex(), info.getEndIndex(), numberOfExamplesPerLeaf, remainingAttributes);
        }
        bestSplit.setChildren(children);
        return bestSplit;
    }

    /**
     * Rimuove l'attributo già utilizzato dall'elenco di attributi disponibili.
     */
    private Attribute[] removeAttribute(Attribute[] attributes, Attribute attributeToRemove) {
        Attribute[] result = new Attribute[attributes.length - 1];
        int idx = 0;
        for (Attribute attribute : attributes) {
            if (!attribute.equals(attributeToRemove)) {
                result[idx++] = attribute;
            }
        }
        return result;
    }

    /**
     * Stampa tutte le regole estratte dall'albero.
     */
    public void printRules() {
        System.out.println("********* RULES **********");
        if (root != null) {
            root.printRules("");
        }
        System.out.println("*************************");
    }

    /**
     * Stampa la rappresentazione testuale dell'intero albero.
     */
    public void printTree() {
        System.out.println("********* TREE **********");
        if (root != null) {
            root.printTree("");
        }
        System.out.println("*************************");
    }

    /**
     * Avvia la predizione partendo dalla radice.
     */
    public Double predictClass() throws UnknownValueException {
        if (root == null) {
            return null;
        }
        return predictClass(root);
    }

    /**
     * Predice il valore seguendo i rami dell'albero in base alle risposte dell'utente.
     */
    private Double predictClass(Node current) throws UnknownValueException {
        if (current instanceof LeafNode) {
            return ((LeafNode) current).getMean();
        }

        SplitNode splitNode = (SplitNode) current;
        System.out.println(splitNode.formulateQuery());
        int risp = readIntFromConsole();
        if (risp < 0 || risp >= splitNode.getNumberOfChildren()) {
            throw new UnknownValueException("The answer should be an integer between 0 and " + (splitNode.getNumberOfChildren() - 1) + "!");
        }
        return predictClass(splitNode.children[risp]);
    }

    /**
     * Legge un intero dalla console per la fase di predizione.
     */
    private static int readIntFromConsole() {
        if (!scanner.hasNextLine()) {
            return -1;
        }
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Salva l'oggetto RegressionTree su file usando serializzazione Java.
     */
    public void salva(String fileName) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(this);
        }
    }

    /**
     * Carica una RegressionTree serializzata da file.
     */
    public static RegressionTree carica(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (RegressionTree) ois.readObject();
        }
    }
}
