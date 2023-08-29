package uk.claritygroup;

import uk.claritygroup.model.CreateMetrics;
import uk.claritygroup.model.UpdateMetrics;
import uk.claritygroup.utility.DateAndTimeUtil;

import java.time.LocalDateTime;
import java.util.Optional;


public abstract class BaseTest {
    protected final CreateMetrics metricsModelWithAllFields= new CreateMetrics("system", "name",1499070300000l,2);
    protected final CreateMetrics metricsModelWithOutValue= new CreateMetrics("system", "name",1499070300000l,null);
    protected final CreateMetrics metricsModelWithOutDate= new CreateMetrics("system", "name",null,2);
    protected final CreateMetrics metricsModelWithOutSystem = new CreateMetrics(null, "name",1499070300000l,null);
    protected final CreateMetrics metricsModelWithOutName= new CreateMetrics("system", null,1499070300000l,2);
    protected final UpdateMetrics updateMetrics = new UpdateMetrics("system", "name",1499070300000l,2);
    protected final UpdateMetrics updateMetricsWithMissingValue = new UpdateMetrics("system", "name",1499070300000l,null);
    protected final UpdateMetrics updateMetricsWithMissingSystem = new UpdateMetrics(null, "name",1499070300000l,null);
    protected final UpdateMetrics updateMetricsWithMissingName = new UpdateMetrics("system", null,1499070300000l,2);
    protected final UpdateMetrics updateMetricsWithMissingDate = new UpdateMetrics("system", "name",null,2);
    protected final UpdateMetrics updateMetricsWithNonExistingSystem = new UpdateMetrics("system2", "1499070300000l",null,2);
  //  protected final uk.claritygroup.kotlin.model.request.CreateMetrics um= new uk.claritygroup.kotlin.model.request.CreateMetrics("system",null,null,0);

}


