package downloader;

import java.io.InputStream;

/**
 * Result of download task.
 * Successful responses buffer resource data in memory.
 * Data can be read from input stream.
 * Failed response has an Exception which cause its error
 * @author pjalybin
 * @since  23.08.14 11:37
 */
public interface URLResponse {
    /**
     * Original request of resource download
     * @return original request
     */
    URLRequest getURLRequest();

    /**
     * Input stream of buffered data. Every call returns a fresh input stream
     * @return input stream of buffered data for successful download or null if no data available
     */
    InputStream getInputStream();

    /**
     * Size of downloaded content
     * @return size in bytes
     */
    int length();

    /**
     * Check if download is failed
     * @return true if error occurred on download
     */
    boolean isFailed();

    /**
     * Get an exception which caused the error occurred on download
     * @return exception which caused fail
     */
    Throwable getFailCause();
}
