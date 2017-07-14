package de.bmoth.parser.typechecker;

import org.junit.Ignore;
import org.junit.Test;

import static de.bmoth.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DefinitionsTest {

    @Test
    public void testExpressionDefinitionsWithoutParameters() {
        String machine = MACHINE_NAME;
        machine += ONE_CONSTANT;
        machine += "PROPERTIES foo = k \n";
        machine += "DEFINITIONS foo == 1 \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k").toString());
    }

    @Test
    public void testExpressionDefinitionsWithParameters() {
        String machine = MACHINE_NAME;
        machine += TWO_CONSTANTS;
        machine += "PROPERTIES foo(k) = k2 \n";
        machine += "DEFINITIONS foo(a) == a + 1 \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k").toString());
        assertEquals(INTEGER, t.getConstants().get("k2").toString());
    }

    @Test
    public void testPredicateDefinitionsWithoutParameters() {
        String machine = MACHINE_NAME;
        machine += ONE_CONSTANT;
        machine += "PROPERTIES foo \n";
        machine += "DEFINITIONS foo == 1 = k \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k").toString());
    }

    @Test
    public void testPredicateDefinitionsWithParameters() {
        String machine = MACHINE_NAME;
        machine += ONE_CONSTANT;
        machine += "PROPERTIES foo(1) \n";
        machine += "DEFINITIONS foo(a) == a = k \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k").toString());
    }

    @Test
    public void testAmbigiousDefinition() {
        String machine = MACHINE_NAME;
        machine += ONE_CONSTANT;
        machine += "PROPERTIES foo = k \n";
        machine += "DEFINITIONS foo == bar ; bar == 2  \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k").toString());
    }

    @Test
    public void testAmbigiousDefinition2() {
        String machine = MACHINE_NAME;
        machine += ONE_CONSTANT;
        machine += "PROPERTIES foo \n";
        machine += "DEFINITIONS foo == bar ; bar == bazz ; bazz == k = 1  \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k").toString());
    }

    @Ignore
    @Test
    public void testVariablesCapturing() {
        String machine = MACHINE_NAME;
        machine += ONE_CONSTANT;
        machine += "PROPERTIES k = 1 & foo(1) \n";
        machine += "DEFINITIONS foo(a) == !k.(k : BOOL => k = 1)  \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k").toString());
    }

    @Ignore
    @Test
    public void testSubstitutionDefinitionsWithoutParameters() {
        String machine = MACHINE_NAME;
        machine += "DEFINITIONS foo == skip \n";
        machine += "OPERATIONS op1 = foo \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertNotNull(t);
    }
}
