package ca.ulaval.glo4003.repul.config.initializer;

import java.util.ArrayList;
import java.util.List;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import ca.ulaval.glo4003.repul.commons.api.jobs.RepULJob;

public class JobInitializer {
    private final List<RepULJob> jobs = new ArrayList<>();

    public void launchJobs() {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            for (RepULJob job : jobs) {
                scheduler.scheduleJob(job.getJobDetail(), job.getTrigger());
            }
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public JobInitializer withJob(RepULJob job) {
        jobs.add(job);
        return this;
    }
}
