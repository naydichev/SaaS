package dev.naydichev.slab.saas.db;

import dev.naydichev.slab.saas.exceptions.UtteranceNotFoundException;
import dev.naydichev.slab.saas.models.Utterance;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UtteranceFactory extends AbstractFactory {
    private static final String GET_UTTERANCE_BASE =
        "SELECT speaker, utterance FROM episode_utterances";
    private static final String GET_EPISODE_UTTERANCES  =
        GET_UTTERANCE_BASE + " WHERE season_id = ? AND episode_id = ? ORDER BY id ASC";
    private static final String GET_RANDOM_EPISODE_SUFFIX = "ORDER BY RAND() LIMIT 1";
    private static final String GET_RANDOM_EPISODE_UTTERANCE =
        GET_UTTERANCE_BASE + " WHERE season_id = ? AND episode_id = ? " + GET_RANDOM_EPISODE_SUFFIX;
    private static final String GET_RANDOM_SEASON_UTTERANCE =
        GET_UTTERANCE_BASE + " WHERE season_id = ? " + GET_RANDOM_EPISODE_SUFFIX;
    private static final String GET_RANDOM_UTTERANCE =
        GET_UTTERANCE_BASE + " " + GET_RANDOM_EPISODE_SUFFIX;

    public UtteranceFactory(Connection connection) {
        super(connection);
    }

    public List<Utterance> getEpisodeUtterances(int season, int episode)
        throws UtteranceNotFoundException {
        PreparedStatement statement = getPreparedStatement(GET_EPISODE_UTTERANCES);
        List<Utterance> utterances = new ArrayList<Utterance>();

        try {
            statement.setInt(1, season);
            statement.setInt(2, episode);

            ResultSet results = statement.executeQuery();


            while (results.next()) {
                utterances.add(extractUtterance(results));
            }

            if (utterances.size() == 0) {
                throw new UtteranceNotFoundException("Could not find utterances", season, episode);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not fetch Utterances", e);
        }

        return utterances;
    }

    public Utterance getRandomUtterance(Integer season, Integer episode)
        throws UtteranceNotFoundException {
        PreparedStatement statement;

        try {
            if (episode != null && season != null) {
                statement = getPreparedStatement(GET_RANDOM_EPISODE_UTTERANCE);
                statement.setInt(1, season);
                statement.setInt(2, episode);
            } else if (season != null) {
                statement = getPreparedStatement(GET_RANDOM_SEASON_UTTERANCE);
                statement.setInt(1, season);
            } else {
                statement = getPreparedStatement(GET_RANDOM_UTTERANCE);
            }

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return extractUtterance(rs);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not find utterance", e);
        }

        throw new UtteranceNotFoundException("Could not find utterance", season, episode);
    }

    private Utterance extractUtterance(ResultSet rs) throws SQLException {
        return Utterance.builder()
            .speaker(rs.getString("speaker"))
            .utterance(rs.getString("utterance"))
            .build();
    }
}
