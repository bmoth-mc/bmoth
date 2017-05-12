package de.bmoth.backend;

public class TranslationOptions {
    private int primeLevel;

    public TranslationOptions() {
        primeLevel = 0;
    }

    public TranslationOptions(int primeLevel) {
        this.primeLevel = primeLevel;
    }

    public int getPrimeLevel() {
        return primeLevel;
    }
}
