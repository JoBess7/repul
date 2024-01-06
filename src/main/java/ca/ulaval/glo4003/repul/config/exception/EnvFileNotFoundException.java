package ca.ulaval.glo4003.repul.config.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.domain.exception.CommonException;

public class EnvFileNotFoundException extends CommonException {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnvFileNotFoundException.class);

    public EnvFileNotFoundException() {
        super("");
        LOGGER.error(".env file was not found");
    }
}
