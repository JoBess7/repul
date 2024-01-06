package ca.ulaval.glo4003.repul.config.env;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import ca.ulaval.glo4003.repul.config.exception.EnvFileNotFoundException;

public class EnvParser {
    private static String fileName = ".envExample";
    private static EnvParser envParser;

    private EnvParser() {
    }

    public static void setFilename(String fileNameToUse) {
        fileName = fileNameToUse;
    }

    public static EnvParser getInstance() {
        if (envParser == null) {
            envParser = new EnvParser();
        }
        return envParser;
    }
    public String readVariable(String variable) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(variable + "=")) {
                    return line.substring(variable.length() + 1);
                }
            }
        } catch (IOException e) {
            throw new EnvFileNotFoundException();
        }
        return "";
    }
}
