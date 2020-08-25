package hudson.plugins.ec2;

import hudson.model.Descriptor;
import hudson.plugins.ec2.util.EC2AgentConfig;
import hudson.slaves.NodeProperty;
import org.jenkinsci.plugins.cloudstats.ProvisioningActivity.Id;
import org.jenkinsci.plugins.cloudstats.TrackedItem;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implement the {@link TrackedItem} interface from the optional cloud-stats plugin.
 *
 * @see hudson.plugins.ec2.util.EC2AgentFactoryImpl#createSpotAgent(EC2AgentConfig.Spot)
 */
public class TrackableEC2SpotSlave extends EC2SpotSlave implements TrackedItem {

    private static final Logger LOGGER = Logger.getLogger(TrackableEC2SpotSlave.class.getName());
    private final Id id;

    public TrackableEC2SpotSlave(String name, String spotInstanceRequestId, String templateDescription, String remoteFS, int numExecutors, Mode mode, String initScript, String tmpDir, String labelString, List<? extends NodeProperty<?>> nodeProperties, String remoteAdmin, String jvmopts, String idleTerminationMinutes, List<EC2Tag> tags, String cloudName, int launchTimeout, AMITypeData amiType, ConnectionStrategy connectionStrategy, int maxTotalUses) throws Descriptor.FormException, IOException {
        super(name, spotInstanceRequestId, templateDescription, remoteFS, numExecutors, mode, initScript, tmpDir, labelString, nodeProperties, remoteAdmin, jvmopts, idleTerminationMinutes, tags, cloudName, launchTimeout, amiType, connectionStrategy, maxTotalUses);
        id = new Id(cloudName, templateDescription, instanceId);
        LOGGER.log(Level.FINER, "TrackableEC2SpotSlave : {0}", id);
    }

    @Override
    public Id getId() {
        return id;
    }
}
