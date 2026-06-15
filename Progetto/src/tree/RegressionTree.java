package tree;

import java.util.TreeSet;
import java.io.*;
import data.Attribute;
import data.ContinuousAttribute;
import data.Data;
import data.DiscreteAttribute;
import utility.Keyboard;

public class RegressionTree implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Node root;

    /* Costruisce l'albero di regressione a partire dal dataset di training. */
    public RegressionTree(Data trainingSet) {
        Attribute[] availableAttributes = new Attribute[trainingSet.getNumberOfExplanatoryAttributes()];
        for (int i = 0; i < availableAttributes.length; i++) {
            availableAttributes[i] = trainingSet.getExplanatoryAttribute(i);
        }
        int numberOfExamplesPerLeaf = Math.max(1, (int) Math.ceil(trainingSet.getNumberOfExamples() * 0.1));
        root = learnTree(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, numberOfExamplesPerLeaf, availableAttributes);
    }

    /* Determina se il nodo deve diventare una foglia in base alla dimensione minima. */
    private boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        return (end - begin + 1) <= numberOfExamplesPerLeaf;
    }

    /* Valuta tutti gli attributi disponibili e restituisce lo split migliore.
       Usa RTTI per distinguere attributi discreti e continui a runtime. */
    private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end, Attribute[] availableAttributes) {
        TreeSet<SplitNode> candidates = new TreeSet<>();

        for (Attribute attribute : availableAttributes) {
            SplitNode candidate;
            if (attribute instanceof DiscreteAttribute) {
                candidate = new DiscreteNode(trainingSet, begin, end, attribute);
            } else if (attribute instanceof ContinuousAttribute) {
                candidate = new ContinuousNode(trainingSet, begin, end, attribute);
            } else {
                throw new IllegalArgumentException("Unsupported attribute type: " + attribute.getClass().getName());
            }

            if (candidate.getNumberOfChildren() > 1) {
                candidates.add(candidate);
            }
        }

        return candidates.isEmpty() ? null : candidates.first();
    }

    /* Costruisce l'albero ricorsivamente, creando foglie o nodi di split. */
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

        Attribute[] nextAttributes = bestSplit.getAttribute() instanceof DiscreteAttribute
                ? removeAttribute(availableAttributes, bestSplit.getAttribute())
                : availableAttributes;

        Node[] children = new Node[bestSplit.getNumberOfChildren()];
        for (int i = 0; i < bestSplit.getNumberOfChildren(); i++) {
            SplitNode.SplitInfo info = bestSplit.getSplitInfo(i);
            children[i] = learnTree(trainingSet, info.getBeginindex(), info.getEndIndex(), numberOfExamplesPerLeaf, nextAttributes);
        }
        bestSplit.setChildren(children);
        return bestSplit;
    }

    /* Rimuove l'attributo già utilizzato dall'elenco di attributi disponibili. */
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

    /* Stampa tutte le regole estratte dall'albero. */
    public void printRules() {
        System.out.println("********* RULES **********");
        if (root != null) {
            root.printRules("");
        }
        System.out.println("*************************");
    }

    /* Stampa la rappresentazione testuale dell'intero albero. */
    public void printTree() {
        System.out.println("********* TREE **********");
        if (root != null) {
            root.printTree("");
        }
        System.out.println("*************************");
    }

    /* Avvia la predizione partendo dalla radice. */
    public Double predictClass() throws UnknownValueException {
        if (root == null) {
            return null;
        }
        return predictClass(root);
    }

    /* Predice il valore seguendo i rami dell'albero in base alle risposte dell'utente. */
    private Double predictClass(Node current) throws UnknownValueException {
        if (current instanceof LeafNode) {
            return ((LeafNode) current).getMean();
        }

        SplitNode splitNode = (SplitNode) current;
        System.out.println(splitNode.formulateQuery());
        int risp = Keyboard.readInt();
        if (risp == Integer.MIN_VALUE || risp < 0 || risp >= splitNode.getNumberOfChildren()) {
            throw new UnknownValueException("The answer should be an integer between 0 and " + (splitNode.getNumberOfChildren() - 1) + "!");
        }
        Node child = splitNode.getChild(risp);
        if (child == null) {
            throw new IllegalStateException("Split node child is missing for response " + risp + " on attribute " + splitNode.getAttribute().getName());
        }
        return predictClass(child);
    }

    /* Salva l'oggetto RegressionTree su file usando serializzazione Java. */
    public void salva(String fileName) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(this);
        }
    }

    /* Carica una RegressionTree serializzata da file. */
    public static RegressionTree carica(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (RegressionTree) ois.readObject();
        }
    }
}
