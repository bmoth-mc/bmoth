package de.bmoth.cli;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

public enum CliOption {
    HELP('h', "help", false),
    ALGORITHM('a', "algorithm", true),
    MACHINE('m', "machine", true),
    BENCHMARK('b', "banchmark", false),
    STEPS('s', "max-steps", true);

    private final char shortOption;
    private final String longOption;
    private final boolean hasArgument;

    CliOption(char shortOption, String longOption, boolean hasArgument) {
        this.shortOption = shortOption;
        this.longOption = longOption;
        this.hasArgument = hasArgument;
    }

    public static Getopt getGetOpt(String[] args) {
        CliOption[] options = CliOption.values();
        StringBuilder sb = new StringBuilder();
        LongOpt[] longOptions = new LongOpt[options.length];

        for (int i = 0; i < options.length; ++i) {
            sb.append(options[i].shortOption);
            longOptions[i] = new LongOpt(options[i].longOption, (options[i].hasArgument ? LongOpt.REQUIRED_ARGUMENT : LongOpt.NO_ARGUMENT), null, options[i].shortOption);

            if (options[i].hasArgument) {
                sb.append(':');
            }
        }

        return new Getopt("bmoth", args, sb.toString(), longOptions);
    }

    public static CliOption getCliOpt(Getopt g) {
        int o = g.getopt();
        for (CliOption option : CliOption.values()) {
            if ((char) o == option.shortOption) {
                return option;
            }
        }
        return null;
    }
}
