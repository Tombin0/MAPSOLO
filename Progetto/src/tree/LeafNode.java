package tree;

import java.util.List;
import data.Data;

class LeafNode extends Node {
    private final double mean;

    /**
     * Costruisce un nodo foglia e calcola il valore medio del target.
     */
    LeafNode(Data trainingSet, int beginIndex, int endIndex) {
        super(trainingSet, beginIndex, endIndex);
        double sum = 0.0;
        int count = endIndex - beginIndex + 1;
        for (int i = beginIndex; i <= endIndex; i++) {
            sum += trainingSet.getClassValue(i);
        }
        mean = sum / count;
    }

    /**
     * Restituisce la media del target per i casi nel nodo foglia.
     */
    double getMean() {
        return mean;
    }

    /**
     * Stampa la rappresentazione del nodo foglia.
     */
    @Override
    void printTree(String indent) {
        System.out.printf("%sLEAF : class=%.6f Nodo: %s variance:%.6f%n", indent, mean, super.toString(), variance);
    }

    /**
     * Aggiunge la regola corrispondente a questa foglia.
     */
    @Override
    void collectRules(String prefix, List<String> rules) {
        String rule = prefix.trim();
        if (rule.isEmpty()) {
            rules.add("TRUE ==> Class=" + mean);
        } else {
            rules.add(rule + " ==> Class=" + mean);
        }
    }
}
