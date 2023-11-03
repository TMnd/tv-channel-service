package pt.amaral.tasks;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import pt.amaral.models.Movie;
import pt.amaral.models.ShowType;
import pt.amaral.models.TvShow;
import pt.amaral.models.entities.CatShows;
import pt.amaral.utils.EmbyClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class UpdateTvShows {

    private Integer DIVIDE_TICKS_TO_SECOND = 10000000;

    @Inject
    EmbyClient embyClient;

    private void cleanShows(CatShows catShows) {
        PanacheQuery<PanacheEntityBase> all = catShows.findAll();

        all.stream().forEach(PanacheEntityBase::delete);
    }

    private CatShows createMoviesCatShows(Movie movie) {
        CatShows catShows = new CatShows();
        catShows.setName(movie.getName());
        catShows.setDuration(movie.getTimeAsMicroseconds() / DIVIDE_TICKS_TO_SECOND);
        catShows.setPath(movie.getPath());
        catShows.setType(movie.getType());

        return catShows;
    }

    @Scheduled(cron="0 0 * * 3 ?")
    @Transactional
    void createShowsCatalog() {
        try {
            System.out.println("Fill the catalog shows");
            Map<String, Object> shows = embyClient.executeShows();

            List<Movie> movies = (List<Movie>) shows.get(ShowType.MOVIES.toString());
            List<TvShow> tvShows = (List<TvShow>) shows.get(ShowType.SERIES.toString());
            List<Movie> documentaries = (List<Movie>) shows.get(ShowType.DOCUMENTARY.toString());

            System.out.println("Clean table from exisiting data");
            CatShows catShows = new CatShows();
            cleanShows(catShows);

            System.out.println("Insert movies");

            if(CollectionUtils.isNotEmpty(movies)) {
                for (Movie movie : movies) {
                    if(BooleanUtils.isTrue(movie.getHasPlayed())) {
                        catShows = createMoviesCatShows(movie);

                        catShows.persist();
                    }
                }
            }

            System.out.println("Insert series");

            if(CollectionUtils.isNotEmpty(tvShows)) {
                for (TvShow tvShow : tvShows) {
                    if(BooleanUtils.isTrue(tvShow.getHasPlayed())){
                        catShows = new CatShows();
                        catShows.setName(tvShow.getName());
                        catShows.setDuration(tvShow.getTimeAsMicroseconds() / DIVIDE_TICKS_TO_SECOND);
                        catShows.setPath(tvShow.getPath());
                        catShows.setEpisode(tvShow.getEpisode());
                        catShows.setSeason(tvShow.getSeason());
                        catShows.setType(tvShow.getType());

                        catShows.persist();
                    }
                }
            }

            System.out.println("Insert Documentary");

            if(CollectionUtils.isNotEmpty(documentaries)) {
                for (Movie movie : documentaries) {
                    if(BooleanUtils.isTrue(movie.getHasPlayed())){
                        catShows = createMoviesCatShows(movie);

                        catShows.persist();
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
