package pt.amaral.service;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import pt.amaral.models.*;
import pt.amaral.models.entities.CatShows;
import pt.amaral.utils.EmbyClient;
import pt.amaral.utils.Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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

    public Map<String, List<CatShows>> getAllShows() throws IOException {

        CatShows catShows = new CatShows();
        List<CatShows> shows = (List<CatShows>) catShows.findAll();

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
}
