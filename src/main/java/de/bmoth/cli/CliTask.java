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
    private long parsingTimes;
    private long checkingTimes;

    CliTask() {
        algorithm = ModelCheckingAlgorithm.ESMC;
        isBenchmark = false;
        times = 1;
        maxSteps = 20;
        machineFile = null;
        resultFileName = null;
        parsingTimes = 0;
        checkingTimes = 0;
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

    public void executeBenchmarks() {
        if (times != 0) {
            // first parsing and checking process takes longer and is ignored
            MachineNode machineNode = parseMachine(readMachineContent());
            String result = doModelCheck(getModelChecker(machineNode)).toString();
            for (int i = 1; i <= times; i++) {
                logger.info("Executing benchmark " + i + " of " + times);
                machineNode = parseMachine(readMachineContent());
                parsingTimes += nanoDiffTime;
                result = doModelCheck(getModelChecker(machineNode)).toString();
                checkingTimes += nanoDiffTime;
            }
            StringJoiner resultString = new StringJoiner("\n", "", "");
            resultString.add("Machine: " + machineFile.getName() + ", algorithm: " + algorithm + ", times: " + times);
            resultString.add("Result: " + result);
            resultString.add("Average parsing time: " + (parsingTimes / times) + " ns");
            resultString.add("Average checking time: " + (checkingTimes / times) + " ns");
            logger.info(resultString.toString());

            if (resultFileName != null) {
                try {
                    PrintWriter writer = new PrintWriter(resultFileName, "UTF-8");
                    writer.println(resultString.toString());
                    writer.close();
                } catch (Exception e) {
                    logger.warning(e.toString());
                }
            }
        }
    }

    public void execute() {
        if (isBenchmark) {
            executeBenchmarks();
        } else {
            MachineNode machineNode = parseMachine(readMachineContent());
            ModelCheckingResult result = doModelCheck(getModelChecker(machineNode));
            logger.info("Result: " + result.toString());
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
