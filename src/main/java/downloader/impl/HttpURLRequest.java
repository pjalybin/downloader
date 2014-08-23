package downloader.impl;

import downloader.URLRequest;

import java.net.URL;

/**
 * @author pjalybin
 * @since 23.08.14 12:05
 */
public class HttpURLRequest implements URLRequest {

    private final URL url;

    public HttpURLRequest(URL url) {
        this.url = url;
    }

    @Override
    public URL getURL() {
        return url;
    }

}
