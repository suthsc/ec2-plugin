package hudson.plugins.ec2;

import hudson.model.Descriptor;
import hudson.plugins.ec2.util.EC2AgentConfig;
import hudson.slaves.NodeProperty;
import org.jenkinsci.plugins.cloudstats.ProvisioningActivity;
import org.jenkinsci.plugins.cloudstats.TrackedItem;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implement the {@link TrackedItem} interface from the optional cloud-stats plugin.
 *
 * @see hudson.plugins.ec2.util.EC2AgentFactoryImpl#createOnDemandAgent(EC2AgentConfig.OnDemand)
 */
public class TrackableEC2OndemandSlave extends EC2OndemandSlave implements TrackedItem {

    private static final Logger LOGGER = Logger.getLogger(TrackableEC2OndemandSlave.class.getName());

    public TrackableEC2OndemandSlave(String name, String instanceId, String templateDescription, String remoteFS, int numExecutors, String labelString, Mode mode, String initScript, String tmpDir, List<? extends NodeProperty<?>> nodeProperties, String remoteAdmin, String jvmopts, boolean stopOnTerminate, String idleTerminationMinutes, String publicDNS, String privateDNS, List<EC2Tag> tags, String cloudName, boolean useDedicatedTenancy, int launchTimeout, AMITypeData amiType, ConnectionStrategy connectionStrategy, int maxTotalUses) throws Descriptor.FormException, IOException {
        super(name, instanceId, templateDescription, remoteFS, numExecutors, labelString, mode, initScript, tmpDir, nodeProperties, remoteAdmin, jvmopts, stopOnTerminate, idleTerminationMinutes, publicDNS, privateDNS, tags, cloudName, useDedicatedTenancy, launchTimeout, amiType, connectionStrategy, maxTotalUses);
    }

    public TrackableEC2OndemandSlave(String instanceId) throws Descriptor.FormException, IOException {
        super(instanceId);
    }

    @Override
    public ProvisioningActivity.Id getId() {
        LOGGER.log(Level.FINER, "TrackableEC2OndemandSlave.getId() : {0}/{1}", new Object[]{cloudName, templateDescription});
        return new ProvisioningActivity.Id(cloudName, templateDescription);
    }
}
