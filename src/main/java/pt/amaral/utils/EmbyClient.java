package pt.amaral.utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import pt.amaral.models.Movie;
import pt.amaral.models.ShowType;
import pt.amaral.models.TvShow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class EmbyClient {

    @Inject
    HttpClient httpClient;

    private final String EMBY_SERVER_HOST = "http://10.10.0.222:8096/emby/";
    private final String EMBY_SERVER_API_KEY = "82e539941c604904882403f7bd6e99ae";
    private final String EMBY_ITEMS_GET_ALL = EMBY_SERVER_HOST+"Items?format=json&" +
            "api_key="+EMBY_SERVER_API_KEY+
            "&Recursive=true&IncludeItemTypes=Movie,Series&ExcludeLocationTypes=Virtual&" +
            "Fields=AlternateMediaSources,Overview,RunTimeTicks,OriginalTitle,MediaSources,ProductionYear,Episode,Path&startIndex=0&" +
            "CollapseBoxSetItems=false";
    private final String EMBY_ITEMS_GET_DATA_SEASON = EMBY_SERVER_HOST+"Items?format=json&" +
            "api_key="+EMBY_SERVER_API_KEY+
            "&ParentId=%S&" +
            "IncludeItemTypes=Episode&" +
            "fields=Path,MediaSources,Season,Episode";

    private final String EMBY_ITEMS_GET_EPISODES_DATA_BY_SEASON = EMBY_SERVER_HOST+"Items?format=json&" +
            "api_key="+EMBY_SERVER_API_KEY+
            "&ParentId=%S&" +
            "IncludeItemTypes=Episode&" +
            "fields=Path,MediaSources,Season,Episode,Path";


    public EmbyClient() {}
    
    private List<Map<String, Object>> getSerializedResponse(String url) throws IOException {
        String response = httpClient.get(url);

        Map<String, Object> responseSerialized = Helper.serilizeResponse(response);

        return (List<Map<String, Object>>) responseSerialized.get("Items");
    }

    public Map<String, Object> executeShows() throws IOException {
        List<Map<String, Object>> items = getSerializedResponse(EMBY_ITEMS_GET_ALL);

        Map<String, Object> shows = new HashMap<>();

        shows.put(ShowType.MOVIES.toString(), processMovies(items));
        shows.put(ShowType.SERIES.toString(), processTvShows(items));

        return shows;
    }

    private List<TvShow> processSeriesEpisode(String seasonId, String seasonIndex) throws IOException {
        String url = String.format(EMBY_ITEMS_GET_EPISODES_DATA_BY_SEASON, seasonId);
        List<Map<String, Object>> episodes = getSerializedResponse(url);

        List<TvShow> tvShowEpisodes = new ArrayList<>();

        for (Map<String, Object> episode : episodes) {
            String name = String.valueOf(episode.get("Name"));
            Long timeAsMicroseconds = (Long) episode.get("RunTimeTicks");
            String episodeNumber = String.valueOf(episode.get("IndexNumber"));
            String path = String.valueOf(episode.get("Path"));
            String showName = String.valueOf(episode.get("SeriesName"));

            TvShow tvShow = new TvShow(
                name,
                timeAsMicroseconds,
                ShowType.SERIES.toString()
            );

            tvShow.setSeason(seasonIndex);
            tvShow.setEpisode(episodeNumber);
            tvShow.setPath(path);
            tvShow.setShowName(showName);

            tvShowEpisodes.add(tvShow);
        }

        return tvShowEpisodes;
    }

    private List<TvShow> processSeriesSeasons(String serieId) throws IOException {
        String url = String.format(EMBY_ITEMS_GET_EPISODES_DATA_BY_SEASON, serieId);
        List<Map<String, Object>> seasons = getSerializedResponse(url);

        List<TvShow> allEpisodesFromSeries = new ArrayList<>();

        for (Map<String, Object> season : seasons) {
            String seasonNumber = String.valueOf(season.get("IndexNumber"));
            String seriesParentId = String.valueOf(season.get("Id"));

            List<TvShow> tvShows = processSeriesEpisode(seriesParentId, seasonNumber);
            allEpisodesFromSeries.addAll(tvShows);
        }

        return allEpisodesFromSeries;
    }

    private List<Movie> processMovies(List<Map<String, Object>> items) {
        List<Movie> movies = new ArrayList<>();

        for (Map<String, Object> item : items) {
            String name= String.valueOf(item.get("Name"));
            String path= String.valueOf(item.get("Path"));
            Long timeAsMicroseconds = (Long) item.get("RunTimeTicks");
            String type = String.valueOf(item.get("Type"));

            if(StringUtils.equals(type, ShowType.MOVIES.toString())) {
                Movie movie = new Movie(
                        name,
                        timeAsMicroseconds,
                        ShowType.MOVIES.toString()
                );

                movie.setPath(path);

                movies.add(movie);
            }
        }

        return movies;
    }

    private List<TvShow> processTvShows(List<Map<String, Object>> items) throws IOException {
        List<TvShow> allEpisodes = new ArrayList<>();

        for (Map<String, Object> item : items) {
            String type = String.valueOf(item.get("Type"));

            if(StringUtils.equals(type, ShowType.SERIES.toString())) {
                String seriesID = String.valueOf(item.get("Id"));

                List<TvShow> tvShows = processSeriesSeasons(seriesID);
                allEpisodes.addAll(tvShows);

            }
        }
        return allEpisodes;
    }
}
