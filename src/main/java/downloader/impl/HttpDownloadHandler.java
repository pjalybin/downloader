package downloader.impl;

import downloader.DownloadController;
import downloader.DownloadHandler;
import downloader.URLRequest;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

/**
 * Additional triggers to control downloading of HTTP response
 * @author pjalybin
 * @since 23.08.14 11:42
 */
public interface HttpDownloadHandler extends DownloadHandler {

    /**
     * Triggers when HTTP status code has been received from server
     * @param controller download controller to control the task
     * @param request current url request
     * @param httpStatusCode received HTTP status code
     * @return true if you don't need to download rest
     * @throws ExecutionException if something wrong and you do not want to download this task and you want to continue
     */
    boolean onHttpStatus(DownloadController controller, URLRequest request, int httpStatusCode) throws ExecutionException;

    /**
     * Triggers when HTTP header is received from server
     * @param controller download controller to control the task
     * @param request current url request
     * @param header received HTTP header
     * @return if you don't need to download rest
     * @throws ExecutionException if something wrong and you do not want to download this task and you want to continue
     */
    boolean onHeader(DownloadController controller, URLRequest request, HttpResponseHeader header) throws ExecutionException;

    /**
     * Trigger when response content is ready to download.
     * You can read it from input stream by yourself.
     * Use it when you need to download big files or responses with unknown content length
     * @param controller download controller to control the task
     * @param request current url request
     * @param stream input stream of HTTP response
     * @return true You must return true if you read content from input stream to prevent subsequent download to buffer
     * @throws ExecutionException  if something wrong and you do not want to download this task and you want to continue
     */
    boolean onContent(DownloadController controller, URLRequest request, InputStream stream) throws ExecutionException;
}
