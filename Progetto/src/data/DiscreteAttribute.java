package data;

import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;

public class DiscreteAttribute extends Attribute implements Iterable<String> {
    private Set<String> values;

    /**
     * Costruisce un attributo discreto con l'insieme dei valori possibili.
     */
    public DiscreteAttribute(String name, int index, String[] valuesArray) {
        super(name, index);
        this.values = new TreeSet<String>();
        for (String value : valuesArray) {
            this.values.add(value);
        }
    }

    /**
     * Restituisce il numero di valori distinti supportati dall'attributo.
     */
    public int getNumberOfDistinctValues() {
        return values.size();
    }

    /**
     * Restituisce il valore discreto alla posizione specificata.
     */
    public String getValue(int i) {
        int index = 0;
        for (String value : values) {
            if (index == i) {
                return value;
            }
            index++;
        }
        return null;
    }

    /**
     * Restituisce una copia dell'array dei valori distinti.
     */
    public String[] getValues() {
        return values.toArray(new String[0]);
    }

    /**
     * Implementa l'interfaccia Iterable per iterare sui valori distinti.
     */
    @Override
    public Iterator<String> iterator() {
        return values.iterator();
    }
}
