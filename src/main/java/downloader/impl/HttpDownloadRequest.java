package downloader.impl;

import downloader.DownloadHandler;
import downloader.DownloadRequest;
import downloader.URLRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author pjalybin
 * @since 23.08.14 12:04
 */
public class HttpDownloadRequest implements DownloadRequest {

    private final List<URLRequest> urlRequests;
    private final int timeout;
    private final DownloadHandler DownloadHandler;

    public HttpDownloadRequest(List<URLRequest> urlRequests, int timeout, DownloadHandler downloadHandler) {
        this.urlRequests = urlRequests;
        this.timeout = timeout;
        DownloadHandler = downloadHandler;
    }

    @Override
    public int getConnectionTimeout() {
        return timeout;
    }

    @Override
    public DownloadHandler getHandler() {
        return DownloadHandler;
    }

    @Override
    public List<URLRequest> getURLRequests() {
        return Collections.unmodifiableList(urlRequests);
    }

}
