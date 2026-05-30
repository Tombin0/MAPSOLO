import java.util.List;

abstract class SplitNode extends Node {
    // Classe che colelzione informazioni descrittive dello split
    class SplitInfo {
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
    SplitInfo mapSplit[];
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
        for (int i = 0; i < mapSplit.length; i++) {
            double localVariance = new LeafNode(trainingSet, mapSplit[i].getBeginindex(), mapSplit[i].getEndIndex()).getVariance();
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
        return mapSplit.length;
    }

    /**
     * Restituisce le informazioni di split per un figlio specifico.
     */
    SplitInfo getSplitInfo(int child) {
        return mapSplit[child];
    }

    /**
     * Crea una domanda testuale per la fase di predizione.
     */
    String formulateQuery() {
        String query = "";
        for (int i = 0; i < mapSplit.length; i++)
            query += (i + ":" + attribute + mapSplit[i].getComparator() + mapSplit[i].getSplitValue()) + "\n";
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
        for (int i = 0; i < mapSplit.length; i++) {
            String condition = attribute.getName() + mapSplit[i].getComparator() + mapSplit[i].getSplitValue();
            String newPrefix = prefix.isEmpty() ? condition : prefix + " AND " + condition;
            if (children != null && i < children.length && children[i] != null) {
                children[i].collectRules(newPrefix, rules);
            } else {
                double mean = new LeafNode(trainingSet, mapSplit[i].getBeginindex(), mapSplit[i].getEndIndex()).getMean();
                rules.add(newPrefix + " ==> Class=" + mean);
            }
        }
    }

    /**
     * Rappresentazione testuale completa del nodo di split.
     */
    @Override
    public String toString() {
        String splitType = attribute instanceof DiscreteAttribute ? "DISCRETE SPLIT" : "CONTINUOUS SPLIT";
        String v = splitType + " : attribute=" + attribute + " Nodo: " + super.toString() + " variance:" + variance + " Split Variance: " + getVariance() + "\n";
        for (int i = 0; i < mapSplit.length; i++) {
            v += "\t" + mapSplit[i] + "\n";
        }
        return v;
    }
}
