package de.bmoth;

public class TestConstants {

    private TestConstants() {}

    public static final String INTEGER = "INTEGER";
    public static final String INTEGER_INTEGER = "INTEGER*INTEGER";
    public static final String POW_INTEGER = "POW(INTEGER)";
    public static final String POW_INTEGER_INTEGER = "POW(INTEGER*INTEGER)";
    public static final String POW_POW_INTEGER = "POW(POW(INTEGER))";
    public static final String POW_POW_INTEGER_INTEGER = "POW(POW(INTEGER*INTEGER))";
    public static final String BOOL = "BOOL";

    public static final String MACHINE_NAME = "MACHINE test\n";
    public static final String ONE_CONSTANT = "CONSTANTS k \n";
    public static final String TWO_CONSTANTS = "CONSTANTS k,k2 \n";
    public static final String THREE_CONSTANTS = "CONSTANTS k,k2,k3 \n";

}
