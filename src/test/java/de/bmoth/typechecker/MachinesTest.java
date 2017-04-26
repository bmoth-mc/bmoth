package de.bmoth.typechecker;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.bmoth.exceptions.TypeErrorException;

public class MachinesTest {

	@Test
	public void testInteger() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES k = INTEGER \n" + "END";
		TestTypechecker t = new TestTypechecker(machine);
		assertEquals("POW(INTEGER)", t.constants.get("k").toString());
	}

	@Test(expected = TypeErrorException.class)
	public void testIntegerException() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES 1 = INTEGER \n" + "END";
		new TestTypechecker(machine);
	}

	@Test
	public void testNatural() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES k = NATURAL \n" + "END";
		TestTypechecker t = new TestTypechecker(machine);
		assertEquals("POW(INTEGER)", t.constants.get("k").toString());
	}

	@Test(expected = TypeErrorException.class)
	public void testNaturalException() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES 1 = NATURAL \n" + "END";
		new TestTypechecker(machine);
	}

	@Test
	public void testNatural1() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES k = NATURAL1 \n" + "END";
		TestTypechecker t = new TestTypechecker(machine);
		assertEquals("POW(INTEGER)", t.constants.get("k").toString());
	}

	@Test(expected = TypeErrorException.class)
	public void testNatural1Exception() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES 1 = NATURAL1 \n" + "END";
		new TestTypechecker(machine);
	}

	@Test
	public void testInterval() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES k = 1..3 \n" + "END";
		TestTypechecker t = new TestTypechecker(machine);
		assertEquals("POW(INTEGER)", t.constants.get("k").toString());
	}

	@Test(expected = TypeErrorException.class)
	public void testIntervalException() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES 1 = 1..3 \n" + "END";
		new TestTypechecker(machine);
	}

	@Test(expected = TypeErrorException.class)
	public void testIntervalException2() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES k = TRUE..3 \n" + "END";
		new TestTypechecker(machine);
	}

	@Test
	public void testGreaterThan() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k,k2 \n" + "PROPERTIES k > k2 \n" + "END";
		TestTypechecker t = new TestTypechecker(machine);
		assertEquals("INTEGER", t.constants.get("k").toString());
		assertEquals("INTEGER", t.constants.get("k2").toString());
	}

	@Test(expected = TypeErrorException.class)
	public void testGreaterThanException() throws Exception {
		String machine = "MACHINE test\n" + "PROPERTIES TRUE > 1 \n" + "END";
		new TestTypechecker(machine);
	}

	@Test
	public void testLessThan() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k,k2 \n" + "PROPERTIES k < k2 \n" + "END";
		TestTypechecker t = new TestTypechecker(machine);
		assertEquals("INTEGER", t.constants.get("k").toString());
		assertEquals("INTEGER", t.constants.get("k2").toString());
	}

	@Test(expected = TypeErrorException.class)
	public void testLessThanException() throws Exception {
		String machine = "MACHINE test\n" + "PROPERTIES TRUE < 1 \n" + "END";
		new TestTypechecker(machine);
	}

	@Test
	public void testGreaterEquals() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k,k2 \n" + "PROPERTIES k >= k2 \n" + "END";
		TestTypechecker t = new TestTypechecker(machine);
		assertEquals("INTEGER", t.constants.get("k").toString());
		assertEquals("INTEGER", t.constants.get("k2").toString());
	}

	@Test(expected = TypeErrorException.class)
	public void testGreaterEqualsException() throws Exception {
		String machine = "MACHINE test\n" + "PROPERTIES TRUE >= 1 \n" + "END";
		new TestTypechecker(machine);
	}

	@Test
	public void testLessEquals() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k,k2 \n" + "PROPERTIES k <= k2 \n" + "END";
		TestTypechecker t = new TestTypechecker(machine);
		assertEquals("INTEGER", t.constants.get("k").toString());
		assertEquals("INTEGER", t.constants.get("k2").toString());
	}

	@Test(expected = TypeErrorException.class)
	public void testLessEqualsException() throws Exception {
		String machine = "MACHINE test\n" + "PROPERTIES TRUE <= 1 \n" + "END";
		new TestTypechecker(machine);
	}

	@Test
	public void testAdd() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k,k2,k3 \n" + "PROPERTIES k = k2 + k3 \n" + "END";
		TestTypechecker t = new TestTypechecker(machine);
		assertEquals("INTEGER", t.constants.get("k").toString());
		assertEquals("INTEGER", t.constants.get("k2").toString());
		assertEquals("INTEGER", t.constants.get("k3").toString());
	}

	@Test(expected = TypeErrorException.class)
	public void testAddException() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k\n" + "PROPERTIES TRUE = 1 + 1 \n" + "END";
		new TestTypechecker(machine);
	}

	@Test
	public void testSub() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k,k2,k3 \n" + "PROPERTIES k = k2 - k3 \n" + "END";
		TestTypechecker t = new TestTypechecker(machine);
		assertEquals("INTEGER", t.constants.get("k").toString());
		assertEquals("INTEGER", t.constants.get("k2").toString());
		assertEquals("INTEGER", t.constants.get("k3").toString());
	}

	@Test(expected = TypeErrorException.class)
	public void testSubException() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k\n" + "PROPERTIES TRUE = 1 - 1 \n" + "END";
		new TestTypechecker(machine);
	}

	@Test
	public void testDivision() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k,k2,k3 \n" + "PROPERTIES k = k2 / k3 \n" + "END";
		TestTypechecker t = new TestTypechecker(machine);
		assertEquals("INTEGER", t.constants.get("k").toString());
		assertEquals("INTEGER", t.constants.get("k2").toString());
		assertEquals("INTEGER", t.constants.get("k3").toString());
	}

	@Test(expected = TypeErrorException.class)
	public void testDivisionException() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k\n" + "PROPERTIES TRUE = 1 / 1 \n" + "END";
		new TestTypechecker(machine);
	}

	@Test
	public void testPowerOf() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k,k2,k3 \n" + "PROPERTIES k = k2 ** k3 \n" + "END";
		TestTypechecker t = new TestTypechecker(machine);
		assertEquals("INTEGER", t.constants.get("k").toString());
		assertEquals("INTEGER", t.constants.get("k2").toString());
		assertEquals("INTEGER", t.constants.get("k3").toString());
	}

	@Test(expected = TypeErrorException.class)
	public void testPowerOfException() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k\n" + "PROPERTIES TRUE = 1 ** 1 \n" + "END";
		new TestTypechecker(machine);
	}

	@Test
	public void testModulo() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k,k2,k3 \n" + "PROPERTIES k = k2 mod k3 \n" + "END";
		TestTypechecker t = new TestTypechecker(machine);
		assertEquals("INTEGER", t.constants.get("k").toString());
		assertEquals("INTEGER", t.constants.get("k2").toString());
		assertEquals("INTEGER", t.constants.get("k3").toString());
	}

	
	@Test(expected = TypeErrorException.class)
	public void testModuloException() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k\n" + "PROPERTIES TRUE = 1 mod 1 \n" + "END";
		new TestTypechecker(machine);
	}

	@Test
	public void testSubstitution() throws Exception {
		String machine = "MACHINE test \n";
		machine += "VARIABLES x,y \n";
		machine += "INVARIANT x=1 & y : BOOL \n";
		machine += "INITIALISATION x,y:= 1,TRUE \n";
		machine += "OPERATIONS foo = SELECT x < 2 THEN x := 2 END \n";
		machine += "END";
		TestTypechecker t = new TestTypechecker(machine);
		assertEquals("INTEGER", t.variables.get("x").toString());
		assertEquals("BOOL", t.variables.get("y").toString());
	}
	
	@Test
	public void testSetComprehension() throws Exception {
		String machine = "MACHINE test\n";
		machine += "CONSTANTS k\n";
		machine += "PROPERTIES k = {x | x : INTEGER } \n";
		machine += "END";
		TestTypechecker t = new TestTypechecker(machine);
		assertEquals("POW(INTEGER)", t.constants.get("k").toString());
	}

}
