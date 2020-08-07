package hudson.plugins.ec2;

import hudson.Extension;
import hudson.model.AsyncPeriodicWork;
import hudson.model.TaskListener;
import hudson.model.Node;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.plugins.ec2.util.MinimumInstanceChecker;
import jenkins.model.Jenkins;

import com.amazonaws.AmazonClientException;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;

/**
 * @author Bruno Meneguello
 */
@Extension
public class EC2SlaveMonitor extends AsyncPeriodicWork {
    private static final Logger LOGGER = Logger.getLogger(EC2SlaveMonitor.class.getName());

    private final Long recurrencePeriod;

    public EC2SlaveMonitor() {
        super("EC2 alive slaves monitor");
        recurrencePeriod = Long.getLong("jenkins.ec2.checkAlivePeriod", TimeUnit.MINUTES.toMillis(10));
        LOGGER.log(FINE, "EC2 check alive period is {0}ms", recurrencePeriod);
    }

    @Override
    public long getRecurrencePeriod() {
        return recurrencePeriod;
    }

    @Override
    protected void execute(TaskListener listener) throws IOException, InterruptedException {
        LOGGER.entering("EC2SlaveMonitor", "execute", listener);
        removeDeadNodes();
        MinimumInstanceChecker.checkForMinimumInstances();
        LOGGER.exiting("EC2SlaveMonitor", "execute");
    }

    private void removeDeadNodes() {
        LOGGER.entering("EC2SlaveMonitor", "removeDeadNodes");
        List<Node> nodeList = Jenkins.get().getNodes();
        LOGGER.log(FINE, "Checking {0} nodes.", nodeList.size());
        for (Node node : Jenkins.get().getNodes()) {
            if (node instanceof EC2AbstractSlave) {
                final EC2AbstractSlave ec2Slave = (EC2AbstractSlave) node;
                LOGGER.log(FINE, "Considering node: {0}", ec2Slave.getInstanceId());
                try {
                    if (!ec2Slave.isAlive(true)) {
                        LOGGER.info("EC2 instance is dead: " + ec2Slave.getInstanceId());
                        ec2Slave.terminate();
                    } else {
                        LOGGER.log(FINE, "EC2 instance {0} is alive!", ec2Slave.getInstanceId());
                    }
                } catch (AmazonClientException e) {
                    LOGGER.log(INFO, e, () -> "EC2 instance is dead and failed to terminate: " + ec2Slave.getInstanceId());
                    removeNode(ec2Slave);
                }
            } else {
                LOGGER.log(FINE, "Ignoring node {0} of type {1}", new Object[] {node.getNodeName(), node.getClass().getName()});
            }
        }
        LOGGER.exiting("EC2SlaveMonitor", "removeDeadNodes");
    }

    private void removeNode(EC2AbstractSlave ec2Slave) {
        try {
            Jenkins.get().removeNode(ec2Slave);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to remove node: " + ec2Slave.getInstanceId());
        }
    }

}
