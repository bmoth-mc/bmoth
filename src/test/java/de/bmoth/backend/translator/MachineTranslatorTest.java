package de.bmoth.backend.translator;

import com.microsoft.z3.Context;
import de.bmoth.TestParser;
import de.bmoth.backend.SubstitutionOptions;
import de.bmoth.backend.TranslationOptions;
import de.bmoth.backend.z3.MachineToZ3Translator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MachineTranslatorTest extends TestParser {

    MachineToZ3Translator translator;

    @Before
    public void init() {
        String machine = "MACHINE SimpleMachine\n";
        machine += "VARIABLES x\n";
        machine += "INVARIANT x : INTEGER &\n";
        machine += "\tx**2 = x*x \n";
        machine += "INITIALISATION x := -3\n";
        machine += "OPERATIONS\n";
        machine += "\tIncX = SELECT x < 50 THEN x := x+1 END\n";
        machine += "END";

        translator = new MachineToZ3Translator(parseMachine(machine), new Context());
    }

    @Test
    public void testInvariantGeneration() {
        assertEquals(translator.getInvariantConstraint(), translator.getInvariantConstraint(TranslationOptions.UNPRIMED));
        assertEquals("(let ((a!1 (forall ((a Int) (b Int))\n" +
            "             (! (let ((a!1 (ite (= 0 (mod b 2))\n" +
            "                                (* (POWER_OF a (div b 2))\n" +
            "                                   (POWER_OF a (div b 2)))\n" +
            "                                (* a (POWER_OF a (- b 1))))))\n" +
            "                  (= (POWER_OF a b) (ite (= 0 b) 1 a!1)))\n" +
            "                :pattern ((POWER_OF a b))\n" +
            "                :weight 2\n" +
            "                :qid |:rec-fun|))))\n" +
            "  (and true (= (POWER_OF x 2) (* x x)) a!1))", translator.getInvariantConstraint().toString());

        assertEquals("(let ((a!1 (forall ((a Int) (b Int))\n" +
            "             (! (let ((a!1 (ite (= 0 (mod b 2))\n" +
            "                                (* (POWER_OF a (div b 2))\n" +
            "                                   (POWER_OF a (div b 2)))\n" +
            "                                (* a (POWER_OF a (- b 1))))))\n" +
            "                  (= (POWER_OF a b) (ite (= 0 b) 1 a!1)))\n" +
            "                :pattern ((POWER_OF a b))\n" +
            "                :weight 2\n" +
            "                :qid |:rec-fun|))))\n" +
            "  (and true (= (POWER_OF |x'24| 2) (* |x'24| |x'24|)) a!1))", translator.getInvariantConstraint(new TranslationOptions(24)).toString());
    }

    @Test
    public void testInitialValueGeneration() {
        assertEquals(translator.getInitialValueConstraint(), translator.getInitialValueConstraint(TranslationOptions.PRIMED_0));
        assertEquals("(= |x'0| (- 3))", translator.getInitialValueConstraint().toString());
        assertEquals("(= |x'1024| (- 3))", translator.getInitialValueConstraint(new TranslationOptions(1024)).toString());
    }

    @Test
    public void testOperationsGeneration() {
        assertEquals(1, translator.getOperationConstraints().size());
        assertEquals(translator.getOperationConstraints(), translator.getOperationConstraints(new SubstitutionOptions(TranslationOptions.PRIMED_0, TranslationOptions.UNPRIMED)));

        assertEquals("[(and (< x 50) (= |x'0| (+ x 1)))]", translator.getOperationConstraints().toString());
        assertEquals("[(and (< |x'128| 50) (= |x'512| (+ |x'128| 1)))]", translator.getOperationConstraints(new SubstitutionOptions(new TranslationOptions(512), new TranslationOptions(128))).toString());
    }

    @Test
    public void testCombinedOperationsGeneration() {
        assertEquals(translator.getCombinedOperationConstraint(), translator.getCombinedOperationConstraint(new SubstitutionOptions(TranslationOptions.PRIMED_0, TranslationOptions.UNPRIMED)));

        assertEquals("(and (< x 50) (= |x'0| (+ x 1)))", translator.getCombinedOperationConstraint().toString());
        assertEquals("(and (< |x'128| 50) (= |x'512| (+ |x'128| 1)))", translator.getCombinedOperationConstraint(new SubstitutionOptions(new TranslationOptions(512), new TranslationOptions(128))).toString());
    }
}
