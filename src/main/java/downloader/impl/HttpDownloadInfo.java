package downloader.impl;

import downloader.DownloadInfo;
import downloader.State;
import downloader.URLRequest;

/**
 * @author pjalybin
 * @since 23.08.14 18:31
 */
public class HttpDownloadInfo implements DownloadInfo {
    private final URLRequest currentRequest;
    private final int contentLength;
    private final int downloadedLength;
    private final State state;

    public HttpDownloadInfo(URLRequest currentRequest, int contentLength, int downloadedLength, State state) {
        this.currentRequest = currentRequest;
        this.contentLength = contentLength;
        this.downloadedLength = downloadedLength;
        this.state = state;
    }

    @Override
    public URLRequest getCurrentRequest() {
        return currentRequest;
    }

    @Override
    public int getDownloadedLength() {
        return downloadedLength;
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    @Override
    public State getState() {
        return state;
    }
}
