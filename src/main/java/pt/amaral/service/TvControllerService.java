package pt.amaral.service;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pt.amaral.models.*;
import pt.amaral.models.entities.CatShows;
import pt.amaral.utils.EmbyClient;
import pt.amaral.utils.Helper;
import pt.amaral.utils.RandomizeFailError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TvControllerService {

    private final AppConfiguration appConfiguration = AppConfiguration.getInstance();

    public ShowResult processSelectedShow(List<CatShows> catShows) {
        ShowResult showResult = new ShowResult();

        int index = Helper.randomValue(catShows.size());

        CatShows catShow = catShows.get(index);

        showResult.setName(catShow.getName());
        showResult.setPath(catShow.getPath());
        showResult.setDuration(catShow.getDuration());

        String episode = catShow.getEpisode();
        String season = catShow.getSeason();

        if(episode != null && season != null) {
            showResult.setEpisode(episode);
            showResult.setSeason(episode);
        }

        return showResult;
    }

    /**
     * Select a random show based of the client's time of request.
     * @param clientCurrentTime Date of the requet in ISO 8601 format (Ex in UTC: 2023-11-11T07:19:01Z)
     * @return ShowResult
     * @throws RandomizeFailError: If for any reason, there aren't any shows to select
     */
    public ShowResult selectRandomShow(ZonedDateTime clientCurrentTime) throws RandomizeFailError {
        boolean isToRandomize = true;

        Map<String, List<CatShows>> allShows = getAllShows();
        List<CatShows> movies = allShows.get(ShowType.MOVIES.toString());
        List<CatShows> tvShows = allShows.get(ShowType.SERIES.toString());
        List<CatShows> documentary = allShows.get(ShowType.DOCUMENTARY.toString());

        ShowResult selectedRandomShow = null;

        if(isLateNightSchedule(clientCurrentTime)) {
            Log.debug("Get random series");
            if(CollectionUtils.isEmpty(tvShows)) {
                isToRandomize = false;
            } else {
                selectedRandomShow = processSelectedShow(tvShows);
            }
        } else if(isLateDaySchedule(clientCurrentTime)) {
            Log.debug("Get random movie");
            if(CollectionUtils.isEmpty(movies)) {
                isToRandomize = false;
            } else {
                selectedRandomShow = processSelectedShow(movies);
            }
        } else if(isDayTimeSchedule(clientCurrentTime)) {
            Log.debug("Get random documentary or series");
            if(CollectionUtils.isEmpty(documentary) && CollectionUtils.isEmpty(tvShows)) {
                isToRandomize = false;
            } else {
                //selectedRandomShow = processSelectedShow(tvShows, documentary);
            }
        }

        if(!isToRandomize){
            throw new RandomizeFailError("No show was selected");
        }

        return selectedRandomShow;
    }

    /**
     * Get all "avaiable" shows in the data base
     * @return A Map of the shows by the type of the show.
     */
    public Map<String, List<CatShows>> getAllShows() {

        CatShows catShows = new CatShows();
        List<CatShows> shows = catShows.findAll().list();

        Map<String, List<CatShows>> showMap = new HashMap<>();

        for (CatShows show : shows) {
            String showType = show.getType();
            showMap.computeIfAbsent(showType, k -> new ArrayList<>()).add(show);
        }

        return showMap;
    }

    public void sendCommand(String command) throws IOException {
        Log.debug("Send command to client: " + command);

        Socket socket = new Socket(appConfiguration.getVlcAddress(), appConfiguration.getVlcPort());
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String response;
        while ((response = in.readLine()) != null) {
            if (response.contains("VLC media")) {
                break;
            }
        }

        out.println(appConfiguration.getVlcPassword());

        while ((response = in.readLine()) != null) {
            if (response.contains("Master")){
                out.println(command);
                break;
            }
        }
    }

    /**
     * The current time is between 00H and 02H
     * @param currentTime: Time to check
     * @return true/false
     */
    public Boolean isLateNightSchedule(ZonedDateTime currentTime) {
        Log.debug("Is late night.");
        return currentTime.getHour() > 0  && currentTime.getHour() <= 2;
    }

    /**
     * The current time is between 08H and 16H
     * @param currentTime: Time to check
     * @return true/false
     */
    public Boolean isDayTimeSchedule(ZonedDateTime currentTime) {
        Log.debug("Is day time.");
        return currentTime.getHour() >= 8 && currentTime.getHour() < 16;
    }

    /**
     * The current time is between 16H and 00H
     * @param currentTime: Time to check
     * @return true/false
     */
    public Boolean isLateDaySchedule(ZonedDateTime currentTime) {
        Log.debug("Is late day.");
        return currentTime.getHour() >= 16;
    }
}
