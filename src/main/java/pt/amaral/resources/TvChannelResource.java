package pt.amaral.resources;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestQuery;
import pt.amaral.models.ShowResult;
import pt.amaral.service.TvControllerService;
import pt.amaral.utils.RandomizeFailError;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;

@Path("/api/tv/")
public class TvChannelResource {

    @Inject
    TvControllerService tvControllerService;

    @POST
    @Path("pause")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pause() {
        try {
            tvControllerService.sendCommand("pause");
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
            tvControllerService.sendCommand("play");
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
            tvControllerService.sendCommand("next");
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
            tvControllerService.sendCommand("prev");
            return Response.ok("Loading previous video").build();
        } catch (IOException e) {
            Log.error(e.getMessage());
            return Response.serverError().build();
        }
    }

    @GET
    @Path("nextShow")
    @Produces(MediaType.APPLICATION_JSON)
    public Response random(@RestQuery ZonedDateTime time) {

        Log.info("Process random show.");

        try {
            ShowResult showResult = tvControllerService.selectRandomShow(time);
            Log.info("Show selected: " + showResult.getName());
            return Response.ok(showResult).build();
        } catch (RandomizeFailError e) {
            Log.error(e.getMessage());
            return Response.noContent().build();
        }

    }

    @PUT
    @Path("updateCatalog")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update() {

        try {
            tvControllerService.forceUpdateShowCatalog();
            return Response.ok("Catalog updated").build();
        } catch (IOException e) {
            Log.error("Fail to update show catalog", e);
            return Response.serverError().build();
        }

    }
}
