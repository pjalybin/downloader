package downloader.impl;

import downloader.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pjalybin
 * @since 23.08.14 20:11
 */
public class HttpDownloadRequestBuilder implements DownloadRequestBuilder {

    private List<URLRequest> urlRequests = new ArrayList<>();
    private DownloadHandler downloadHandler = null;
    private int timeout=0;

    private final DownloadManager downloadManager;

    HttpDownloadRequestBuilder(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    @Override
    public DownloadRequestBuilder urls(URL... urls) {
        for (URL url : urls) {
            urlRequests.add(new HttpURLRequest(url));
        }
        return this;
    }

    @Override
    public DownloadRequestBuilder url(URLRequest request) {
        urlRequests.add(request);
        return this;
    }

    @Override
    public DownloadRequestBuilder timeout(int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout can not be negative");
        }
        this.timeout = timeout;
        return this;
    }

    @Override
    public DownloadRequestBuilder handler(DownloadHandler handler) {
        this.downloadHandler = handler;
        return this;
    }

    @Override
    public DownloadController start() {
        return downloadManager.download(build());
    }

    @Override
    public DownloadRequest build() {
        return new HttpDownloadRequest(urlRequests,timeout,downloadHandler);
    }
}