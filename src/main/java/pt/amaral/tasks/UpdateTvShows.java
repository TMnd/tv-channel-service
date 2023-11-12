package pt.amaral.tasks;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import pt.amaral.models.Movie;
import pt.amaral.models.ShowType;
import pt.amaral.models.TvShow;
import pt.amaral.models.entities.CatCustomShowsTypes;
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

        if(CollectionUtils.isNotEmpty(all.list())){
            all.stream().forEach(PanacheEntityBase::delete);
        }
    }

    private CatShows createMoviesCatShows(Movie movie) {
        CatShows catShows = new CatShows();
        catShows.setName(movie.getName());
        catShows.setDuration(movie.getTimeAsMicroseconds() / DIVIDE_TICKS_TO_SECOND);
        catShows.setPath(movie.getPath());
        catShows.setType(movie.getType());

        return catShows;
    }

    private CatCustomShowsTypes getCustomType(String name) {
        return CatCustomShowsTypes.findById(name);
    }

    @Scheduled(every="200s")
    @Transactional
    void createShowsCatalog() {
        Log.info("Updating the show catalog");
        try {
            Map<String, Object> shows = embyClient.executeShows();

            List<Movie> movies = (List<Movie>) shows.get(ShowType.MOVIES.toString());
            List<TvShow> tvShows = (List<TvShow>) shows.get(ShowType.SERIES.toString());
            List<Movie> documentaries = (List<Movie>) shows.get(ShowType.DOCUMENTARY.toString());

            Log.debug("Clean the table from existing data.");
            CatShows catShows = new CatShows();
            cleanShows(catShows);

            Log.debug("Insert movies");

            if(CollectionUtils.isNotEmpty(movies)) {
                for (Movie movie : movies) {
                    if(BooleanUtils.isTrue(movie.getHasPlayed())) {
                        catShows = createMoviesCatShows(movie);

                        catShows.persist();
                    }
                }
            }

            Log.debug("Insert series");

            if(CollectionUtils.isNotEmpty(tvShows)) {
                for (TvShow tvShow : tvShows) {
                    if(BooleanUtils.isTrue(tvShow.getHasPlayed())){
                        catShows = new CatShows();
                        String name = tvShow.getName();

                        catShows.setName(name);
                        catShows.setDuration(tvShow.getTimeAsMicroseconds() / DIVIDE_TICKS_TO_SECOND);
                        catShows.setPath(tvShow.getPath());
                        catShows.setEpisode(tvShow.getEpisode());
                        catShows.setSeason(tvShow.getSeason());
                        catShows.setSeries(tvShow.getShowName());

                        String type = tvShow.getType();

                        CatCustomShowsTypes customType = getCustomType(name);
                        if(customType != null) {
                            type = customType.getCustomType();
                        }

                        catShows.setType(type);

                        catShows.persist();

                    }
                }
            }

            Log.debug("Insert documentaries");

            if(CollectionUtils.isNotEmpty(documentaries)) {
                for (Movie movie : documentaries) {
                    if(BooleanUtils.isTrue(movie.getHasPlayed())){
                        catShows = createMoviesCatShows(movie);

                        catShows.persist();
                    }
                }
            }

            Log.info("Updating done");

        } catch (IOException e) {
            Log.error("Fail to update show catalog", e);
        }
    }
}
