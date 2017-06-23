package de.bmoth.modelchecker.bmc;

import org.junit.Test;

import static de.bmoth.modelchecker.bmc.BoundedModelCheckingResult.Type.COUNTER_EXAMPLE_FOUND;
import static de.bmoth.modelchecker.bmc.BoundedModelCheckingResult.Type.EXCEEDED_MAX_STEPS;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BoundedModelCheckerResultTest {
    @Test
    public void testType() {
        assertArrayEquals(new BoundedModelCheckingResult.Type[]{COUNTER_EXAMPLE_FOUND,
                EXCEEDED_MAX_STEPS},
            BoundedModelCheckingResult.Type.values());

        assertEquals(COUNTER_EXAMPLE_FOUND, BoundedModelCheckingResult.Type.valueOf("COUNTER_EXAMPLE_FOUND"));
        assertEquals(EXCEEDED_MAX_STEPS, BoundedModelCheckingResult.Type.valueOf("EXCEEDED_MAX_STEPS"));
    }
}
