package de.bmoth.cli;

public enum CliOption {
    HELP('h', "help", false),
    ALGORITHM('a', "algorithm", true),
    MACHINE('m', "machine", true),
    BENCHMARK('b', "benchmark", false),
    STEPS('s', "max-steps", true);

    private final char shortOption;
    private final String longOption;
    private final boolean hasArgument;

    CliOption(char shortOption, String longOption, boolean hasArgument) {
        this.shortOption = shortOption;
        this.longOption = longOption;
        this.hasArgument = hasArgument;
    }

    public char getShortOption() {
        return shortOption;
    }

    public String getLongOption() {
        return longOption;
    }

    public boolean isHasArgument() {
        return hasArgument;
    }
}
