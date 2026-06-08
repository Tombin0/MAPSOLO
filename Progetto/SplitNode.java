import java.util.List;
import java.util.ArrayList;

abstract class SplitNode extends Node implements Comparable<SplitNode> {
    // Classe che colelzione informazioni descrittive dello split
    static class SplitInfo implements java.io.Serializable {
        Object splitValue;
        int beginIndex;
        int endIndex;
        int numberChild;
        String comparator = "=";

        /**
         * Costruisce le informazioni di split senza comparatore esplicito.
         */
        SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild) {
            this.splitValue = splitValue;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.numberChild = numberChild;
        }

        /**
         * Costruisce le informazioni di split con comparatore specifico.
         */
        SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild, String comparator) {
            this.splitValue = splitValue;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.numberChild = numberChild;
            this.comparator = comparator;
        }

        /**
         * Restituisce l'indice iniziale dell'intervallo di esempi dello split.
         */
        int getBeginindex() {
            return beginIndex;
        }

        /**
         * Restituisce l'indice finale dell'intervallo di esempi dello split.
         */
        int getEndIndex() {
            return endIndex;
        }

        /**
         * Restituisce il valore di split associato al child.
         */
        Object getSplitValue() {
            return splitValue;
        }

        /**
         * Rappresentazione testuale dello split.
         */
        public String toString() {
            return "child " + numberChild + " split value" + comparator + splitValue + " [Examples:" + beginIndex + "-" + endIndex + "]";
        }

        /**
         * Restituisce l'operatore usato nello split.
         */
        String getComparator() {
            return comparator;
        }
    }

    Attribute attribute;
    List<SplitInfo> mapSplit;
    protected Node[] children;
    double splitVariance;

    /**
     * Imposta le informazioni di suddivisione per il nodo.
     */
    abstract void setSplitInfo(Data trainingSet, int beginExampelIndex, int endExampleIndex, Attribute attribute);

    /**
     * Valuta a quale figlio corrisponde un valore di input.
     */
    abstract int testCondition(Object value);

    /**
     * Costruisce un nodo di split e calcola la varianza complessiva dello split.
     */
    SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex);
        this.attribute = attribute;
        trainingSet.sort(attribute, beginExampleIndex, endExampleIndex);
        setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);
        splitVariance = 0;
        for (SplitInfo info : mapSplit) {
            double localVariance = new LeafNode(trainingSet, info.getBeginindex(), info.getEndIndex()).getVariance();
            splitVariance += localVariance;
        }
    }

    /**
     * Assegna ai figli i nodi calcolati dal processo di apprendimento.
     */
    void setChildren(Node[] children) {
        this.children = children;
    }

    /**
     * Restituisce l'attributo su cui è basato lo split.
     */
    Attribute getAttribute() {
        return attribute;
    }

    /**
     * Restituisce la varianza dello split invece della varianza del nodo.
     */
    @Override
    double getVariance() {
        return splitVariance;
    }

    /**
     * Restituisce il numero di rami generati dallo split.
     */
    @Override
    int getNumberOfChildren() {
        return mapSplit.size();
    }

    /**
     * Restituisce le informazioni di split per un figlio specifico.
     */
    SplitInfo getSplitInfo(int child) {
        return mapSplit.get(child);
    }

    /**
     * Crea una domanda testuale per la fase di predizione.
     */
    String formulateQuery() {
        String query = "";
        int index = 0;
        for (SplitInfo info : mapSplit) {
            query += (index + ":" + attribute + info.getComparator() + info.getSplitValue()) + "\n";
            index++;
        }
        return query;
    }

    /**
     * Stampa l'albero a partire da questo nodo di split.
     */
    @Override
    void printTree(String indent) {
        System.out.print(indent + this);
        System.out.println();
        if (children != null) {
            for (Node child : children) {
                child.printTree(indent + "  ");
            }
        }
    }

    /**
     * Costruisce le regole ricorsivamente per tutti i figli.
     */
    @Override
    void collectRules(String prefix, List<String> rules) {
        int index = 0;
        for (SplitInfo info : mapSplit) {
            String condition = attribute.getName() + info.getComparator() + info.getSplitValue();
            String newPrefix = prefix.isEmpty() ? condition : prefix + " AND " + condition;
            if (children != null && index < children.length && children[index] != null) {
                children[index].collectRules(newPrefix, rules);
            } else {
                double mean = new LeafNode(trainingSet, info.getBeginindex(), info.getEndIndex()).getMean();
                rules.add(newPrefix + " ==> Class=" + mean);
            }
            index++;
        }
    }

    /**
     * Rappresentazione testuale completa del nodo di split.
     */
    @Override
    public String toString() {
        String splitType = attribute instanceof DiscreteAttribute ? "DISCRETE SPLIT" : "CONTINUOUS SPLIT";
        String v = splitType + " : attribute=" + attribute + " Nodo: " + super.toString() + " variance:" + variance + " Split Variance: " + getVariance() + "\n";
        for (SplitInfo info : mapSplit) {
            v += "\t" + info + "\n";
        }
        return v;
    }

    /**
     * Implementa Comparable per confrontare SplitNode basato su splitVariance.
     * Restituisce un valore negativo se this ha varianza minore (meglio),
     * zero se uguali, positivo se this ha varianza maggiore (peggio).
     */
    @Override
    public int compareTo(SplitNode other) {
        return Double.compare(this.splitVariance, other.splitVariance);
    }
}
