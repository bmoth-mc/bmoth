package de.bmoth.backend;

public class TranslationOptions {
    private int primeLevel;
    private final boolean hasPrimeLevel;

    public TranslationOptions() {
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
