package de.bmoth.app;

import org.fxmisc.richtext.model.StyleSpans;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class HighlighterTest {
    @Test
    public void simpleHighlighterTest() {
        String machine = "MACHINE SimpleMachine\n";
        machine += "VARIABLES x\n";
        machine += "INVARIANT x : NATURAL\n";
        machine += "INITIALISATION x := 0\n";
        machine += "END";
        StyleSpans<Collection<String>> styleSpans = Highlighter.computeHighlighting(machine);

        assertEquals(9, styleSpans.getSpanCount());
        assertEquals(7, styleSpans.getStyleSpan(0).getLength());
        assertEquals("[start]", styleSpans.getStyleSpan(0).getStyle().toString());
        assertEquals(15, styleSpans.getStyleSpan(1).getLength());
        assertEquals("[]", styleSpans.getStyleSpan(1).getStyle().toString());
        assertEquals(9, styleSpans.getStyleSpan(2).getLength());
        assertEquals("[keyword]", styleSpans.getStyleSpan(2).getStyle().toString());
        assertEquals(3, styleSpans.getStyleSpan(3).getLength());
        assertEquals("[]", styleSpans.getStyleSpan(3).getStyle().toString());
        assertEquals(9, styleSpans.getStyleSpan(4).getLength());
        assertEquals("[keyword]", styleSpans.getStyleSpan(4).getStyle().toString());
        assertEquals(13, styleSpans.getStyleSpan(5).getLength());
        assertEquals("[]", styleSpans.getStyleSpan(5).getStyle().toString());
        assertEquals(14, styleSpans.getStyleSpan(6).getLength());
        assertEquals("[keyword]", styleSpans.getStyleSpan(6).getStyle().toString());
        assertEquals(8, styleSpans.getStyleSpan(7).getLength());
        assertEquals("[]", styleSpans.getStyleSpan(7).getStyle().toString());
        assertEquals(3, styleSpans.getStyleSpan(8).getLength());
        assertEquals("[keyword2]", styleSpans.getStyleSpan(8).getStyle().toString());
    }
}
