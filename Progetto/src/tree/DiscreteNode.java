package tree;

import java.util.ArrayList;
import java.util.List;
import data.Attribute;
import data.Data;

public class DiscreteNode extends SplitNode {

    /**
     * Costruisce un nodo di split per un attributo discreto.
     */
    DiscreteNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
    }

    /**
     * Costruisce i segmenti di esempi per ciascun valore discreto dell'attributo.
     */
    @Override
    void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        List<SplitInfo> splits = new ArrayList<>();
        int start = beginExampleIndex;
        Object currentValue = trainingSet.getExplanatoryValue(start, attribute.getIndex());
        int childIndex = 0;
        for (int i = beginExampleIndex + 1; i <= endExampleIndex; i++) {
            Object value = trainingSet.getExplanatoryValue(i, attribute.getIndex());
            if (!currentValue.equals(value)) {
                splits.add(new SplitInfo(currentValue, start, i - 1, childIndex++));
                start = i;
                currentValue = value;
            }
        }
        splits.add(new SplitInfo(currentValue, start, endExampleIndex, childIndex));
        mapSplit = splits;
    }

    /**
     * Restituisce il ramo corrispondente al valore discreto di input.
     */
    @Override
    int testCondition(Object value) {
        int index = 0;
        for (SplitInfo info : mapSplit) {
            if (info.getSplitValue().equals(value)) {
                return index;
            }
            index++;
        }
        return -1;
    }
}
