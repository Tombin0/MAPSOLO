public class RegressionTree {
    private Node root;

    public RegressionTree(Data trainingSet) {
        Attribute[] availableAttributes = new Attribute[trainingSet.getNumberOfExplanatoryAttributes()];
        for (int i = 0; i < availableAttributes.length; i++) {
            availableAttributes[i] = trainingSet.getExplanatoryAttribute(i);
        }
        int numberOfExamplesPerLeaf = Math.max(1, (int) Math.ceil(trainingSet.getNumberOfExamples() * 0.1));
        root = learnTree(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, numberOfExamplesPerLeaf, availableAttributes);
    }

    private boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        return (end - begin + 1) <= numberOfExamplesPerLeaf;
    }

    private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end, Attribute[] availableAttributes) {
        SplitNode bestSplit = null;
        double bestVariance = Double.POSITIVE_INFINITY;

        for (Attribute attribute : availableAttributes) {
            SplitNode candidate;
            if (attribute instanceof DiscreteAttribute) {
                candidate = new DiscreteNode(trainingSet, begin, end, attribute);
            } else {
                candidate = new ContinuousNode(trainingSet, begin, end, attribute);
            }
            if (candidate.getNumberOfChildren() <= 1) {
                continue;
            }
            if (candidate.getVariance() < bestVariance) {
                bestVariance = candidate.getVariance();
                bestSplit = candidate;
            }
        }

        return bestSplit;
    }

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

    public void printRules() {
        System.out.println("********* RULES **********");
        if (root != null) {
            root.printRules("");
        }
        System.out.println("*************************");
    }

    public void printTree() {
        System.out.println("********* TREE **********");
        if (root != null) {
            root.printTree("");
        }
        System.out.println("*************************");
    }
}
