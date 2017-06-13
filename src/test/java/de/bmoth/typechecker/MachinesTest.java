package de.bmoth.typechecker;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.TypeErrorException;
import de.bmoth.parser.ast.nodes.AnySubstitutionNode;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MachinesTest {

    private static final String POW_INTEGER = "POW(INTEGER)";
    private static final String INTEGER = "INTEGER";

    @Test
    public void testInteger() {
        String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES k = INTEGER \n" + "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(POW_INTEGER, t.getConstants().get("k"));
    }

    @Test(expected = TypeErrorException.class)
    public void testIntegerException() {
        String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES 1 = INTEGER \n" + "END";
        new TestTypechecker(machine);
    }

    @Test
    public void testNatural() {
        String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES k = NATURAL \n" + "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(POW_INTEGER, t.getConstants().get("k"));
    }

    @Test(expected = TypeErrorException.class)
    public void testNaturalException() {
        String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES 1 = NATURAL \n" + "END";
        new TestTypechecker(machine);
    }

    @Test
    public void testNatural1() {
        String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES k = NATURAL1 \n" + "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(POW_INTEGER, t.getConstants().get("k"));
    }

    @Test(expected = TypeErrorException.class)
    public void testNatural1Exception() {
        String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES 1 = NATURAL1 \n" + "END";
        new TestTypechecker(machine);
    }

    @Test
    public void testInterval() {
        String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES k = 1..3 \n" + "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(POW_INTEGER, t.getConstants().get("k"));
    }

    @Test(expected = TypeErrorException.class)
    public void testIntervalException() {
        String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES 1 = 1..3 \n" + "END";
        new TestTypechecker(machine);
    }

    @Test(expected = TypeErrorException.class)
    public void testIntervalException2() {
        String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES k = TRUE..3 \n" + "END";
        new TestTypechecker(machine);
    }

    @Test
    public void testGreaterThan() {
        String machine = "MACHINE test\n" + "CONSTANTS k,k2 \n" + "PROPERTIES k > k2 \n" + "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k"));
        assertEquals(INTEGER, t.getConstants().get("k2"));
    }

    @Test(expected = TypeErrorException.class)
    public void testGreaterThanException() {
        String machine = "MACHINE test\n" + "PROPERTIES TRUE > 1 \n" + "END";
        new TestTypechecker(machine);
    }

    @Test
    public void testLessThan() {
        String machine = "MACHINE test\n" + "CONSTANTS k,k2 \n" + "PROPERTIES k < k2 \n" + "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k"));
        assertEquals(INTEGER, t.getConstants().get("k2"));
    }

    @Test(expected = TypeErrorException.class)
    public void testLessThanException() {
        String machine = "MACHINE test\n" + "PROPERTIES TRUE < 1 \n" + "END";
        new TestTypechecker(machine);
    }

    @Test
    public void testGreaterEquals() {
        String machine = "MACHINE test\n" + "CONSTANTS k,k2 \n" + "PROPERTIES k >= k2 \n" + "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k"));
        assertEquals(INTEGER, t.getConstants().get("k2"));
    }

    @Test(expected = TypeErrorException.class)
    public void testGreaterEqualsException() {
        String machine = "MACHINE test\n" + "PROPERTIES TRUE >= 1 \n" + "END";
        new TestTypechecker(machine);
    }

    @Test
    public void testLessEquals() {
        String machine = "MACHINE test\n" + "CONSTANTS k,k2 \n" + "PROPERTIES k <= k2 \n" + "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k"));
        assertEquals(INTEGER, t.getConstants().get("k2"));
    }

    @Test(expected = TypeErrorException.class)
    public void testLessEqualsException() {
        String machine = "MACHINE test\n" + "PROPERTIES TRUE <= 1 \n" + "END";
        new TestTypechecker(machine);
    }

    @Test
    public void testAdd() {
        String machine = "MACHINE test\n" + "CONSTANTS k,k2,k3 \n" + "PROPERTIES k = k2 + k3 \n" + "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k"));
        assertEquals(INTEGER, t.getConstants().get("k2"));
        assertEquals(INTEGER, t.getConstants().get("k3"));
    }

    @Test(expected = TypeErrorException.class)
    public void testAddException() {
        String machine = "MACHINE test\n" + "CONSTANTS k\n" + "PROPERTIES TRUE = 1 + 1 \n" + "END";
        new TestTypechecker(machine);
    }

    @Test
    public void testSub() {
        String machine = "MACHINE test\n" + "CONSTANTS k,k2,k3 \n" + "PROPERTIES k = k2 + k3 \n" + "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k"));
        assertEquals(INTEGER, t.getConstants().get("k2"));
        assertEquals(INTEGER, t.getConstants().get("k3"));
    }

    @Test(expected = TypeErrorException.class)
    public void testSubException() {
        String machine = "MACHINE test\n" + "CONSTANTS k\n" + "PROPERTIES TRUE = 1 - 1 \n" + "END";
        new TestTypechecker(machine);
    }

    @Test
    public void testDivision() {
        String machine = "MACHINE test\n" + "CONSTANTS k,k2,k3 \n" + "PROPERTIES k = k2 / k3 \n" + "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k"));
        assertEquals(INTEGER, t.getConstants().get("k2"));
        assertEquals(INTEGER, t.getConstants().get("k3"));
    }

    @Test(expected = TypeErrorException.class)
    public void testDivisionException() {
        String machine = "MACHINE test\n" + "CONSTANTS k\n" + "PROPERTIES TRUE = 1 / 1 \n" + "END";
        new TestTypechecker(machine);
    }

    @Test
    public void testPowerOf() {
        String machine = "MACHINE test\n" + "CONSTANTS k,k2,k3 \n" + "PROPERTIES k = k2 ** k3 \n" + "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k"));
        assertEquals(INTEGER, t.getConstants().get("k2"));
        assertEquals(INTEGER, t.getConstants().get("k3"));
    }

    @Test(expected = TypeErrorException.class)
    public void testPowerOfException() {
        String machine = "MACHINE test\n" + "CONSTANTS k\n" + "PROPERTIES TRUE = 1 ** 1 \n" + "END";
        new TestTypechecker(machine);
    }

    @Test
    public void testModulo() {
        String machine = "MACHINE test\n" + "CONSTANTS k,k2,k3 \n" + "PROPERTIES k = k2 mod k3 \n" + "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getConstants().get("k"));
        assertEquals(INTEGER, t.getConstants().get("k2"));
        assertEquals(INTEGER, t.getConstants().get("k3"));
    }

    @Test(expected = TypeErrorException.class)
    public void testModuloException() {
        String machine = "MACHINE test\n" + "CONSTANTS k\n" + "PROPERTIES TRUE = 1 mod 1 \n" + "END";
        new TestTypechecker(machine);
    }

    @Test
    public void testSubstitution() {
        String machine = "MACHINE test \n";
        machine += "VARIABLES x,y \n";
        machine += "INVARIANT x=1 & y : BOOL \n";
        machine += "INITIALISATION x,y:= 1,TRUE \n";
        machine += "OPERATIONS foo = SELECT x < 2 THEN x := 2 END \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getVariables().get("x"));
        assertEquals("BOOL", t.getVariables().get("y"));
    }

    @Test
    public void testSetComprehension() {
        String machine = "MACHINE test\n";
        machine += "CONSTANTS k\n";
        machine += "PROPERTIES k = {x | x : INTEGER } \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(POW_INTEGER, t.getConstants().get("k"));
    }

    @Test
    public void testAnySubstitution() {
        String machine = "MACHINE test \n";
        machine += "VARIABLES x,y \n";
        machine += "INVARIANT x=1 & y : BOOL \n";
        machine += "INITIALISATION x,y:= 1,TRUE \n";
        machine += "OPERATIONS foo = ANY p WHERE 1=1 THEN x := p END \n";
        machine += "END";
        MachineNode machineAsSemanticAst = Parser.getMachineAsSemanticAst(machine);
        AnySubstitutionNode any = (AnySubstitutionNode) machineAsSemanticAst.getOperations().get(0).getSubstitution();
        DeclarationNode p = any.getParameters().get(0);
        assertEquals("p", p.getName());
        assertEquals(INTEGER, p.getType().toString());
    }

    @Test
    public void testPreSubstitution() {
        String machine = "MACHINE test \n";
        machine += "VARIABLES x,y \n";
        machine += "INVARIANT x=1 & y : BOOL \n";
        machine += "INITIALISATION x,y:= 1,TRUE \n";
        machine += "OPERATIONS foo = PRE x < 2 THEN x := 2 END \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getVariables().get("x"));
        assertEquals("BOOL", t.getVariables().get("y"));
    }

    @Test
    public void testBecomesSuchThatSubstitution() {
        String machine = "MACHINE test \n";
        machine += "VARIABLES x,y \n";
        machine += "INVARIANT x=1 & y : BOOL \n";
        machine += "INITIALISATION x,y:(x = 1 & y = TRUE) \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getVariables().get("x"));
        assertEquals("BOOL", t.getVariables().get("y"));
    }

    @Test
    public void testBecomesElementOfSubstitution() {
        String machine = "MACHINE test \n";
        machine += "VARIABLES a,b,c,d \n";
        machine += "INVARIANT a : {b,c,1} & d : BOOL \n";
        machine += "INITIALISATION a::{1} || b,c,d::INTEGER*{2}*BOOL \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getVariables().get("a"));
        assertEquals(INTEGER, t.getVariables().get("b"));
        assertEquals(INTEGER, t.getVariables().get("c"));
        assertEquals("BOOL", t.getVariables().get("d"));
    }

    @Test
    public void testIfSubstitution() {
        String machine = "MACHINE test \n";
        machine += "VARIABLES a \n";
        machine += "INVARIANT a : INTEGER \n";
        machine += "INITIALISATION IF 1=1 THEN a := 1 ELSE a:= 2 END \n";
        machine += "END";
        TestTypechecker t = new TestTypechecker(machine);
        assertEquals(INTEGER, t.getVariables().get("a"));
    }

    @Test
    public void testSkipSubstitution() {
        String machine = "MACHINE test \n";
        machine += "INITIALISATION skip \n";
        machine += "END";
        new TestTypechecker(machine);
    }

}
