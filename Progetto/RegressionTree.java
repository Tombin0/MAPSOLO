public class RegressionTree {
    private Node root;

    public RegressionTree(Data trainingSet) {
        Attribute[] availableAttributes = new Attribute[trainingSet.getNumberOfExplanatoryAttributes()];
        for (int i = 0; i < availableAttributes.length; i++) {
            availableAttributes[i] = trainingSet.getExplanatoryAttribute(i);
        }
        root = buildTree(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, availableAttributes);
    }

    private Node buildTree(Data trainingSet, int beginIndex, int endIndex, Attribute[] availableAttributes) {
        LeafNode leaf = new LeafNode(trainingSet, beginIndex, endIndex);
        if (beginIndex >= endIndex || availableAttributes.length == 0) {
            return leaf;
        }

        Attribute bestAttribute = null;
        SplitNode bestSplit = null;
        double bestVariance = leaf.getVariance();

        for (Attribute attribute : availableAttributes) {
            DiscreteNode candidate = new DiscreteNode(trainingSet, beginIndex, endIndex, attribute);
            if (candidate.getNumberOfChildren() <= 1) {
                continue;
            }
            if (candidate.getVariance() < bestVariance) {
                bestVariance = candidate.getVariance();
                bestSplit = candidate;
                bestAttribute = attribute;
            }
        }

        if (bestSplit == null) {
            return leaf;
        }

        Attribute[] remainingAttributes = removeAttribute(availableAttributes, bestAttribute);
        Node[] children = new Node[bestSplit.getNumberOfChildren()];
        for (int i = 0; i < bestSplit.getNumberOfChildren(); i++) {
            SplitNode.SplitInfo info = bestSplit.getSplitInfo(i);
            children[i] = buildTree(trainingSet, info.getBeginindex(), info.getEndIndex(), remainingAttributes);
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
        if (root != null) {
            root.printRules("");
        }
    }

    public void printTree() {
        if (root != null) {
            root.printTree("");
        }
    }
}
