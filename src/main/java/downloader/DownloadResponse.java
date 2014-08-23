package downloader;

import java.util.List;

/**
 * Result of download task contains list of successful or failed responses.
 * @author pjalybin
 * @since  23.08.14 11:35
 */
public interface DownloadResponse {
    /**
     * Original request of download task
     * @return original request
     */
    DownloadRequest getRequest();

    /**
     * List of successful or failed responses of url downloading
     * @return list of url responses
     */
    List<URLResponse> getURLResponses();
}
