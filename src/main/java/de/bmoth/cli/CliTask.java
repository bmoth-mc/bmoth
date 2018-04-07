package de.bmoth.cli;

import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.bmc.BoundedModelChecker;
import de.bmoth.modelchecker.esmc.ExplicitStateModelChecker;
import de.bmoth.modelchecker.kind.KInductionModelChecker;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.MachineNode;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringJoiner;
import java.util.logging.Logger;

public class CliTask {
    private static Logger logger = Logger.getAnonymousLogger();

    private boolean isBenchmark;
    private int maxSteps;
    private int times;
    private ModelCheckingAlgorithm algorithm;
    private File machineFile;
    private String resultFileName;

    private long nanoDiffTime;

    CliTask() {
        algorithm = ModelCheckingAlgorithm.ESMC;
        isBenchmark = false;
        times = 1;
        maxSteps = 20;
        machineFile = null;
        resultFileName = null;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = ModelCheckingAlgorithm.valueOf(algorithm.toUpperCase());
        logger.config("Setting algorithm to " + this.algorithm.verbose());
    }

    public void setIsBenchmark() {
        this.isBenchmark = true;
        logger.config("Enabling benchmark");
    }

    public void setMachineFile(File machineFile) {
        this.machineFile = machineFile;
        if (machineFile == null || !machineFile.exists()) {
            logger.warning("Setting invalid machine file");
        } else {
            logger.config("Setting machine file to " + machineFile.getAbsolutePath());
        }
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
        logger.config("Setting max steps to " + maxSteps);
    }

    public void setTimes(int times) {
        this.times = times;
        logger.config("Setting times to " + times);
    }

    public void setResultFileName(String resultFileName) {
        this.resultFileName = resultFileName;
        logger.config("Setting resultFileName to " + resultFileName);
    }

    public void execute() {
        StringJoiner resultString = new StringJoiner("\n", "", "");
        MachineNode machineNode = parseMachine(readMachineContent());
        if (isBenchmark) {
            logger.info("Parsing time: " + nanoDiffTime + " ns");
            resultString.add("Parsing time: " + nanoDiffTime + " ns");
        }
        ModelCheckingResult result = doModelCheck(getModelChecker(machineNode));
        if (isBenchmark) {
            logger.info("Checking time: " + nanoDiffTime + " ns");
            resultString.add("Checking time: " + nanoDiffTime + " ns");
        }
        logger.info("Result: " + result.toString());
        resultString.add(result.toString());
        try {
            PrintWriter writer = new PrintWriter(resultFileName, "UTF-8");
            writer.println(resultString.toString());
            writer.close();
        } catch (Exception e) {
            logger.warning(e.toString());
        }

    }

    private ModelCheckingResult doModelCheck(ModelChecker modelChecker) {
        ModelCheckingResult result;
        long start = isBenchmark ? System.nanoTime() : 0;
        result = modelChecker.check();
        nanoDiffTime = isBenchmark ? System.nanoTime() - start : 0;
        return result;
    }

    private ModelChecker getModelChecker(MachineNode machineNode) {
        switch (algorithm) {
            case ESMC:
                return new ExplicitStateModelChecker(machineNode);
            case BMC:
                return new BoundedModelChecker(machineNode, maxSteps);
            case KIND:
                return new KInductionModelChecker(machineNode, maxSteps);
            default:
                // should not be reachable
                logger.severe("Unknown algorithm " + algorithm);
                System.exit(1);
                return null;
        }
    }

    private MachineNode parseMachine(String machineContent) {
        MachineNode machineNode = null;
        try {
            long start = isBenchmark ? System.nanoTime() : 0;
            machineNode = Parser.getMachineAsSemanticAst(machineContent);
            nanoDiffTime = isBenchmark ? System.nanoTime() - start : 0;
        } catch (ParserException e) {
            logger.severe(e.toString());
            System.exit(1);
        }

        if (machineNode == null) {
            logger.severe("Invalid machine");
            System.exit(1);
        }

        return machineNode;
    }

    private String readMachineContent() {
        if (machineFile == null || !machineFile.exists() || !machineFile.isFile() || !machineFile.canRead()) {
            logger.severe("Unable to read file " + machineFile);
            System.exit(1);
        }
        String machineContent = null;
        try {
            machineContent = new String(Files.readAllBytes(Paths.get(machineFile.getPath())));
        } catch (IOException e) {
            logger.severe(e.toString());
            System.exit(1);
        }
        // we need a machine
        if (machineContent == null || machineContent.isEmpty()) {
            logger.severe("Missing machine");
            System.exit(1);
        }

        return machineContent;
    }

    private enum ModelCheckingAlgorithm {
        ESMC("Explicit-state model checking"), BMC("Bounded model checking"), KIND("k-induction model checking");

        private final String name;

        ModelCheckingAlgorithm(String name) {
            this.name = name;
        }

        public String verbose() {
            return name;
        }
    }
}
