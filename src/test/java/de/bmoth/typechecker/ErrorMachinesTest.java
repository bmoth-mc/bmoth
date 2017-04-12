package de.bmoth.typechecker;

import org.junit.Test;

import de.bmoth.exceptions.TypeErrorException;

public class ErrorMachinesTest {

	@Test(expected = TypeErrorException.class)
	public void testNaturalException() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k1,k2 \n" + "PROPERTIES k1 = 1 \n" + "END";
		new TestTypechecker(machine);
	}

}
