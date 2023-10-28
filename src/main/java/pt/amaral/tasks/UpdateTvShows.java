package pt.amaral.tasks;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;

@ApplicationScoped
public class UpdateTvShows {
    @Scheduled(every="10s")
    void teste() {
        System.out.println("----- ola ola ola");
    }
}
