package ca.ulaval.glo4003.repul.commons.api;

import java.net.URI;
import java.net.URISyntaxException;

import ca.ulaval.glo4003.repul.commons.api.exception.InvalidURIException;

public class UriFactory {
    public URI createURI(String location) {
        try {
            return new URI(location);
        } catch (URISyntaxException e) {
            throw new InvalidURIException();
        }
    }
}
