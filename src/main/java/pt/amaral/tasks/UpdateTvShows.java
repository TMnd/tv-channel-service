package pt.amaral.tasks;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;
import pt.amaral.models.Movie;
import pt.amaral.models.ShowType;
import pt.amaral.models.TvShow;
import pt.amaral.utils.EmbyClient;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class UpdateTvShows {

    @Inject
    EmbyClient embyClient;

    @Scheduled(every="120s")
    void teste() {
        try {
            Map<String, Object> shows = embyClient.executeShows();

            List<Movie> movies = (List<Movie>) shows.get(ShowType.MOVIES.toString());
            List<TvShow> tvShows = (List<TvShow>) shows.get(ShowType.SERIES.toString());

            if(CollectionUtils.isNotEmpty(movies)) {
                for (Movie movie : movies) {
                    System.out.println(movie.getName() + " || " + movie.getTimeAsMicroseconds() + " || " + movie.getHasPlayed());
                }
            }
            System.out.println("-----------------------");
            if(CollectionUtils.isNotEmpty(tvShows)) {
                for (TvShow tvShow : tvShows) {
                    System.out.println(tvShow.getName() + " || " + tvShow.getTimeAsMicroseconds() + " || " + tvShow.getHasPlayed());
                }
            }
            System.out.println("-----------------------");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
