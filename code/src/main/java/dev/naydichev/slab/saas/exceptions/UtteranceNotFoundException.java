package dev.naydichev.slab.saas.exceptions;

public class UtteranceNotFoundException extends NotFoundException{
    public UtteranceNotFoundException(String message, Integer season, Integer episode) {
        super(message, season, episode);
    }

}
