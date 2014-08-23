package downloader;

/**
 * Root class of downloader system.
 * Can contain executing queue.
 * Must be closed after use.
 * @author pjalybin
 * @since 23.08.14 11:36
 */
public interface DownloadManager extends AutoCloseable {

    /**
     * prepare request and start download using handy request builder
     * @return builder of request parameter
     */
    DownloadRequestBuilder download();

    /**
     * Download prepared request
     * @param request request to download
     * @return controller to control asynchronous download
     */
    DownloadController download(DownloadRequest request);


}
