package hudson.plugins.ec2;

import org.jenkinsci.plugins.cloudstats.ProvisioningActivity.Id;
import org.jenkinsci.plugins.cloudstats.TrackedItem;

import javax.annotation.Nullable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implement the {@link TrackedItem} interface from the optional cloud-stats plugin.
 *
 * @see EC2AbstractSlave#getComputer
 */
public class TrackableEC2Computer extends EC2Computer implements TrackedItem {

    private static final Logger LOGGER = Logger.getLogger(TrackableEC2Computer.class.getName());
    private final String cloudName;
    private final String templateName;
    public TrackableEC2Computer(EC2AbstractSlave slave) {
        super(slave);
        cloudName = slave.cloudName;
        templateName = slave.templateDescription;
    }

    @Nullable
    @Override
    public Id getId() {
        LOGGER.log(Level.FINER, "TrackableEC2Computer.getId() : {0}/{1}", new Object[]{cloudName, templateName});
        return new Id(cloudName, templateName);
    }
}
