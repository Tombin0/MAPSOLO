import java.io.File;
import java.util.Scanner;
import java.util.List;
import java.util.LinkedList;

public class Data implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private Object data [][];
    private int numberOfExamples;
    private List<Attribute> explanatorySet;
    private ContinuousAttribute classAttribute;

    /**
     * Carica il dataset dal file e inizializza attributi e target.
     */
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

            explanatorySet = new LinkedList<Attribute>();
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
                    if (s.length >= 3) {
                        String discreteValues[] = s[2].split(",");
                        explanatorySet.add(new DiscreteAttribute(s[1], iAttribute, discreteValues));
                    } else {
                        // attribute declared without explicit discrete values -> treat as continuous
                        explanatorySet.add(new ContinuousAttribute(s[1], iAttribute));
                    }
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

            data = new Object[numberOfExamples][explanatorySet.size() + 1];
            int iRow = 0;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                if (line.trim().isEmpty()) {
                    continue;
                }
                s = line.split(",");
                if (s.length != explanatorySet.size() + 1) {
                    throw new TrainingDataException("Invalid example format");
                }
                for (int jColumn = 0; jColumn < s.length - 1; jColumn++) {
                    Attribute attr = explanatorySet.get(jColumn);
                    if (attr instanceof ContinuousAttribute) {
                        try {
                            data[iRow][jColumn] = Double.parseDouble(s[jColumn].trim());
                        } catch (NumberFormatException e) {
                            throw new TrainingDataException("Invalid numeric explanatory value", e);
                        }
                    } else {
                        data[iRow][jColumn] = s[jColumn].trim();
                    }
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

    /**
     * Restituisce la rappresentazione testuale dell'intero dataset.
     */
    @Override
    public String toString(){
        StringBuilder value = new StringBuilder();
        for(int i = 0; i < numberOfExamples; i++){
            for(int j = 0; j < explanatorySet.size(); j++)
                value.append(data[i][j]).append(",");
            value.append(data[i][explanatorySet.size()]).append("\n");
        }
        return value.toString();
    }

    /**
     * Restituisce il numero di attributi esplicativi disponibili.
     */
    public int getNumberOfExplanatoryAttributes(){
        return explanatorySet.size();
    }

    /**
     * Restituisce il numero di esempi presenti nel dataset.
     */
    public int getNumberOfExamples(){
        return numberOfExamples;
    }

    /**
     * Restituisce il valore del target per l'esempio richiesto.
     */
    public Double getClassValue(int exampleIndex){
        return (Double) data[exampleIndex][explanatorySet.size()];
    }

    /**
     * Restituisce il valore dell'attributo esplicativo per l'esempio specificato.
     */
    public Object getExplanatoryValue(int exampleIndex, int attributeIndex){
        return data[exampleIndex][attributeIndex];
    }

    /**
     * Restituisce l'attributo esplicativo in posizione index.
     */
    public Attribute getExplanatoryAttribute(int index){
        return explanatorySet.get(index);
    }

    /**
     * Restituisce l'attributo target continuo.
     */
    public ContinuousAttribute getClassAttribute(){
        return classAttribute;
    }

    /**
     * Ordina gli esempi in base all'attributo specificato.
     */
    void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex){
        quicksort(attribute, beginExampleIndex, endExampleIndex);
    }

    /**
     * Scambia due righe del dataset mantenendo tutti gli attributi e il target.
     */
    private void swap(int i, int j){
        Object temp;
        for (int k = 0; k < getNumberOfExplanatoryAttributes() + 1; k++){
            temp = data[i][k];
            data[i][k] = data[j][k];
            data[j][k] = temp;
        }
    }

    /**
     * Confronta due valori esplicativi per l'ordinamento.
     * Supporta valori numerici e stringhe.
     */
    private int compareExplanatoryValues(Object a, Object b) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        if (a instanceof Number && b instanceof Number) {
            return Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue());
        }
        return a.toString().compareTo(b.toString());
    }

    /**
     * Partiziona il dataset usando l'attributo specificato come pivot.
     */
    private int partition(Attribute attribute, int inf, int sup){
        int i = inf;
        int j = sup;
        int med = (inf + sup) / 2;
        Object pivot = getExplanatoryValue(med, attribute.getIndex());
        swap(inf, med);
        while (true) {
            while (i <= sup && compareExplanatoryValues(getExplanatoryValue(i, attribute.getIndex()), pivot) <= 0) {
                i++;
            }
            while (compareExplanatoryValues(getExplanatoryValue(j, attribute.getIndex()), pivot) > 0) {
                j--;
            }
            if (i < j) {
                swap(i, j);
            }
            else break;
        }
        swap(inf, j);
        return j;
    }

    /**
     * Implementa l'algoritmo quicksort ricorsivo per ordinare il dataset.
     */
    private void quicksort(Attribute attribute, int inf, int sup){
        if(sup >= inf){
            int pos = partition(attribute, inf, sup);
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

    /**
     * Test locale per stampare il dataset e provare l'ordinamento.
     */
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
