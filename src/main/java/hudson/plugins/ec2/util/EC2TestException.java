package hudson.plugins.ec2.util;

public class EC2TestException extends RuntimeException {
    public EC2TestException() {
        super();
    }

    public EC2TestException(String message) {
        super(message);
    }

    public EC2TestException(String message, Throwable cause) {
        super(message, cause);
    }

    public EC2TestException(Throwable cause) {
        super(cause);
    }

    public EC2TestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
