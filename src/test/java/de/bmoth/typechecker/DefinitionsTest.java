package de.bmoth.typechecker;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefinitionsTest {

    @Test
    public void testExpressionDefinitionsWithoutParameters() {
        String machine = "MACHINE test \n";
        machine += "CONSTANTS x \n";
        machine += "PROPERTIES foo = x \n";
        machine += "DEFINITIONS foo == 1 \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals("INTEGER", t.getConstants().get("x").toString());
    }

    @Test
    public void testExpressionDefinitionsWithParameters() {
        String machine = "MACHINE test \n";
        machine += "CONSTANTS x,y \n";
        machine += "PROPERTIES foo(x) = y \n";
        machine += "DEFINITIONS foo(a) == a + 1 \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals("INTEGER", t.getConstants().get("x").toString());
        assertEquals("INTEGER", t.getConstants().get("y").toString());
    }

    @Test
    public void testPredicateDefinitionsWithoutParameters() {
        String machine = "MACHINE test \n";
        machine += "CONSTANTS x \n";
        machine += "PROPERTIES foo \n";
        machine += "DEFINITIONS foo == 1 = x \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals("INTEGER", t.getConstants().get("x").toString());
    }

    @Test
    public void testPredicateDefinitionsWithParameters() {
        String machine = "MACHINE test \n";
        machine += "CONSTANTS x \n";
        machine += "PROPERTIES foo(1) \n";
        machine += "DEFINITIONS foo(a) == a = x \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals("INTEGER", t.getConstants().get("x").toString());
    }

    @Test
    public void testAmbigiousDefinition() {
        String machine = "MACHINE test \n";
        machine += "CONSTANTS x \n";
        machine += "PROPERTIES foo = x \n";
        machine += "DEFINITIONS foo == bar ; bar == 2  \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals("INTEGER", t.getConstants().get("x").toString());
    }

    @Ignore
    @Test
    public void testSubstitutionDefinitionsWithoutParameters() {
        String machine = "MACHINE test \n";
        machine += "DEFINITIONS foo == skip \n";
        machine += "OPERATIONS op1 = foo \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
    }
}
