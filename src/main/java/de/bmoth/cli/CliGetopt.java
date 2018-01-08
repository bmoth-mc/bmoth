package de.bmoth.cli;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

class CliGetopt {
    private final Getopt getopt;

    CliGetopt(String[] args) {
        CliOption[] options = CliOption.values();
        StringBuilder shortOptionsBuilder = new StringBuilder();
        LongOpt[] longOptions = new LongOpt[options.length];

        for (int i = 0; i < options.length; ++i) {
            shortOptionsBuilder.append(options[i].getShortOption());

            if (options[i].isHasArgument()) {
                shortOptionsBuilder.append(':');
                longOptions[i] = new LongOpt(options[i].getLongOption(), LongOpt.REQUIRED_ARGUMENT, null, options[i].getShortOption());
            } else {
                longOptions[i] = new LongOpt(options[i].getLongOption(), LongOpt.NO_ARGUMENT, null, options[i].getShortOption());
            }
        }

        getopt = new Getopt("bmoth", args, shortOptionsBuilder.toString(), longOptions);
    }

    public CliOption getopt() {
        int o = getopt.getopt();
        for (CliOption option : CliOption.values()) {
            if ((char) o == option.getShortOption()) {
                return option;
            }
        }
        return null;
    }

    public String getOptarg() {
        return getopt.getOptarg();
    }
}
