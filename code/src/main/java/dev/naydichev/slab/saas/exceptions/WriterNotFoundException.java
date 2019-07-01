package dev.naydichev.slab.saas.exceptions;

public class WriterNotFoundException extends NotFoundException{
    public WriterNotFoundException(String message) {
        super(message);
    }

    public WriterNotFoundException(String message, int season, int episode) {
        super(message, season, episode);
    }
}
