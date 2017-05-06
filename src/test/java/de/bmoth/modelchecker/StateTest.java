package de.bmoth.modelchecker;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Solver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class StateTest {

    private Context ctx;
    private Solver s;

    @Before
    public void setup() {
        ctx = new Context();
        s = ctx.mkSolver();
    }

    @After
    public void cleanup() {
        ctx.close();
    }

    @Test
    public void testToString() throws Exception {
        HashMap<String, Expr> map = new HashMap<>();

        map.put("z", ctx.mkInt(13));
        map.put("x", ctx.mkInt(11));
        map.put("y", ctx.mkInt(12));
        map.put("a", ctx.mkInt(-200));

        State state = new State(null, map);
        assertEquals("{a=-200, x=11, y=12, z=13}", state.toString());
    }

    @Test
    public void testStateEquals() throws Exception {
        HashMap<String, Expr> map1 = new HashMap<>();
        HashMap<String, Expr> map2 = new HashMap<>();

        map1.put("x", ctx.mkInt(11));
        map2.put("x", ctx.mkInt(11));

        State state1 = new State(null, map1);
        State state2 = new State(null, map2);
        assertEquals(state1, state2);
        assertEquals(state1.hashCode(), state2.hashCode());
    }

    @Test
    public void testStateEquals2() throws Exception {
        HashMap<String, Expr> map1 = new HashMap<>();
        HashMap<String, Expr> map2 = new HashMap<>();

        map1.put("x", ctx.mkInt(11));
        map1.put("y", ctx.mkInt(12));
        map2.put("x", ctx.mkInt(11));

        State state1 = new State(null, map1);
        State state2 = new State(null, map2);
        assertNotEquals(state1, state2);
        assertNotEquals(state1.hashCode(), state2.hashCode());
    }

    @Test
    public void testStateEquals3() throws Exception {
        HashMap<String, Expr> map = new HashMap<>();

        map.put("x", ctx.mkInt(11));

        State state = new State(null, map);
        assertEquals(state, state);
        assertNotEquals(state, new Object());
    }
}
