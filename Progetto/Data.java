import java.io.File;
import java.util.Scanner;

public class Data {
    private Object data [][];
    private int numberOfExamples;
    private Attribute explanatorySet[];
    private ContinuousAttribute classAttribute;

    Data(String fileName) throws TrainingDataException {
        File inFile = new File(fileName);
        Scanner sc;
        try {
            sc = new Scanner(inFile);
        } catch (java.io.FileNotFoundException e) {
            throw new TrainingDataException(e);
        }

        try {
            if (!sc.hasNextLine()) {
                throw new TrainingDataException("Training file is empty");
            }

            String line = sc.nextLine().trim();
            if (!line.contains("@schema")) {
                throw new TrainingDataException("Missing schema");
            }

            String s[] = line.split(" ");
            if (s.length < 2) {
                throw new TrainingDataException("Invalid schema declaration");
            }

            explanatorySet = new Attribute[Integer.parseInt(s[1])];
            int iAttribute = 0;
            while (sc.hasNextLine()) {
                line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.contains("@data")) {
                    break;
                }
                s = line.split(" ");
                if (s[0].equals("@desc")) {
                    String discreteValues[] = s[2].split(",");
                    explanatorySet[iAttribute] = new DiscreteAttribute(s[1], iAttribute, discreteValues);
                } else if (s[0].equals("@target")) {
                    classAttribute = new ContinuousAttribute(s[1], iAttribute);
                }
                iAttribute++;
            }

            if (classAttribute == null) {
                throw new TrainingDataException("Missing numeric target attribute");
            }
            if (!line.contains("@data")) {
                throw new TrainingDataException("Missing data section");
            }

            String[] parts = line.split(" ");
            if (parts.length < 2) {
                throw new TrainingDataException("Invalid data declaration");
            }

            numberOfExamples = Integer.parseInt(parts[1]);
            if (numberOfExamples <= 0) {
                throw new TrainingDataException("Training set empty");
            }

            data = new Object[numberOfExamples][explanatorySet.length + 1];
            int iRow = 0;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                if (line.trim().isEmpty()) {
                    continue;
                }
                s = line.split(",");
                if (s.length != explanatorySet.length + 1) {
                    throw new TrainingDataException("Invalid example format");
                }
                for (int jColumn = 0; jColumn < s.length - 1; jColumn++) {
                    data[iRow][jColumn] = s[jColumn].trim();
                }
                try {
                    data[iRow][s.length - 1] = Double.parseDouble(s[s.length - 1].trim());
                } catch (NumberFormatException e) {
                    throw new TrainingDataException("Invalid numeric target value", e);
                }
                iRow++;
            }

            if (iRow != numberOfExamples) {
                throw new TrainingDataException("Training set contains " + iRow + " examples but declared " + numberOfExamples);
            }
        } finally {
            sc.close();
        }
    }

    public String toString(){
        StringBuilder value = new StringBuilder();
        for(int i = 0; i < numberOfExamples; i++){
            for(int j = 0; j < explanatorySet.length; j++)
                value.append(data[i][j]).append(",");
            value.append(data[i][explanatorySet.length]).append("\n");
        }
        return value.toString();
    }

    public int getNumberOfExplanatoryAttributes(){
        return explanatorySet.length;
    }

    public int getNumberOfExamples(){
        return numberOfExamples;
    }

    public Double getClassValue(int exampleIndex){
        return (Double) data[exampleIndex][explanatorySet.length];
    }

    public Object getExplanatoryValue(int exampleIndex, int attributeIndex){
        return data[exampleIndex][attributeIndex];
    }

    public Attribute getExplanatoryAttribute(int index){
        return explanatorySet[index];
    }

    public ContinuousAttribute getClassAttribute(){
        return classAttribute;
    }

    void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex){
        quicksort(attribute, beginExampleIndex, endExampleIndex);
    }

    private void swap(int i, int j){
        Object temp;
        for (int k = 0; k < getNumberOfExplanatoryAttributes() + 1; k++){
            temp = data[i][k];
            data[i][k] = data[j][k];
            data[j][k] = temp;
        }
    }

    private int partition(DiscreteAttribute attribute, int inf, int sup){
        int i, j;
        i = inf;
        j = sup;
        int med = (inf + sup) / 2;
        String x = (String) getExplanatoryValue(med, attribute.getIndex());
        swap(inf, med);
        while (true) {
            while(i <= sup && ((String) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0){
                i++;
            }
            while(((String) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
                j--;
            }
            if(i < j) {
                swap(i, j);
            }
            else break;
        }
        swap(inf, j);
        return j;
    }

    private void quicksort(Attribute attribute, int inf, int sup){
        if(sup >= inf){
            int pos = partition((DiscreteAttribute) attribute, inf, sup);
            if ((pos - inf) < (sup - pos + 1)) {
                quicksort(attribute, inf, pos - 1);
                quicksort(attribute, pos + 1, sup);
            }
            else{
                quicksort(attribute, pos + 1, sup);
                quicksort(attribute, inf, pos - 1);
            }
        }
    }

    public static void main(String args[]) throws TrainingDataException {
        Data trainingSet = new Data("servo.dat");
        System.out.println(trainingSet);
        for(int jColumn = 0; jColumn < trainingSet.getNumberOfExplanatoryAttributes(); jColumn++){
            System.out.println("ORDER BY " + trainingSet.getExplanatoryAttribute(jColumn));
            trainingSet.quicksort(trainingSet.getExplanatoryAttribute(jColumn), 0, trainingSet.getNumberOfExamples()-1);
            System.out.println(trainingSet);
        }
    }
}
