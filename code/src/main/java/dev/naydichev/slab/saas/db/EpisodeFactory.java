package dev.naydichev.slab.saas.db;

import dev.naydichev.slab.saas.exceptions.EpisodeNotFoundException;
import dev.naydichev.slab.saas.exceptions.NotFoundException;
import dev.naydichev.slab.saas.exceptions.SeasonNotFoundException;
import dev.naydichev.slab.saas.exceptions.UtteranceNotFoundException;
import dev.naydichev.slab.saas.exceptions.WriterNotFoundException;
import dev.naydichev.slab.saas.models.Episode;
import dev.naydichev.slab.saas.models.EpisodeWithUtterances;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EpisodeFactory extends AbstractFactory {
    private static final String GET_EPISODES =
        "SELECT season_id, episode_id, title, date, director FROM episodes WHERE season_id = ?";
    private static final String GET_EPISODE =
        GET_EPISODES + " AND episode_id = ?";
    private static final String GET_EPISODE_WRITERS =
        "SELECT w.name FROM writers w INNER JOIN episode_writers ew ON " +
            "ew.writer_id = w.id WHERE ew.season_id = ? AND ew.episode_id = ?";
    private final UtteranceFactory utteranceFactory;
    public EpisodeFactory(Connection connection) {
        super(connection);

        utteranceFactory = new UtteranceFactory(connection);
    }

    public Episode getEpisode(int season, int episode) throws NotFoundException {
        PreparedStatement statement = getPreparedStatement(GET_EPISODE);

        try {
            statement.setInt(1, season);
            statement.setInt(2, episode);

            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                throw new EpisodeNotFoundException("Could not find episode", season, episode);
            }

            return extractEpisodeWithUtterances(rs);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not fetch episode", e);
        }
    }

    public List<Episode> getEpisodesForSeason(int season) throws NotFoundException {
        PreparedStatement statement = getPreparedStatement(GET_EPISODES);
        List<Episode> episodes = new ArrayList<Episode>();

        try {
            statement.setInt(1, season);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                episodes.add(extractEpisode(rs));
            }

            if (episodes.size() == 0) {
                throw new SeasonNotFoundException("Could not find season", season);
            }

            return episodes;
        } catch (SQLException e) {
            throw new IllegalStateException("Could not fetch episodes", e);
        }
    }

    List<String> getWriters(int season, int episode) throws SQLException, WriterNotFoundException {
        PreparedStatement statement = getPreparedStatement(GET_EPISODE_WRITERS);
        statement.setInt(1, season);
        statement.setInt(2, episode);

        ResultSet rs = statement.executeQuery();
        List<String> writers = new ArrayList<String>();
        while (rs.next()) {
            writers.add(rs.getString("name"));
        }

        if (writers.size() == 0) {
            throw new WriterNotFoundException("Could not find writers", season, episode);
        }
        return writers;
    }

    Episode extractEpisode(ResultSet rs) throws SQLException, WriterNotFoundException {
        int season = rs.getInt("season_id");
        int episode = rs.getInt("episode_id");

        return Episode.builder()
            .id(episode)
            .season(season)
            .title(rs.getString("title"))
            .director(rs.getString("director"))
            .writers(getWriters(season, episode))
            .build();
    }

    EpisodeWithUtterances extractEpisodeWithUtterances(ResultSet rs)
        throws SQLException, NotFoundException {
        Episode episode = extractEpisode(rs);

        return EpisodeWithUtterances.builder()
            .id(episode.getId())
            .season(episode.getSeason())
            .title(episode.getTitle())
            .director(episode.getDirector())
            .writers(episode.getWriters())
            .utterances(utteranceFactory.getEpisodeUtterances(
                episode.getSeason(), episode.getId()
            ))
            .build();
    }

}
