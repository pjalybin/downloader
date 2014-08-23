package downloader;

import java.net.URL;

/**
 * Builder for simple creation of download request
 * @author pjalybin
 * @since 23.08.14 19:34
 */
public interface DownloadRequestBuilder {
    /**
     * Add the series of URL to download request
     * @param urls one or more urls
     * @return this builder
     */
    DownloadRequestBuilder urls(URL... urls);

    /**
     * Add url request with additional specific parameters
     * @param request url download request
     * @return this builder
     */
    DownloadRequestBuilder url(URLRequest request);

    /**
     * Set connection timeout of download request
     * @param timeout connection timeout in milliseconds. Zero means infinite timeout.
     * @return this builder
     */
    DownloadRequestBuilder timeout(int timeout);

    /**
     * Set usr handler to work with events raised on download lifecycle
     * @param handler user handler implementation
     * @return this builder
     */
    DownloadRequestBuilder handler(DownloadHandler handler);

    /**
     * Build download request and immediately start it
     * @return controller of download task
     */
    DownloadController start();

    /**
     * Build download request with specified parameters in builder.
     * Not specified parameters remain its default values.
     * @return built download request
     */
    DownloadRequest build();

}
