package uk.claritygroup;

import uk.claritygroup.model.CreateMetrics;
import uk.claritygroup.model.UpdateMetrics;


public abstract class BaseTest {
    protected final CreateMetrics metricsModelWithAllFields= new CreateMetrics("system", "name",1499070300000l,2);
    protected final CreateMetrics metricsModelWithOutValue= new CreateMetrics("system", "name",1499070300000l,null);
    protected final CreateMetrics metricsModelWithOutDate= new CreateMetrics("system", "name",null,2);
    protected final CreateMetrics metricsModelWithOutSystem = new CreateMetrics(null, "name",1499070300000l,null);
    protected final CreateMetrics metricsModelWithOutName= new CreateMetrics("system", null,1499070300000l,2);
    protected final UpdateMetrics updateMetrics = new UpdateMetrics("system2", "name",1499070300000l,2);
    protected final UpdateMetrics updateMetricsWithMissingValue = new UpdateMetrics("system2", "name",1499070300000l,null);
    protected final UpdateMetrics updateMetricsWithMissingSystem = new UpdateMetrics(null, "name",1499070300000l,null);
    protected final UpdateMetrics updateMetricsWithMissingName = new UpdateMetrics("system2", null,1499070300000l,2);
    protected final UpdateMetrics updateMetricsWithMissingDate = new UpdateMetrics("system2", "name",null,2);
}
