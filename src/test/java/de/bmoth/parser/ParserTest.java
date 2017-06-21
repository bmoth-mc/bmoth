package de.bmoth.parser;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class ParserTest {

    private static final String UNIX_FILE = "unixFile";
    private static final String WIN_FILE = "winFile";


    @Test
    public void testReadFile() throws IOException {
        final File emptyFile = prepareFileFromArray(UNIX_FILE, new int[]{});
        final File unixFile = prepareFileFromArray(UNIX_FILE, new int[]{65279, 'T', 'E', 'S', 'T'});
        final File unixFileEmpty = prepareFileFromArray(UNIX_FILE, new int[]{65279});
        final File winFile = prepareFileFromArray(WIN_FILE, new int[]{239, 187, 191, 'T', 'E', 'S', 'T'});
        final File winFileEmpty = prepareFileFromArray(WIN_FILE, new int[]{239, 187, 191});

        assertEquals("", Parser.readFile(emptyFile));
        assertEquals("TEST", Parser.readFile(unixFile));
        assertEquals("", Parser.readFile(unixFileEmpty));
        assertEquals("TEST", Parser.readFile(winFile));
        assertEquals("", Parser.readFile(winFileEmpty));
    }

    private static File prepareFileFromArray(String name, int[] data) throws IOException {
        final File result = File.createTempFile(name, ".tmp");
        result.deleteOnExit();

        try (Writer out = new OutputStreamWriter(new FileOutputStream(result), "UTF-8")) {
            for (int i : data) {
                out.write(i);
            }
        }
        return result;
    }
}
