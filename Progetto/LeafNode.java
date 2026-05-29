class LeafNode extends Node {
    private final double mean;
    private final double variance;

    LeafNode(Data trainingSet, int beginIndex, int endIndex) {
        super(trainingSet, beginIndex, endIndex);
        double sum = 0.0;
        int count = endIndex - beginIndex + 1;
        for (int i = beginIndex; i <= endIndex; i++) {
            sum += trainingSet.getClassValue(i);
        }
        mean = sum / count;
        double sumSq = 0.0;
        for (int i = beginIndex; i <= endIndex; i++) {
            double diff = trainingSet.getClassValue(i) - mean;
            sumSq += diff * diff;
        }
        variance = sumSq;
    }

    @Override
    double getVariance() {
        return variance;
    }

    double getMean() {
        return mean;
    }

    @Override
    void printTree(String indent) {
        System.out.printf("%sLEAF %s mean=%.6f variance=%.6f%n", indent, super.toString(), mean, variance);
    }

    @Override
    void printRules(String prefix) {
        String rule = prefix.trim();
        if (rule.endsWith("AND")) {
            rule = rule.substring(0, rule.length() - 3).trim();
        }
        if (rule.isEmpty()) {
            System.out.printf("IF TRUE THEN class=%.6f%n", mean);
        } else {
            System.out.printf("IF %s THEN class=%.6f%n", rule, mean);
        }
    }
}
