import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

class MainTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws FileNotFoundException {
        String dataFileName = resolveDataFilePath(args);
        Data trainingSet = new Data(dataFileName);

        RegressionTree tree = new RegressionTree(trainingSet);

        tree.printRules();
        tree.printTree();
    }

    private static String resolveDataFilePath(String[] args) {
        if (args.length > 0) {
            return args[0];
        }

        Path cwd = Paths.get(System.getProperty("user.dir"));
        Path candidate = cwd.resolve("servo.dat");
        if (candidate.toFile().exists()) {
            return candidate.toString();
        }

        candidate = cwd.resolve("Progetto").resolve("servo.dat");
        if (candidate.toFile().exists()) {
            return candidate.toString();
        }

        candidate = cwd.resolve("../Progetto").resolve("servo.dat").normalize();
        if (candidate.toFile().exists()) {
            return candidate.toString();
        }

        // Fallback to relative path in Progetto when running from workspace root
        return "Progetto" + File.separator + "servo.dat";
    }
}
