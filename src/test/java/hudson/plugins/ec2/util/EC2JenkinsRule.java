package hudson.plugins.ec2.util;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.Reservation;
import hudson.model.Computer;
import hudson.model.Descriptor;
import hudson.model.Label;
import hudson.model.Node;
import hudson.plugins.ec2.AmazonEC2Cloud;
import hudson.plugins.ec2.EC2AbstractSlave;
import hudson.plugins.ec2.EC2OndemandSlave;
import hudson.plugins.ec2.SlaveTemplate;
import hudson.slaves.NodeProvisioner.PlannedNode;
import org.jvnet.hudson.test.JenkinsRule;
import org.powermock.reflect.Whitebox;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

public class EC2JenkinsRule extends JenkinsRule {

    private transient final AtomicInteger nodeCounter = new AtomicInteger();
    private transient final AmazonEC2 mockConnection = getMockAmazonEC2Connection();
    private transient final DescribeInstancesResult mockDescribeInstancesResult = getMockDescribeInstancesResult();
    private transient final List<Reservation> reservationList = new ArrayList<>();

    private DescribeInstancesResult getMockDescribeInstancesResult() {
        DescribeInstancesResult result = mock(DescribeInstancesResult.class);
        when(result.getReservations()).thenReturn(reservationList);
        return null;
    }

    private AmazonEC2 getMockAmazonEC2Connection() {
        AmazonEC2 connection = mock(AmazonEC2.class);
        DescribeInstancesResult result = new DescribeInstancesResult();
        when(connection.describeInstances(any(DescribeInstancesRequest.class))).thenReturn(mockDescribeInstancesResult);
        when(connection.describeSpotInstanceRequests(any(DescribeSpotInstanceRequestsRequest.class))).thenReturn();
        return connection;
    }

    public SlaveTemplate getDefaultTemplate() {
        return mock(SlaveTemplate.class);
    }

    public AmazonEC2Cloud getDefaultCloud(SlaveTemplate template) {
        AmazonEC2Cloud mockCloud = mock(AmazonEC2Cloud.class);
        Whitebox.setInternalState(mockCloud, "slaveCountingLock", new ReentrantLock());
        Whitebox.setInternalState(mockCloud, "templates", new ArrayList<SlaveTemplate>());
        doCallRealMethod().when(mockCloud).provision(eq(template), anyInt());
        doCallRealMethod().when(mockCloud).provision(any(Label.class), anyInt());
        when(mockCloud.getTemplates()).thenReturn(Collections.singletonList(template));
        when(mockCloud.getTemplate(anyString())).thenReturn(template);
        when(mockCloud.getTemplate(any(Label.class))).thenReturn(template);
        when(mockCloud.connect()).thenReturn(mockConnection);

        return mockCloud;
    }

    private Callable<Node> getDefaultNodeCallable() {
        return this::getDefaultEC2OnDemandAgent;
    }

    private Node getDefaultEC2OnDemandAgent() {
        return mock(EC2OndemandSlave.class);
    }

    @Nonnull private List<PlannedNode> getDefaultPlannedNodes(@Nonnull SlaveTemplate slaveTemplate, @Nonnull Callable<Node> callable, int count) {
        List<PlannedNode> nodes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            nodes.add(getDefaultPlannedNode(slaveTemplate, callable));
        }
        return nodes;
    }

    @Nonnull private PlannedNode getDefaultPlannedNode(@Nonnull SlaveTemplate slaveTemplate, @Nonnull Callable<Node> callable) {
        return new PlannedNode(slaveTemplate.getSlaveName(getInstanceId()), Computer.threadPoolForRemoting.submit(callable), slaveTemplate.getNumExecutors());
    }

    public EC2AbstractSlave getDefaultEC2Agent(@Nonnull AmazonEC2Cloud cloud, @Nonnull SlaveTemplate template)
            throws IOException, Descriptor.FormException {
        return new EC2OndemandSlave(getInstanceId());
    }

    private String getInstanceId() {
        return "Agent " + nodeCounter.incrementAndGet();
    }

}
