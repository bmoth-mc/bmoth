package de.bmoth.app;

/*
 *Original Code from https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywords.java
 */

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Highlighter {

    String[] START = new String[]{
        "MACHINE"
    };

    String[] KEYWORDS = new String[]{
        "VARIABLES", "INVARIANT", "INITIALISATION", "OPERATIONS", "CONSTANTS", "PROPERTIES"
    };

    String[] KEYWORDS2 = new String[]{
        "END", "SELECT", "THEN", "BEGIN", "WHERE", "ANY"
    };

    String START_PATTERN = "\\b(" + String.join("|", START) + ")\\b";
    String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    String KEYWORD2_PATTERN = "\\b(" + String.join("|", KEYWORDS2) + ")\\b";
    String PAREN_PATTERN = "\\(|\\)";
    String BRACE_PATTERN = "\\{|\\}";
    String BRACKET_PATTERN = "\\[|\\]";
    String SEMICOLON_PATTERN = "\\;";
    String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    Pattern PATTERN = Pattern.compile(
        "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<START>" + START_PATTERN + ")"
            + "|(?<KEYWORD2>" + KEYWORD2_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
            = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass; /* never happens */
            if (matcher.group("START") != null) styleClass = "start";
            else if (matcher.group("KEYWORD") != null) styleClass = "keyword";
            else if (matcher.group("KEYWORD2") != null) styleClass = "keyword2";
            else if (matcher.group("PAREN") != null) styleClass = "paren";
            else if (matcher.group("BRACE") != null) styleClass = "brace";
            else if (matcher.group("BRACKET") != null) styleClass = "bracket";
            else if (matcher.group("SEMICOLON") != null) styleClass = "semicolon";
            else if (matcher.group("STRING") != null) styleClass = "string";
            else if (matcher.group("COMMENT") != null) styleClass = "comment";
            else styleClass = null;
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
