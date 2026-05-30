import java.util.ArrayList;
import java.util.List;

abstract class Node {
    protected Data trainingSet;
    protected int beginIndex;
    protected int endIndex;
    protected double variance;

    /**
     * Costruisce un nodo dell'albero usando l'intervallo di esempi specificato.
     */
    Node(Data trainingSet, int beginIndex, int endIndex) {
        this.trainingSet = trainingSet;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.variance = computeVariance();
    }

    /**
     * Calcola la varianza del target sugli esempi del nodo.
     */
    private double computeVariance() {
        double sum = 0;
        for (int i = beginIndex; i <= endIndex; i++) {
            sum += trainingSet.getClassValue(i);
        }
        double mean = sum / (endIndex - beginIndex + 1);
        double sumSq = 0;
        for (int i = beginIndex; i <= endIndex; i++) {
            double diff = trainingSet.getClassValue(i) - mean;
            sumSq += diff * diff;
        }
        return sumSq;
    }

    /**
     * Stampa la struttura del sottoalbero a partire da questo nodo.
     */
    abstract void printTree(String indent);

    /**
     * Costruisce e stampa le regole associate al sottoalbero.
     */
    void printRules(String prefix) {
        List<String> rules = new ArrayList<>();
        collectRules(prefix, rules);
        for (String rule : rules) {
            System.out.println(rule);
        }
    }

    /**
     * Colleziona le regole di classificazione dal sottoalbero.
     */
    abstract void collectRules(String prefix, List<String> rules);

    /**
     * Restituisce il numero di figli del nodo (0 per i nodi foglia).
     */
    int getNumberOfChildren() {
        return 0;
    }

    /**
     * Restituisce la varianza calcolata per il nodo.
     */
    double getVariance() {
        return variance;
    }

    /**
     * Rappresentazione testuale dell'intervallo di esempi del nodo.
     */
    @Override
    public String toString() {
        return "[" + beginIndex + " - " + endIndex + "]";
    }
}
