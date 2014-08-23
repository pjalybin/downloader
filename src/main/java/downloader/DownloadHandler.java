package downloader;

import java.util.concurrent.ExecutionException;

/**
 * Allows user to handle download events:
 * observe event of download lifecycle, read partial results
 * or even break or continue download workflow
 * @author pjalybin
 * @since  23.08.14 11:36
 */
public interface DownloadHandler {

    /**
     * Triggered when download of series of url is started.
     * Usually is triggered once, but can be triggered again on restart
     * @param controller download controller to control task inside handler
     * @throws ExecutionException if something wrong to stop download
     */
    void onDownloadStarted(DownloadController controller) throws ExecutionException;

    /**
     * Triggered when download of specific url is started.
     * Triggered for every url request.
     * Can be triggered again after restart
     * @param controller download controller to control task inside handler
     * @param urlRequest current url request
     * @return true if you do not want to download this task and you want to continue
     * @throws ExecutionException if something wrong with this url, fail it and continue to download next urls
     */
    boolean onDownloadStartedURL(DownloadController controller, URLRequest urlRequest) throws ExecutionException;

    /**
     * Triggered when specific url has finished downloading.
     * Triggered for every not failed url request.
     * Can be triggered again after restart.
     * @param controller download controller to control task inside handler
     * @param urlResponse result of downloading url
     * @throws ExecutionException if something wrong with this url, fail it and continue to download next urls
     */
    void onDownloadFinishedURL(DownloadController controller, URLResponse urlResponse) throws ExecutionException;

    /**
     * Triggered when error has occurred during url download
     * @param controller download controller to control task inside handler
     * @param urlRequest download request parameters which cause the error
     * @param cause exception which cause the error
     * @return true if you do not want to download consequent urls in task and fail whole task
     * @throws ExecutionException rethrow cause or throw another error to fail whole task
     */
    boolean onDownloadFailedURL(DownloadController controller, URLRequest urlRequest, Throwable cause) throws ExecutionException;

    /**
     * Triggered when whole task is failed, cancelled, restarted or broken by user
     * @param controller download controller to control task inside handler
     * @param cause exception which cause the error
     * @throws ExecutionException rethrow cause or throw another error. Throwing of an error aborts restart
     */
    void onDownloadFailed(DownloadController controller, Throwable cause) throws ExecutionException;

    /**
     * Triggered when all urls in list have been downloaded
     * @param controller download controller to control task inside handler
     * @param response result of whole task
     * @throws ExecutionException throw if you are not satisfied with the result and you want to fail whole task
     */
    void onDownloadFinished(DownloadController controller, DownloadResponse response) throws ExecutionException;
}
