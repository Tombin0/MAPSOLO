import java.util.ArrayList;
import java.util.List;

public class ContinuousNode extends SplitNode {

    public ContinuousNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
    }

    @Override
    void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        Double currentSplitValue = (Double) trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());
        double bestInfoVariance = Double.POSITIVE_INFINITY;
        List<SplitInfo> bestMapSplit = null;

        for (int i = beginExampleIndex + 1; i <= endExampleIndex; i++) {
            Double value = (Double) trainingSet.getExplanatoryValue(i, attribute.getIndex());
            if (!value.equals(currentSplitValue)) {
                double varianceLeft = new LeafNode(trainingSet, beginExampleIndex, i - 1).getVariance();
                double varianceRight = new LeafNode(trainingSet, i, endExampleIndex).getVariance();
                double candidateSplitVariance = varianceLeft + varianceRight;
                if (bestMapSplit == null || candidateSplitVariance < bestInfoVariance) {
                    bestMapSplit = new ArrayList<>();
                    bestMapSplit.add(new SplitInfo(currentSplitValue, beginExampleIndex, i - 1, 0, "<="));
                    bestMapSplit.add(new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
                    bestInfoVariance = candidateSplitVariance;
                }
                currentSplitValue = value;
            }
        }
        if (bestMapSplit == null) {
            mapSplit = new SplitInfo[0];
        } else {
            mapSplit = bestMapSplit.toArray(new SplitInfo[0]);
        }
    }

    @Override
    int testCondition(Object value) {
        for (int i = 0; i < mapSplit.length; i++) {
            if (mapSplit[i].getSplitValue().equals(value)) {
                return i;
            }
        }
        return -1;
    }
}
