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
import java.nio.file.Files;
import java.nio.file.Paths;

public class CliTask {
    private boolean isBenchmark;
    private int maxSteps;
    private ModelCheckingAlgorithm algorithm;
    private File machineFile;
    private long nanoDiffTime;

    CliTask() {
        algorithm = ModelCheckingAlgorithm.ESMC;
        isBenchmark = false;
        machineFile = null;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = ModelCheckingAlgorithm.valueOf(algorithm.toUpperCase());
    }

    public void setIsBenchmark(boolean isBenchmark) {
        this.isBenchmark = isBenchmark;
    }

    public void setMachineFile(File machineFile) {
        this.machineFile = machineFile;
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    public void execute() {
        MachineNode machineNode = parseMachine(readMachineContent());
        if (isBenchmark) {
            System.out.printf("Parsing time:\t%10d ns\n", nanoDiffTime);
        }
        ModelCheckingResult result = doModelCheck(getModelChecker(machineNode));
        if (isBenchmark) {
            System.out.printf("Checking time:\t%10d ns\n", nanoDiffTime);
        }
        System.out.println(result);
    }

    private ModelCheckingResult doModelCheck(ModelChecker modelChecker) {
        ModelCheckingResult result;
        long start = isBenchmark ? System.nanoTime() : 0;
        result = modelChecker.check();
        nanoDiffTime = isBenchmark ? System.nanoTime() - start : 0;
        return result;
    }

    private ModelChecker getModelChecker(MachineNode machineNode) {
        ModelChecker modelChecker = null;
        switch (algorithm) {
            case ESMC:
                modelChecker = new ExplicitStateModelChecker(machineNode);
                break;
            case BMC:
                modelChecker = new BoundedModelChecker(machineNode, maxSteps);
                break;
            case KIND:
                modelChecker = new KInductionModelChecker(machineNode, maxSteps);
                break;
        }

        return modelChecker;
    }

    private MachineNode parseMachine(String machineContent) {
        MachineNode machineNode = null;
        try {
            long start = isBenchmark ? System.nanoTime() : 0;
            machineNode = Parser.getMachineAsSemanticAst(machineContent);
            nanoDiffTime = isBenchmark ? System.nanoTime() - start : 0;
        } catch (ParserException e) {
            e.printStackTrace();
        }

        if (machineNode == null) {
            System.err.println("Invalid machine");
            System.exit(1);
        }

        return machineNode;
    }

    private String readMachineContent() {
        if (machineFile == null || !machineFile.exists() || !machineFile.isFile() || !machineFile.canRead()) {
            System.err.println("Unable to read file " + machineFile);
            System.exit(1);
        }
        String machineContent = null;
        try {
            machineContent = new String(Files.readAllBytes(Paths.get(machineFile.getPath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // we need a machine
        if (machineContent == null || machineContent.isEmpty()) {
            System.err.println("Missing machine");
            System.exit(1);
        }

        return machineContent;
    }

    private enum ModelCheckingAlgorithm {
        ESMC, BMC, KIND
    }
}
