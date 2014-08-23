package downloader.impl;

import downloader.URLRequest;
import downloader.URLResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author pjalybin
 * @since 23.08.14 12:06
 */
public class HttpURLResponse implements URLResponse {

    private final URLRequest request;
    private Throwable exception;
    private int contentLength;

    HttpURLResponse(URLRequest request) {
        this.request = request;
    }

    private int statusCode;

    private HttpResponseHeader header;

    private byte[] data;

    @Override
    public URLRequest getURLRequest() {
        return request;
    }

    @Override
    public InputStream getInputStream() {
        if (data == null) return null;
        return new ByteArrayInputStream(data);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public HttpResponseHeader getHeader() {
        return header;
    }

    void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    void setHeader(HttpResponseHeader header) {
        this.header = header;
    }

    void setData(byte[] data) {
        this.data = data;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public int length() {
        return contentLength;
    }

    @Override
    public boolean isFailed() {
        return exception != null;
    }

    @Override
    public Throwable getFailCause() {
        return exception;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }
}
