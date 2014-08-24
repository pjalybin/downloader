package downloader;

/**
 * States of download task
 *
 * @author pjalybin
 * @since 23.08.14 21:44
 */
public enum State {
    /**
     * newborn - Not in processing queue or executing thread
     */
    NEW,

    /**
     * Waiting on semaphore block
     */
    WAITING,

    /**
     * Downloading data
     */
    RUNNING,

    /**
     * Paused by user
     */
    PAUSED,

    /**
     * Successfully downloaded the result
     */
    FINISHED,

    /**
     * Failed or cancelled task
     */
    FAILED
}
