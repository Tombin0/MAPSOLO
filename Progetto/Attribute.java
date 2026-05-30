public abstract class Attribute {
    private String name;
    private int index;

    /**
     * Costruisce un attributo con nome e indice.
     */
    public Attribute(String name, int index) {
        this.name = name;
        this.index = index;
    }

    /**
     * Restituisce il nome dell'attributo.
     */
    public String getName() {
        return name;
    }

    /**
     * Restituisce l'indice dell'attributo nella riga dei dati.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Rappresentazione testuale dell'attributo.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Confronta due attributi per nome e indice.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Attribute)) {
            return false;
        }
        Attribute other = (Attribute) obj;
        return index == other.index && name.equals(other.name);
    }

    /**
     * Calcola l'hash code basato su nome e indice.
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + index;
        result = 31 * result + name.hashCode();
        return result;
    }
}
