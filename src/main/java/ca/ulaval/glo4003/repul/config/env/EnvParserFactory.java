package ca.ulaval.glo4003.repul.config.env;

public class EnvParserFactory {
    public static EnvParser getEnvParser(String fileName) {
        EnvParser envParser = EnvParser.getInstance();
        EnvParser.setFilename(fileName);
        return envParser;
    }
}
