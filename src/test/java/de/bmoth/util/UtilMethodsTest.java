package de.bmoth.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class UtilMethodsTest {

	@Test
	public void testSort() throws Exception {
		Map<String, Set<String>> dependencies = new HashMap<>();
		dependencies.put("a", new HashSet<>());
		dependencies.put("b", new HashSet<>(Arrays.asList("a")));
		dependencies.put("c", new HashSet<>(Arrays.asList("a", "d")));
		dependencies.put("d", new HashSet<>(Arrays.asList("b")));
		List<String> sorted = Utils.sortByTopologicalOrder(dependencies);
		assertEquals(Arrays.asList("a", "b", "d", "c"), sorted);
	}

}
