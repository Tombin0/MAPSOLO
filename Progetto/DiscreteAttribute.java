public class DiscreteAttribute extends Attribute {
    private String[] values;

    /**
     * Costruisce un attributo discreto con l'insieme dei valori possibili.
     */
    public DiscreteAttribute(String name, int index, String[] values) {
        super(name, index);
        this.values = new String[values.length];
        System.arraycopy(values, 0, this.values, 0, values.length);
    }

    /**
     * Restituisce il numero di valori distinti supportati dall'attributo.
     */
    public int getNumberOfDistinctValues() {
        return values.length;
    }

    /**
     * Restituisce il valore discreto alla posizione specificata.
     */
    public String getValue(int i) {
        return values[i];
    }

    /**
     * Restituisce una copia dell'array dei valori distinti.
     */
    public String[] getValues() {
        return values.clone();
    }
}
