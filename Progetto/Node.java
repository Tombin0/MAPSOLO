import java.util.ArrayList;
import java.util.List;

abstract class Node {
    protected Data trainingSet;
    protected int beginIndex;
    protected int endIndex;
    protected double variance;

    Node(Data trainingSet, int beginIndex, int endIndex) {
        this.trainingSet = trainingSet;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.variance = computeVariance();
    }

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

    abstract void printTree(String indent);

    void printRules(String prefix) {
        List<String> rules = new ArrayList<>();
        collectRules(prefix, rules);
        for (String rule : rules) {
            System.out.println(rule);
        }
    }

    abstract void collectRules(String prefix, List<String> rules);

    int getNumberOfChildren() {
        return 0;
    }

    double getVariance() {
        return variance;
    }

    @Override
    public String toString() {
        return "[" + beginIndex + " - " + endIndex + "]";
    }
}
