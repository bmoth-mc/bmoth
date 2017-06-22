package de.bmoth.backend;

public class TranslationOptions {
    public static TranslationOptions UNPRIMED = new TranslationOptions();
    public static TranslationOptions PRIMED_0 = new TranslationOptions(0);

    private int primeLevel;
    private final boolean hasPrimeLevel;

    private TranslationOptions() {
        this.hasPrimeLevel = false;
    }

    public TranslationOptions(int primeLevel) {
        this.hasPrimeLevel = true;
        this.primeLevel = primeLevel;
    }

    public int getPrimeLevel() {
        return primeLevel;
    }

    public boolean isHasPrimeLevel() {
        return hasPrimeLevel;
    }
}
