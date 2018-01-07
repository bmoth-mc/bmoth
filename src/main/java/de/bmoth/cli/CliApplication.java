package de.bmoth.cli;

import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.bmc.BoundedModelChecker;
import de.bmoth.modelchecker.esmc.ExplicitStateModelChecker;
import de.bmoth.modelchecker.kind.KInductionModelChecker;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.MachineNode;
import gnu.getopt.Getopt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CliApplication {
    private enum ModelCheckingAlgorithm {
        ESMC, BMC, KIND
    }

    private static void help() {
        System.out.print("bmoth-cli - BMoTH command line interface\n\n");
        System.out.print("Usage example:\n");
        System.out.print("bmoth-cli (-h|--help)|(-m|--machine) mch-file [(-a|--algorithm) esmc|bmc|kind] [(-b|--benchmark)] [(-s|--max-steps)]\n\n");
        System.out.print("Options:\n");
        System.out.print("-h or --help:             Displays this information.\n");
        System.out.print("-m or --machine string:   Machine file to be model checked.\n");
        System.out.print("-a or --algorithm string: Specify the algorithm used for model checking. (default: esmc).\n");
        System.out.print("-b or --benchmark:        Enable benchmark mode.\n");
        System.out.print("-s or --max-steps:        Optional for bmc|kind max # steps before aborting. (default: 20)\n");
        System.exit(1);
    }

    public static void main(String[] args) {
        // prepare getopt
        Getopt g = CliOption.getGetOpt(args);

        // set defaults
        ModelCheckingAlgorithm algorithm = ModelCheckingAlgorithm.ESMC;
        boolean isBenchmark = false;
        String machineContent = null;
        int maxSteps = 20;

        // parse options one by one
        CliOption option;
        while ((option = CliOption.getCliOpt(g)) != null) {
            switch (option) {
                case ALGORITHM:
                    algorithm = ModelCheckingAlgorithm.valueOf(g.getOptarg().toUpperCase());
                    break;
                case BENCHMARK:
                    isBenchmark = true;
                    break;
                case MACHINE:
                    File inputFile = new File(g.getOptarg());
                    if (!inputFile.exists() || !inputFile.isFile() || !inputFile.canRead()) {
                        System.err.println("Unable to read file " + inputFile);
                        System.exit(1);
                    }
                    try {
                        machineContent = new String(Files.readAllBytes(Paths.get(inputFile.getPath())));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case STEPS:
                    maxSteps = Integer.parseInt(g.getOptarg());
                    break;
                case HELP:
                default:
                    help();
            }
        }

        // we need a machine
        if (machineContent == null) {
            System.err.println("Missing machine");
            System.exit(1);
        }

        MachineNode machineNode = null;
        long start = 0, end;
        try {
            if (isBenchmark) {
                start = System.nanoTime();
            }
            machineNode = Parser.getMachineAsSemanticAst(machineContent);
            if (isBenchmark) {
                end = System.nanoTime();
                System.out.printf("Parsing time:\t%14d ns\n", end - start);
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }

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

        ModelCheckingResult result;
        if (isBenchmark) {
            start = System.nanoTime();
        }
        result = modelChecker.check();
        if (isBenchmark) {
            end = System.nanoTime();
            System.out.printf("Checking time:\t%14d ns\n", end - start);
        }

        System.out.printf("Result:\t%s\n", result);
    }
}
