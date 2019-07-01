package dev.naydichev.slab.saas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.LambdaRuntime;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.naydichev.slab.saas.db.ConnectionFactory;
import dev.naydichev.slab.saas.db.EpisodeFactory;
import dev.naydichev.slab.saas.db.SeasonFactory;
import dev.naydichev.slab.saas.db.UtteranceFactory;
import dev.naydichev.slab.saas.exceptions.NotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final LambdaLogger LOG = LambdaRuntime.getLogger();
    private static final Pattern REQUEST_PATTERN = Pattern.compile("^(?<seasons>\\/seasons\\/?(?<season>\\d+)?(\\/?episodes\\/?(?<episode>\\d+)?)?)?\\/?(?<quote>quotes)?\\/?$");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
        APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        String path = apiGatewayProxyRequestEvent.getPath();

        Matcher matcher = REQUEST_PATTERN.matcher(path);

        if (!matcher.matches()) {
            LOG.log(String.format("Path '%s' is not valid", path));
            response.setStatusCode(404);
            response.setBody("That's a shame.");
            return response;
        }

        try (Connection connection = getConnection()) {
            Integer season = null;
            Integer episode = null;
            boolean quotes = false;

            if (matcher.group("season") != null) {
                season = Integer.valueOf(matcher.group("season"));
            }

            if (matcher.group("episode") != null) {
                episode = Integer.valueOf(matcher.group("episode"));
            }

            if (matcher.group("quote") != null) {
                quotes = true;
            }

            LOG.log(String.format("[season=%d, episode=%d, quotes=%b]", season, episode, quotes));

            SeasonFactory seasonFactory = new SeasonFactory(connection);
            EpisodeFactory episodeFactory = new EpisodeFactory(connection);
            UtteranceFactory utteranceFactory = new UtteranceFactory(connection);

            Object body;
            if (quotes) {
                body = utteranceFactory.getRandomUtterance(season, episode);
            } else if (season != null && episode != null) {
                body = episodeFactory.getEpisode(season, episode);
            } else if (season != null) {
                body = seasonFactory.getSeason(season);
            } else if (matcher.group("seasons") != null){
                body = seasonFactory.getAllSeasons();
            } else {
                body = utteranceFactory.getRandomUtterance(null, null);
            }

            LOG.log(String.format("[response=%s]", GSON.toJson(body)));

            response.setBody(GSON.toJson(body));
        } catch (SQLException e) {
            response.setStatusCode(500);
            response.setBody(GSON.toJson(e));
        } catch (NotFoundException e) {
            response.setStatusCode(404);
            response.setBody(GSON.toJson(e));
        }

        return response;
    }

    private Connection getConnection() {
        String jdbcUrl = System.getenv("JDBC_URL");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");

        ConnectionFactory.createConnection(jdbcUrl, user, pass);

        return ConnectionFactory.getConnection();
    }
}
