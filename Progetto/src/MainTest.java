import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import data.Data;
import data.TrainingDataException;
import tree.RegressionTree;
import tree.UnknownValueException;

class MainTest {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Avvia l'esecuzione del programma, legge il dataset e costruisce l'albero.
     */
    public static void main(String[] args) {
        int decision = 0;
        do {
            System.out.println("Learn Regression Tree from data [1]");
            System.out.println("Load Regression Tree from archive [2]");
            decision = readInt(scanner);
        } while (!(decision == 1) && !(decision == 2));

        System.out.println("File name:");
        String trainingfileName = readString(scanner);

        RegressionTree tree = null;
        if (decision == 1) {
            System.out.println("Starting data acquisition phase!");
            Data trainingSet = null;
            try {
                trainingSet = new Data(resolveDataFilePath(trainingfileName + ".dat"));
            } catch (TrainingDataException e) {
                System.out.println(e);
                return;
            }

            System.out.println("Starting learning phase!");
            tree = new RegressionTree(trainingSet);
            try {
                tree.salva(trainingfileName + ".dmp");
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        } else {
            try {
                tree = RegressionTree.carica(trainingfileName + ".dmp");
            } catch (ClassNotFoundException | java.io.IOException e) {
                System.out.println(e);
                return;
            }
        }

        tree.printRules();

        char risp = 'y';
        do {
            System.out.println("Starting prediction phase!");
            try {
                System.out.println(tree.predictClass());
            } catch (UnknownValueException e) {
                System.out.println(e);
            }
            System.out.println("Would you repeat ? (y/n)");
            risp = readChar(scanner);
        } while (Character.toUpperCase(risp) == 'Y');
    }

    /**
     * Legge un intero dall'input standard in modo robusto.
     */
    private static int readInt(Scanner scanner) {
        while (true) {
            if (!scanner.hasNextLine()) {
                return -1;
            }
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("Invalid input. Please enter an integer.");
                continue;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    /**
     * Legge una stringa dall'input standard.
     */
    private static String readString(Scanner scanner) {
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine().trim();
    }

    /**
     * Legge un carattere dall'input standard.
     */
    private static char readChar(Scanner scanner) {
        if (!scanner.hasNextLine()) {
            return 'n';
        }
        String line = scanner.nextLine().trim();
        return line.isEmpty() ? 'n' : line.charAt(0);
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
