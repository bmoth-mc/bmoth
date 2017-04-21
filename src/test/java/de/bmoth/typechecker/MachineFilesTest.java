package de.bmoth.typechecker;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.MachineNode;

public class MachineFilesTest {

	@Test
	public void testSimpleMachine() throws Exception {
		String dir = "src/test/resources/machines/";
		MachineNode machineNode = Parser.getMachineFileAsSemanticAst(dir + "SimpleMachine.mch");
		//TODO complete
	}
}
