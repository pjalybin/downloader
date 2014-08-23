package downloader;

import java.net.URL;

/**
 * Request of single resource download from URL.
 * Can also contain additional parameter of download
 * @author pjalybin
 * @since  23.08.14 11:37
 */
public interface URLRequest {
    /**
     * Unified Resource Locator of the requested resource
     * @return requested URL
     */
    URL getURL();
}
