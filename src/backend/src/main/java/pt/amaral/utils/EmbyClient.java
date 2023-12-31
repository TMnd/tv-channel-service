package pt.amaral.utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.BooleanUtils;
import pt.amaral.models.AppConfiguration;
import pt.amaral.models.Movie;
import pt.amaral.models.ShowType;
import pt.amaral.models.TvShow;

import java.io.IOException;
import java.util.*;

@ApplicationScoped
public class EmbyClient {

    @Inject
    HttpClient httpClient;

    private final String EMBY_GET_ITEMS_BY_CATEGORY;
    private final Map<String, String> REQUEST_HEADER;

    public EmbyClient() {
        AppConfiguration appConfiguration = AppConfiguration.getInstance();

        EMBY_GET_ITEMS_BY_CATEGORY = appConfiguration.getEmbyServerHost() + "/Users/" + appConfiguration.getEmbyServerDefaultUserUUID() + "/items?Recursive=true&IncludeItemTypes=%S&Fields=RunTimeTicks,path,MediaSources&ParentId=%S";
        REQUEST_HEADER = Map.of(
        "X-Emby-Token", appConfiguration.getEmbyServerApiToken()
        );
    }
    
    private List<Map<String, Object>> getSerializedResponse(String url) throws IOException {
        String response = httpClient.get(url,this.REQUEST_HEADER);

        Map<String, Object> responseSerialized = Helper.serilizeResponse(response);

        return (List<Map<String, Object>>) responseSerialized.get("Items");
    }

    public Map<String, Object> executeShows() throws IOException {

        Map<String, Object> shows = new HashMap<>();

        shows.put(ShowType.MOVIES.toString(), processShows(ShowType.MOVIES));
        shows.put(ShowType.DOCUMENTARY.toString(), processShows(ShowType.DOCUMENTARY));
        shows.put(ShowType.SERIES.toString(), processTvShows());

        return shows;
    }

    private List<Movie> processShows(ShowType showType) {
        List<Movie> movies = new ArrayList<>();

        try {
            String url = String.format(EMBY_GET_ITEMS_BY_CATEGORY, showType, showType.getCategoryID());

            List<Map<String, Object>> responseMovies = getSerializedResponse(url);

            for (Map<String, Object> item : responseMovies) {
                String id = String.valueOf(item.get("Id"));
                String name= String.valueOf(item.get("Name"));
                String path= String.valueOf(item.get("Path"));
                Long timeAsMicroseconds = (Long) item.get("RunTimeTicks");
                Map<String, Object> userData = (Map<String, Object>) item.get("UserData");
                String played = String.valueOf(userData.get("Played"));

                Movie movie = new Movie(
                    id,
                    name,
                    timeAsMicroseconds,
                    showType.toString()
                );

                movie.setPath(path);
                movie.setHasPlayed(BooleanUtils.toBoolean(played));

                movies.add(movie);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
            //log error
        }

        return movies;
    }

    private List<TvShow> processShowEpisodes(String id) throws IOException {
        String url = String.format(EMBY_GET_ITEMS_BY_CATEGORY, ShowType.EPISODE, id);

        List<Map<String, Object>> episodes = getSerializedResponse(url);

        List<TvShow> tvShowEpisodes = new ArrayList<>();

        for (Map<String, Object> episode : episodes) {
            String name = String.valueOf(episode.get("Name"));
            Long timeAsMicroseconds = (Long) episode.get("RunTimeTicks");
            String episodeNumber = String.valueOf(episode.get("IndexNumber"));
            String path = String.valueOf(episode.get("Path"));
            String showName = String.valueOf(episode.get("SeriesName"));
            String season = String.valueOf(episode.get("ParentIndexNumber"));
            Map<String, Object> userData = (Map<String, Object>) episode.get("UserData");
            String played = String.valueOf(userData.get("Played"));

            TvShow tvShow = new TvShow(
                    id,
                    name,
                    timeAsMicroseconds,
                    ShowType.SERIES.toString()
            );

            tvShow.setSeason(season);
            tvShow.setEpisode(episodeNumber);
            tvShow.setPath(path);
            tvShow.setShowName(showName);
            tvShow.setHasPlayed(BooleanUtils.toBoolean(played));

            tvShowEpisodes.add(tvShow);
        }

        return tvShowEpisodes;
    }

    private List<TvShow> processTvShows() throws IOException {
        String url = String.format(EMBY_GET_ITEMS_BY_CATEGORY, ShowType.SERIES, ShowType.SERIES.getCategoryID());

        List<Map<String, Object>> shows = getSerializedResponse(url);

        List<TvShow> allEpisodes = new ArrayList<>();

        for (Map<String, Object> show : shows) {
            String id = String.valueOf(show.get("Id"));

            List<TvShow> tvShows = processShowEpisodes(id);

            allEpisodes.addAll(tvShows);
        }

        return allEpisodes;
    }
}
