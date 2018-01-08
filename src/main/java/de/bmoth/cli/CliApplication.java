package de.bmoth.cli;

import java.io.File;

public class CliApplication {

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
        CliTask task = new CliTask();

        // prepare getopt
        CliGetopt g = new CliGetopt(args);

        // parse options one by one
        for (CliOption option = g.getopt(); option != null; option = g.getopt()) {
            switch (option) {
                case ALGORITHM:
                    task.setAlgorithm(g.getOptarg());
                    break;
                case BENCHMARK:
                    task.setIsBenchmark(true);
                    break;
                case MACHINE:
                    task.setMachineFile(new File(g.getOptarg()));
                    break;
                case STEPS:
                    task.setMaxSteps(Integer.parseInt(g.getOptarg()));
                    break;
                case HELP:
                default:
                    help();
            }
        }

        task.execute();
    }
}
