package de.bmoth;

import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import org.junit.After;
import org.junit.Before;

public class TestUsingZ3 {
    protected Context z3Context;
    protected Solver z3Solver;

    @Before
    public void setup() {
        z3Context = new Context();
        z3Solver = z3Context.mkSolver();
    }

    @After
    public void cleanup() {
        z3Context.close();
    }
}
