package com.github.ianrae.dnalparse.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Writes a list of text lines to a text file. If the file doesn't exist it is
 * created; otherwise it is overwritten. Generally used in junit tests.
 *
 * @author irae
 *
 */
public class TextFileWriter {

    /**
     *
     * @param path
     * @param linesL
     * @return success
     */
    @SuppressWarnings("PMD.AvoidPrintStackTrace")
    public boolean writeFile(final String path, final List<String> linesL) {
        try {
            return doWriteFile(path, linesL);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param path
     * @param linesL
     * @return success
     * @throws Exception
     */
    private boolean doWriteFile(final String path, final List<String> linesL) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {

            for (final String line : linesL) {
                writer.write(line);
                writer.newLine();
            }
        }
        return true;
    }
}