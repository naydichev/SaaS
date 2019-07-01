package dev.naydichev.slab.saas.exceptions;

public class NotFoundException extends Throwable {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, int season, int episode) {
        this(String.format("%s; [season=%d, episode=%d]", message, season, episode));
    }

    public NotFoundException(String message, int season) {
        this(String.format("%s; [season=%d]", message, season));
    }
}
