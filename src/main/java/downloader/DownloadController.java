package downloader;

import java.util.List;
import java.util.concurrent.Future;

/**
 * The class is used by a user to control a lifecycle of an asynchronous download request.
 * Controller implements Future<DownloadResponse> interface,
 * so you cancel it or get result synchronously
 * @author pjalybin
 * @since  23.08.14 11:36
 */
public interface DownloadController extends Future<DownloadResponse> {
    /**
     * Return download manager which created this class and executes its download task
     * @return download manager
     */
    DownloadManager getManager();

    /**
     * Return initial download request parameters
     * @return download request
     */
    DownloadRequest getRequest();

    /**
     * Interrupt and restart download task if it is running or paused.
     * Does not work on waiting on finished tasks.
     */
    void restart();

    /**
     * Pause the download task.
     */
    void pause();

    /**
     * Resumes paused task.
     */
    void resume();

    /**
     * Cancel or interrupt download task.
     * A stopped task can not be restarted
     */
    void stop();

    /**
     * Return list of completed or failed url downloads.
     * This list will be cleaned on restart
     * @return list of completed or failed tasks
     */
    List<URLResponse> getCompletedResponses();

    /**
     * Return informational class about current downloaded task:
     * url, content length, length of downloaded bytes and state of task
     * @return information about progress of current download
     */
    DownloadInfo getCurrentDownloadInfo();

}
