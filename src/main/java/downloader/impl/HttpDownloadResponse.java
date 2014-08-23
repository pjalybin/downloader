package downloader.impl;

import downloader.DownloadRequest;
import downloader.DownloadResponse;
import downloader.URLResponse;

import java.util.List;

/**
 * @author pjalybin
 * @since 23.08.14 12:04
 */
public class HttpDownloadResponse implements DownloadResponse {

    private final DownloadRequest request;
    private final List<URLResponse> urlResponses;

    public HttpDownloadResponse(DownloadRequest downloadRequest, List<URLResponse> urlResponses) {
        this.request = downloadRequest;
        this.urlResponses = urlResponses;
    }

    @Override
    public DownloadRequest getRequest() {
        return request;
    }

    @Override
    public List<URLResponse> getURLResponses() {
        return urlResponses;
    }
}
