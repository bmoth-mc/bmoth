package de.bmoth.backend;

public class TranslationOptions {
    public static final TranslationOptions UNPRIMED = new TranslationOptions();
    public static final TranslationOptions PRIMED_0 = new TranslationOptions(0);

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

    @Override
    public String toString() {
        return hasPrimeLevel ? "prime level " + primeLevel : "not primed";
    }
}
