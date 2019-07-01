package dev.naydichev.slab.saas.exceptions;

public class SeasonNotFoundException extends NotFoundException{
    public SeasonNotFoundException(String message) {
        super(message);
    }

    public SeasonNotFoundException(String message, int season) {
        super(message, season);
    }
}
