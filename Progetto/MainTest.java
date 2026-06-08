import java.nio.file.Path;
import java.nio.file.Paths;
import utility.Keyboard;

class MainTest {

    /**
     * Avvia l'esecuzione del programma, legge il dataset e costruisce l'albero.
     */
    public static void main(String[] args) {
        String dataFileName;
        if (args.length > 0) {
            dataFileName = args[0];
        } else {
            System.out.println("Training set:");
            dataFileName = Keyboard.readString();
        }

        dataFileName = resolveDataFilePath(dataFileName);

        System.out.println("Starting data acquisition phase!");
        Data trainingSet;
        try {
            trainingSet = new Data(dataFileName);
        } catch (TrainingDataException e) {
            System.out.println(e);
            return;
        }

        System.out.println("Starting learning phase!");
        RegressionTree tree = new RegressionTree(trainingSet);

        tree.printRules();
        tree.printTree();

        char risp;
        do {
            System.out.println("Starting prediction phase!");
            try {
                System.out.println(tree.predictClass());
            } catch (UnknownValueException e) {
                System.out.println(e);
            }
            System.out.println("Would you repeat ? (y/n)");
            risp = Keyboard.readChar();
        } while (Character.toUpperCase(risp) == 'Y');
    }

    /**
     * Risolve il percorso del file di dati a partire dalla directory corrente e dalle posizioni attese.
     */
    private static String resolveDataFilePath(String fileName) {
        Path filePath = Paths.get(fileName);
        if (filePath.toFile().exists()) {
            return fileName;
        }

        Path cwd = Paths.get(System.getProperty("user.dir"));
        Path candidate = cwd.resolve(fileName);
        if (candidate.toFile().exists()) {
            return candidate.toString();
        }

        candidate = cwd.resolve("Progetto").resolve(fileName);
        if (candidate.toFile().exists()) {
            return candidate.toString();
        }

        candidate = cwd.resolve("..")
                .resolve("Progetto")
                .resolve(fileName)
                .normalize();
        if (candidate.toFile().exists()) {
            return candidate.toString();
        }

        return fileName;
    }
}
