package de.bmoth.backend;

public class TranslationOptions {
    private int primeLevel;

    public TranslationOptions() {
        this(0);
    }

    public TranslationOptions(int primeLevel) {
        this.primeLevel = primeLevel;
    }

    public int getPrimeLevel() {
        return primeLevel;
    }
}
