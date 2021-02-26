package hudson.plugins.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.InstanceType;
import hudson.model.Node;
import hudson.plugins.ec2.util.EC2JenkinsRule;
import hudson.plugins.ec2.util.SSHCredentialHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.LoggerRule;
import org.mockito.Mockito;

import java.security.Security;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.logging.Level.FINEST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@For(EC2SlaveMonitor.class) public class EC2ExpiredRequestTest {

    @Rule public EC2JenkinsRule r = new EC2JenkinsRule();

    @Rule public LoggerRule logging = new LoggerRule();

    @Before public void init() {
        // Tests using the BouncyCastleProvider failed without that
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        logging.record(EC2Cloud.class, FINEST);
        logging.record(SlaveTemplate.class, FINEST);
        logging.record(EC2SlaveMonitor.class, FINEST);
        logging.record(EC2AbstractSlave.class, FINEST);
        logging.record(Node.class, FINEST);
    }

    @Test public void testEC2SlaveMonitorExecute() throws Exception {
        // given
        SlaveTemplate template = r.getDefaultTemplate();
        SSHCredentialHelper.assureSshCredentialAvailableThroughCredentialProviders("ghi");
        AmazonEC2Cloud cloud = r.getDefaultCloud(template);
        r.jenkins.clouds.add(cloud);
        cloud.provision(template, 1);

        // when

        // tests
        List<EC2Computer> computers = Arrays.stream(r.jenkins.getComputers()).filter(computer -> computer instanceof EC2Computer).map(EC2Computer.class::cast).collect(Collectors.toList());
        assertEquals(2, computers.size());
        assertTrue(computers.stream().map(EC2Computer::isOnline).reduce(Boolean::logicalAnd).orElse(false));
        assertTrue(computers.stream().map(EC2Computer::getNode).map(n -> n.isConnected).reduce(Boolean::logicalAnd).orElse(false));
    }

    //    @Test
    public void testEC2SlaveMonitorExecuteWithExpiredRequest() throws Exception {
        // Arguments split onto newlines matching the constructor definition to make figuring which is which easier.
        SlaveTemplate template = r.getDefaultTemplate();
        SSHCredentialHelper.assureSshCredentialAvailableThroughCredentialProviders("ghi");
        AmazonEC2Cloud cloud = r.getDefaultCloud(template);
        AmazonEC2 amazonEC2 = cloud.connect();
        r.jenkins.clouds.add(cloud);
        r.configRoundtrip();
        assertEquals(2, r.jenkins.getComputers().length);
        assertEquals(2, Arrays.stream(r.jenkins.getComputers()).filter(computer -> computer instanceof EC2Computer).count());
    }

}
