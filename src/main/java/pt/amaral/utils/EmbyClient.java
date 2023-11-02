package pt.amaral.utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import pt.amaral.models.Movie;
import pt.amaral.models.Show;
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
    private final String EMBY_SERVER_DEFAULT_USER_UUID = "87417e26999f4b218b4bfd5d5c862e8c";
    private final String EMBY_ITEMS_GET_MOVIES_ITEMS_BY_CATEGORY = EMBY_SERVER_HOST + "/items?Fields=RunTimeTicks,OriginalTitle,MediaSources,Episode,Path&parentId=%s";
    private final String EMBY_ITEMS_GET_SERIES_ITEMS_BY_CATEGORY = EMBY_SERVER_HOST + "/items?Recursive=true&fields=RunTimeTicks,MediaSources,Episode,Path&IncludeItemTypes=Episode&ParentId=%s";
    private final String EMBY_ITEMS_USER_DATA = EMBY_SERVER_HOST + "/Users/%s/Items/%s";
    private final Map<String, String> REQUEST_HEADER = Map.of(
        "X-Emby-Token", "82e539941c604904882403f7bd6e99ae"
    );
    /*
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
*/

    public EmbyClient() {}
    
    private List<Map<String, Object>> getSerializedResponse(String url) throws IOException {
        String response = httpClient.get(url,this.REQUEST_HEADER);

        Map<String, Object> responseSerialized = Helper.serilizeResponse(response);

        return (List<Map<String, Object>>) responseSerialized.get("Items");
    }

    public Map<String, Object> executeShows() throws IOException {

        Map<String, Object> shows = new HashMap<>();

        shows.put(ShowType.MOVIES.toString(), processMovies());
        shows.put(ShowType.SERIES.toString(), processTvShows());

        return shows;
    }

    public void processPlayState(Show show){

        String url = String.format(EMBY_ITEMS_USER_DATA, EMBY_SERVER_DEFAULT_USER_UUID, show.getId());

        try {
            String response = httpClient.get(url,this.REQUEST_HEADER);

            Map<String, Object> serilizedResponse = Helper.serilizeResponse(response);

            Map<String, Object> userData = (Map<String, Object>) serilizedResponse.get("UserData");

            show.setHasPlayed((Boolean) userData.get("Played"));

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private List<Movie> processMovies() {
        List<Movie> movies = new ArrayList<>();

        try {
            String url = String.format(EMBY_ITEMS_GET_MOVIES_ITEMS_BY_CATEGORY, ShowType.MOVIES.getCatrgoryID());

            List<Map<String, Object>> responseMovies = getSerializedResponse(url);

            for (Map<String, Object> item : responseMovies) {
                String id = String.valueOf(item.get("Id"));
                String name= String.valueOf(item.get("Name"));
                String path= String.valueOf(item.get("Path"));
                Long timeAsMicroseconds = (Long) item.get("RunTimeTicks");
                String type = String.valueOf(item.get("Type"));

                Movie movie = new Movie(
                    id,
                    name,
                    timeAsMicroseconds,
                    ShowType.MOVIES.toString()
                );

                movie.setPath(path);
                movie.setPath(type);

                //processPlayState(id, movie);

                movies.add(movie);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
            //log error
        }

        return movies;
    }

    private List<TvShow> processTvShows() throws IOException {
        String url = String.format(EMBY_ITEMS_GET_SERIES_ITEMS_BY_CATEGORY,  ShowType.SERIES.getCatrgoryID());

        List<Map<String, Object>> episodes = getSerializedResponse(url);

        List<TvShow> tvShowEpisodes = new ArrayList<>();

        for (Map<String, Object> episode : episodes) {
            String id = String.valueOf(episode.get("Id"));
            String name = String.valueOf(episode.get("Name"));
            Long timeAsMicroseconds = (Long) episode.get("RunTimeTicks");
            String episodeNumber = String.valueOf(episode.get("IndexNumber"));
            String path = String.valueOf(episode.get("Path"));
            String showName = String.valueOf(episode.get("SeriesName"));
            String season = String.valueOf(episode.get("ParentIndexNumber"));

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

           // processPlayState(id, tvShow);

            tvShowEpisodes.add(tvShow);
        }

        return tvShowEpisodes;
    }
}
