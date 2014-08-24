package downloader;

import java.util.List;

/**
 * Represents user parameters of data download request:
 * Series of URL to download and various additional parameters.
 *
 * @author pjalybin
 * @since 23.08.14 11:35
 */
public interface DownloadRequest {
    /**
     * List of URL download requests with specific parameters.
     * URLs will be downloaded sequently
     *
     * @return list of url requests
     */
    List<URLRequest> getURLRequests();

    /**
     * Default connection timeout for network connections
     *
     * @return timeout in milliseconds
     */
    int getConnectionTimeout();

    /**
     * User handler to work with events on download lifecycle
     *
     * @return download handler - can be null
     */
    DownloadHandler getHandler();
}
