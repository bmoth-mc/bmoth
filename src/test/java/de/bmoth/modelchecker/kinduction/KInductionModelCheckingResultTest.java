package de.bmoth.modelchecker.kinduction;

import de.bmoth.modelchecker.kind.KInductionModelCheckingResult;
import org.junit.Test;

import static de.bmoth.modelchecker.kind.KInductionModelCheckingResult.Type.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class KInductionModelCheckingResultTest {
    @Test
    public void testType() {
        assertArrayEquals(new KInductionModelCheckingResult.Type[]{COUNTER_EXAMPLE_FOUND,
                EXCEEDED_MAX_STEPS, VERFIED},
            KInductionModelCheckingResult.Type.values());

        assertEquals(COUNTER_EXAMPLE_FOUND, KInductionModelCheckingResult.Type.valueOf("COUNTER_EXAMPLE_FOUND"));
        assertEquals(EXCEEDED_MAX_STEPS, KInductionModelCheckingResult.Type.valueOf("EXCEEDED_MAX_STEPS"));
    }
}
