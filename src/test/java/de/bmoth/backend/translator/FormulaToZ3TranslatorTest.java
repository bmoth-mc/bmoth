package de.bmoth.backend.translator;

import com.microsoft.z3.Context;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import de.bmoth.parser.ast.types.Type;
import de.bmoth.parser.ast.types.UnificationException;
import org.junit.Test;

public class FormulaToZ3TranslatorTest {

    @Test(expected = AssertionError.class)
    public void testBTypeToZ3Sort() {
        FormulaToZ3Translator.bTypeToZ3Sort(new Context(), new Type() {
            @Override
            public Type unify(Type otherType) throws UnificationException {
                return null;
            }

            @Override
            public boolean unifiable(Type otherType) {
                return false;
            }

            @Override
            public boolean contains(Type other) {
                return false;
            }

            @Override
            public boolean isUntyped() {
                return false;
            }
        });
    }
}
