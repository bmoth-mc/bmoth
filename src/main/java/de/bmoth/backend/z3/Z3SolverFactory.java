package de.bmoth.backend.z3;

import com.microsoft.z3.Context;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import de.bmoth.app.PersonalPreferences;

public interface Z3SolverFactory {
    static Solver getZ3Solver(Context ctx) {
        Solver solver = ctx.mkSolver();

        Params params = ctx.mkParams();
        params.add("timeout", PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.Z3_TIMEOUT));

        solver.setParameters(params);
        return solver;
    }
}
