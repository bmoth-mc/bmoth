package de.bmoth.backend.typechecker;

import org.junit.Test;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.node.Start;
import de.bmoth.backend.translator.BInputSupportedChecker;
import de.bmoth.backend.translator.UnsupportedInputException;
import de.prob.typechecker.MachineContext;
import de.prob.typechecker.Typechecker;

public class UnsupportedInputDetectionTest {
	@Test(expected = UnsupportedInputException.class)
	public void testSeqIsUnsupported() throws Exception {
		String machine = "MACHINE test\n" + "CONSTANTS k \n" + "PROPERTIES k : seq(NAT) \n" + "END";

		BParser parser = new BParser("Test");
		Start start = parser.parse(machine, false);
		MachineContext c = new MachineContext(null, start);
		c.analyseMachine();
		Typechecker t = new Typechecker(c);

		BInputSupportedChecker checker = new BInputSupportedChecker(t);
		start.apply(checker);

	}
}
