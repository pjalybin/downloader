package downloader;

/**
 * Miscellaneous class with information about cureent download
 * @author pjalybin
 * @since 23.08.14 18:30
 */
public interface DownloadInfo {
    /**
     * Current downloading url request
     * @return url request
     */
    URLRequest getCurrentRequest();

    /**
     * Length of already received data
     * @return length in bytes
     */
    int getDownloadedLength();

    /**
     * Total size of current download
     * @return length in bytes
     */
    int getContentLength();

    /**
     * State of whole download: waiting, running, paused etc
     * @return state of download
     */
    State getState();
}
