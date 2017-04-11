package de.bmoth.backend.translator;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;
import org.sosy_lab.common.ShutdownManager;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.Model;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.ProverEnvironment;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;
import org.sosy_lab.java_smt.api.SolverException;

public class SMTSolverConnectionTest {

	@Test
	public void testSimpleCallToSMTINTERPOL()
			throws InvalidConfigurationException, SolverException, InterruptedException {
		Configuration config = Configuration.defaultConfiguration();
		LogManager logger = BasicLogManager.create(config);
		ShutdownManager shutdown = ShutdownManager.create();

		SolverContext context = SolverContextFactory.createSolverContext(config, logger, shutdown.getNotifier(),
				Solvers.SMTINTERPOL);
		FormulaManager fmgr = context.getFormulaManager();

		BooleanFormulaManager bmgr = fmgr.getBooleanFormulaManager();
		IntegerFormulaManager imgr = fmgr.getIntegerFormulaManager();

		IntegerFormula a = imgr.makeVariable("a"), b = imgr.makeVariable("b");
		BooleanFormula constraint = bmgr.and(imgr.equal(a, b), imgr.equal(a, imgr.makeNumber(5)));

		try (ProverEnvironment prover = context.newProverEnvironment(ProverOptions.GENERATE_MODELS)) {
			prover.addConstraint(constraint);
			boolean isUnsat = prover.isUnsat();
			if (!isUnsat) {
				Model model = prover.getModel();
				assertEquals(BigInteger.valueOf(5), model.evaluate(b));
			}
		}
	}

	@Test
	public void testSimpleCallToZ3() throws InvalidConfigurationException, SolverException, InterruptedException {
		Configuration config = Configuration.defaultConfiguration();
		LogManager logger = BasicLogManager.create(config);
		ShutdownManager shutdown = ShutdownManager.create();

		SolverContext context = SolverContextFactory.createSolverContext(config, logger, shutdown.getNotifier(),
				Solvers.Z3);
		FormulaManager fmgr = context.getFormulaManager();

		BooleanFormulaManager bmgr = fmgr.getBooleanFormulaManager();
		IntegerFormulaManager imgr = fmgr.getIntegerFormulaManager();

		IntegerFormula a = imgr.makeVariable("a"), b = imgr.makeVariable("b");
		BooleanFormula constraint = bmgr.and(imgr.equal(a, b), imgr.equal(a, imgr.makeNumber(5)));

		try (ProverEnvironment prover = context.newProverEnvironment(ProverOptions.GENERATE_MODELS)) {
			prover.addConstraint(constraint);
			boolean isUnsat = prover.isUnsat();
			if (!isUnsat) {
				Model model = prover.getModel();
				assertEquals(BigInteger.valueOf(5), model.evaluate(b));
			}
		}
	}

}
