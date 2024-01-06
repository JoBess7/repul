package ca.ulaval.glo4003.repul.commons.api.jobs;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Trigger;

public interface RepULJob extends Job {
    JobDetail getJobDetail();

    Trigger getTrigger();
}
