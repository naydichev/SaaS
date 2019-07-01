package dev.naydichev.slab.saas.db;

import dev.naydichev.slab.saas.exceptions.NotFoundException;
import dev.naydichev.slab.saas.exceptions.SeasonNotFoundException;
import dev.naydichev.slab.saas.models.Episode;
import dev.naydichev.slab.saas.models.Season;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SeasonFactory extends AbstractFactory {
    private final EpisodeFactory episodeFactory;
    public SeasonFactory(Connection connection) {
        super(connection);

        episodeFactory = new EpisodeFactory(connection);
    }

    private String GET_SEASONS = "SELECT DISTINCT season_id FROM episodes";

    public Season getSeason(int season) throws NotFoundException {
        List<Episode> episodes = episodeFactory.getEpisodesForSeason(season);

        return Season.builder()
            .id(season)
            .episodes(episodes)
            .build();
    }

    public List<Integer> getAllSeasons() throws SeasonNotFoundException {
        PreparedStatement statement = getPreparedStatement(GET_SEASONS);
        List<Integer> seasons = new ArrayList<Integer>();

        try {
            ResultSet rs = statement.executeQuery();

            while (true) {
                if (!rs.next())
                    break;
                seasons.add(rs.getInt("season_id"));
            }

            if (seasons.size() == 0) {
                throw new SeasonNotFoundException("Could not find seasons");
            }

            return seasons;
        } catch (SQLException e) {
            throw new IllegalStateException("Could not find seasons", e);
        }
    }
}
