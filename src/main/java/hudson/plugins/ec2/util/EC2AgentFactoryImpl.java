package hudson.plugins.ec2.util;

import java.io.IOException;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.plugins.ec2.*;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.cloudstats.CloudStatistics;
import org.jenkinsci.plugins.cloudstats.PhaseExecutionAttachment;
import org.jenkinsci.plugins.cloudstats.ProvisioningActivity;
import org.jenkinsci.plugins.cloudstats.ProvisioningActivity.Id;

import static org.jenkinsci.plugins.cloudstats.ProvisioningActivity.Phase.PROVISIONING;
import static org.jenkinsci.plugins.cloudstats.ProvisioningActivity.Status.OK;

@Extension
public class EC2AgentFactoryImpl implements EC2AgentFactory {

    @Override
    public EC2OndemandSlave createOnDemandAgent(EC2AgentConfig.OnDemand config)
            throws Descriptor.FormException, IOException {
        if (Jenkins.get().getPlugin("cloud-stats") != null) {
            TrackableEC2OndemandSlave agent = new TrackableEC2OndemandSlave(config.name, config.instanceId,
                    config.description, config.remoteFS, config.numExecutors, config.labelString,
                    config.mode, config.initScript, config.tmpDir, config.nodeProperties, config.remoteAdmin,
                    config.jvmopts, config.stopOnTerminate, config.idleTerminationMinutes, config.publicDNS,
                    config.privateDNS, config.tags, config.cloudName, config.useDedicatedTenancy,
                    config.launchTimeout, config.amiType, config.connectionStrategy, config.maxTotalUses);
            Id id = new Id(config.cloudName, config.description, config.instanceId);
            ProvisioningActivity activity = new ProvisioningActivity(id);
            PhaseExecutionAttachment attachment = new PhaseExecutionAttachment(OK, "OnDemand");
            CloudStatistics.get().attach(activity, PROVISIONING, attachment);
            return agent;
        } else {
            return new EC2OndemandSlave(config.name, config.instanceId, config.description, config.remoteFS, config.numExecutors, config.labelString, config.mode, config.initScript, config.tmpDir, config.nodeProperties, config.remoteAdmin, config.jvmopts, config.stopOnTerminate, config.idleTerminationMinutes, config.publicDNS, config.privateDNS, config.tags, config.cloudName, config.useDedicatedTenancy, config.launchTimeout, config.amiType, config.connectionStrategy, config.maxTotalUses);
        }
    }

    @Override
    public EC2SpotSlave createSpotAgent(EC2AgentConfig.Spot config) throws Descriptor.FormException, IOException {
        if (Jenkins.get().getPlugin("cloud-stats") != null) {
            TrackableEC2SpotSlave agent = new TrackableEC2SpotSlave(config.name, config.spotInstanceRequestId,
                    config.description, config.remoteFS, config.numExecutors, config.mode, config.initScript,
                    config.tmpDir, config.labelString, config.nodeProperties, config.remoteAdmin,
                    config.jvmopts, config.idleTerminationMinutes, config.tags, config.cloudName,
                    config.launchTimeout, config.amiType, config.connectionStrategy, config.maxTotalUses);
            Id id = new Id(config.cloudName, config.description, config.spotInstanceRequestId);
            ProvisioningActivity activity = new ProvisioningActivity(id);
            PhaseExecutionAttachment attachment = new PhaseExecutionAttachment(OK, "OnDemand");
            CloudStatistics.get().attach(activity, PROVISIONING, attachment);
            return agent;
        } else {
            return new EC2SpotSlave(config.name, config.spotInstanceRequestId, config.description, config.remoteFS, config.numExecutors, config.mode, config.initScript, config.tmpDir, config.labelString, config.nodeProperties, config.remoteAdmin, config.jvmopts, config.idleTerminationMinutes, config.tags, config.cloudName, config.launchTimeout, config.amiType, config.connectionStrategy, config.maxTotalUses);
        }
    }
}
