import java.util.ArrayList;
import java.util.List;

abstract class SplitNode extends Node {
    // Classe che colelzione informazioni descrittive dello split
    class SplitInfo {
        Object splitValue;
        int beginIndex;
        int endIndex;
        int numberChild;
        String comparator = "=";

        SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild) {
            this.splitValue = splitValue;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.numberChild = numberChild;
        }

        SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild, String comparator) {
            this.splitValue = splitValue;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.numberChild = numberChild;
            this.comparator = comparator;
        }

        int getBeginindex() {
            return beginIndex;
        }

        int getEndIndex() {
            return endIndex;
        }

        Object getSplitValue() {
            return splitValue;
        }

        public String toString() {
            return "child " + numberChild + " split value" + comparator + splitValue + " [Examples:" + beginIndex + "-" + endIndex + "]";
        }

        String getComparator() {
            return comparator;
        }
    }

    Attribute attribute;
    SplitInfo mapSplit[];
    protected Node[] children;
    double splitVariance;

    abstract void setSplitInfo(Data trainingSet, int beginExampelIndex, int endExampleIndex, Attribute attribute);

    abstract int testCondition(Object value);

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

    void setChildren(Node[] children) {
        this.children = children;
    }

    Attribute getAttribute() {
        return attribute;
    }

    @Override
    double getVariance() {
        return splitVariance;
    }

    @Override
    int getNumberOfChildren() {
        return mapSplit.length;
    }

    SplitInfo getSplitInfo(int child) {
        return mapSplit[child];
    }

    String formulateQuery() {
        String query = "";
        for (int i = 0; i < mapSplit.length; i++)
            query += (i + ":" + attribute + mapSplit[i].getComparator() + mapSplit[i].getSplitValue()) + "\n";
        return query;
    }

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
