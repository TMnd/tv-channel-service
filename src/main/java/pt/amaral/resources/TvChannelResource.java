package pt.amaral.resources;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.jboss.resteasy.reactive.RestQuery;
import pt.amaral.models.Movie;
import pt.amaral.models.ShowType;
import pt.amaral.models.TvShow;
import pt.amaral.models.entities.CatShows;
import pt.amaral.service.TvControllerService;
import pt.amaral.utils.Helper;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Path("/api/tv/")
public class TvChannelResource {

    @Inject
    TvControllerService tvController;

    @POST
    @Path("pause")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pause() {
        try {
            tvController.sendCommand("pause");
            return Response.ok("Video paused").build();
        } catch (IOException e) {
            Log.error(e.getMessage());
            return Response.serverError().build();
        }
    }

    @POST
    @Path("resume")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resume() {
        try {
            tvController.sendCommand("play");
            return Response.ok("Video resumed").build();
        } catch (IOException e) {
            Log.error(e.getMessage());
            return Response.serverError().build();
        }
    }

    @POST
    @Path("next")
    @Produces(MediaType.APPLICATION_JSON)
    public Response next() {
        try {
            tvController.sendCommand("next");
            return Response.ok("Loading next video").build();
        } catch (IOException e) {
            Log.error(e.getMessage());
            return Response.serverError().build();
        }
    }

    @POST
    @Path("prev")
    @Produces(MediaType.APPLICATION_JSON)
    public Response prev() {
        try {
            tvController.sendCommand("prev");
            return Response.ok("Loading previous video").build();
        } catch (IOException e) {
            Log.error(e.getMessage());
            return Response.serverError().build();
        }
    }

    @GET
    @Path("nextShow")
    @Produces(MediaType.APPLICATION_JSON)
    public Response random(@RestQuery ZonedDateTime clientCurrentTime) {

        System.out.println(clientCurrentTime);
        boolean isToRandomize = true;

        try {
            Log.info("Process random show.");

            Map<String, List<CatShows>> allShows = tvController.getAllShows();
            List<CatShows> movies = allShows.get(ShowType.MOVIES.toString());
            List<CatShows> tvShows = allShows.get(ShowType.SERIES.toString());
            List<CatShows> documentary = allShows.get(ShowType.DOCUMENTARY.toString());

            if(clientCurrentTime.getHour() >= 8 && clientCurrentTime.getHour() < 16) {
                Log.debug("Get random series");
                if(CollectionUtils.isEmpty(tvShows)) {
                    isToRandomize = false;
                } else {
                    tvController.processSelectedShow(tvShows);
                }
            } else if(clientCurrentTime.getHour() >= 16) {
                Log.debug("Get random movie");
                if(CollectionUtils.isEmpty(movies)) {
                    isToRandomize = false;
                } else {
                    tvController.processSelectedShow(movies);
                }
            } else if(clientCurrentTime.getHour() > 0  && clientCurrentTime.getHour() <= 2) {
                Log.debug("Get random documentary or series");
                if(CollectionUtils.isEmpty(documentary) && CollectionUtils.isEmpty(tvShows)) {
                    isToRandomize = false;
                } else {

                }
            }

            if(isToRandomize) {
                return Response.ok().build();
            }

            return Response.noContent().build();

        } catch (IOException e) {
            Log.error(e.getMessage());
            return Response.serverError().build();
        }
    }

}
