abstract class Node {
    protected Data trainingSet;
    protected int beginIndex;
    protected int endIndex;

    Node(Data trainingSet, int beginIndex, int endIndex) {
        this.trainingSet = trainingSet;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    abstract void printTree(String indent);

    abstract void printRules(String prefix);

    abstract double getVariance();

    @Override
    public String toString() {
        return "[" + beginIndex + " - " + endIndex + "]";
    }
}
