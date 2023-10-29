package pt.amaral.tasks;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import jakarta.inject.Inject;
import pt.amaral.models.Movie;
import pt.amaral.models.ShowType;
import pt.amaral.models.TvShow;
import pt.amaral.utils.EmbyClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class UpdateTvShows {

    @Inject
    EmbyClient embyClient;

    @Scheduled(every="60s")
    void teste() {
        try {
            Map<String, Object> shows = embyClient.executeShows();

            List<Movie> movies = (List<Movie>) shows.get(ShowType.MOVIES.toString());
            List<TvShow> tvShows = (List<TvShow>) shows.get(ShowType.SERIES.toString());

            for (Movie movie : movies) {
                System.out.println(movie.getName() + " || " + movie.getTimeAsMicroseconds());
            }
            System.out.println("-----------------------");
            for (TvShow tvShow : tvShows) {
                System.out.println(tvShow.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
