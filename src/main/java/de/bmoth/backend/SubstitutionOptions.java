package de.bmoth.backend;

public class SubstitutionOptions {
    private final TranslationOptions lhs;
    private final TranslationOptions rhs;

    public SubstitutionOptions(TranslationOptions lhs, TranslationOptions rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public TranslationOptions getLhs() {
        return lhs;
    }

    public TranslationOptions getRhs() {
        return rhs;
    }

}
