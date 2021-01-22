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
    private final Id id;

    public TrackableEC2Computer(EC2AbstractSlave slave) {
        super(slave);
        id = new Id(slave.cloudName, slave.templateDescription, slave.getInstanceId());
        LOGGER.log(Level.FINER, "TrackableEC2Computer : {0}", id);
    }

    @Nullable
    @Override
    public Id getId() {
        return id;
    }
}
