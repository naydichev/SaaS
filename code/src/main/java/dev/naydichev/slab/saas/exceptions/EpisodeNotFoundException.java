package dev.naydichev.slab.saas.exceptions;

public class EpisodeNotFoundException extends NotFoundException{
    public EpisodeNotFoundException(String message) {
        super(message);
    }

    public EpisodeNotFoundException(String message, int season, int episode) {
        super(message, season, episode);
    }
}
