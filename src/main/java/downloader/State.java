package downloader;

/**
 * States of download task
 *
 * @author pjalybin
 * @since 23.08.14 21:44
 */
public enum State {
    /**
     * newborn - Not in processing queue or in executing thread.
     * Stillborn task cancelled before execution remains with this state
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
     * Successfully downloaded the result.
     * Notice that finished task may contain failed subtasks
     */
    FINISHED,

    /**
     * Failed or cancelled task
     */
    FAILED
}
