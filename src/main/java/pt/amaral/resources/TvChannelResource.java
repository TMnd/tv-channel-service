package pt.amaral.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.amaral.utils.TvController;

import java.io.IOException;

@Path("/api/tv/")
public class TvChannelResource {

    @Inject
    TvController tvController;

    @POST
    @Path("pause")
    @Produces(MediaType.APPLICATION_JSON)
    public Response Pause() {
        try {
            tvController.sendCommand("pause");
                return Response.ok("Video paused").build();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return Response.serverError().build();
        }
    }

    @POST
    @Path("resume")
    @Produces(MediaType.APPLICATION_JSON)
    public Response Resume() {
        try {
            tvController.sendCommand("play");
            return Response.ok("Video resumed").build();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return Response.serverError().build();
        }
    }

    @POST
    @Path("next")
    @Produces(MediaType.APPLICATION_JSON)
    public Response Next() {
        try {
            tvController.sendCommand("next");
            return Response.ok("Loading next video").build();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return Response.serverError().build();
        }
    }

    @POST
    @Path("prev")
    @Produces(MediaType.APPLICATION_JSON)
    public Response Prev() {
        try {
            tvController.sendCommand("prev");
            return Response.ok("Loading previous video").build();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return Response.serverError().build();
        }
    }

}
