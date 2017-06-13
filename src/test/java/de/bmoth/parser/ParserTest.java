package de.bmoth.parser;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class ParserTest {
    @Test
    public void testReadFile() throws IOException {
        final File emptyFile = prepareFileFromArray("unixFile", new int[]{});
        final File unixFile = prepareFileFromArray("unixFile", new int[]{65279, 'T', 'E', 'S', 'T'});
        final File unixFileEmpty = prepareFileFromArray("unixFile", new int[]{65279});
        final File winFile = prepareFileFromArray("winFile", new int[]{239, 187, 191, 'T', 'E', 'S', 'T'});
        final File winFileEmpty = prepareFileFromArray("winFile", new int[]{239, 187, 191});

        assertEquals("", Parser.readFile(emptyFile));
        assertEquals("TEST", Parser.readFile(unixFile));
        assertEquals("", Parser.readFile(unixFileEmpty));
        assertEquals("TEST", Parser.readFile(winFile));
        assertEquals("", Parser.readFile(winFileEmpty));
    }

    private static File prepareFileFromArray(String name, int[] data) throws IOException {
        final File result = File.createTempFile(name, ".tmp");
        result.deleteOnExit();

        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(result), "UTF-8"))) {
            for (int i : data) {
                out.write(i);
            }
            out.close();
        }
        return result;
    }
}
