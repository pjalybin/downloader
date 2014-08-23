package downloader.impl;

import downloader.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

/**
 * @author pjalybin
 * @since 23.08.14 13:02
 */
class HttpDownloadTask extends PauseableCallableWithSemaphore<DownloadResponse> {

    private DownloadController downloadController;
    private final List<URLResponse> completedResponses = Collections.synchronizedList(new ArrayList<URLResponse>());
    private volatile boolean restartFlag = false;

    private volatile int currentContentLength;
    private volatile int currentContentDownloaded;
    private volatile URLRequest currentRequest;

    HttpDownloadTask(Semaphore semaphore) {
        super(semaphore);
    }

    @Override
    public DownloadResponse execute() throws Exception {
        DownloadRequest downloadRequest = downloadController.getRequest();
        DownloadHandler downloadHandler = downloadRequest.getHandler();
        boolean firstTime = true;
        while (firstTime || restartFlag) {
            try {
                firstTime = false;
                restartFlag = false;
                completedResponses.clear();
                if (downloadHandler != null) downloadHandler.onDownloadStarted(downloadController);

                for (URLRequest urlRequest : downloadRequest.getURLRequests()) {
                    currentContentLength = 0;
                    currentContentDownloaded = 0;
                    currentRequest = urlRequest;
                    HttpURLResponse response = new HttpURLResponse(urlRequest);
                    try {
                        if (downloadController.isCancelled()) {
                            stop();
                        }
                        checkInterrupted();
                        if (downloadHandler != null) {
                            boolean stop = downloadHandler.onDownloadStartedURL(downloadController, urlRequest);
                            if (stop) throw new UserBreakException();
                        }
                        HttpURLConnection connection = connect(urlRequest);
                        try {
                            downloadStatus(connection, downloadHandler, urlRequest, response);
                            downloadHeader(connection, downloadHandler, urlRequest, response);
                            downloadContent(connection, downloadHandler, urlRequest, response);
                            response.setException(null); //clean response exception
                        } finally {
                            connection.disconnect();
                        }
                        checkInterrupted();
                        if (downloadHandler != null) {
                            downloadHandler.onDownloadFinishedURL(downloadController, response);
                            checkInterrupted();
                        }
                    } catch (Throwable e) {
                        boolean stop = false;
                        if (downloadHandler != null) {
                            stop = downloadHandler.onDownloadFailedURL(downloadController, urlRequest, e);
                        }
                        response.setException(e);
                        if (Thread.currentThread().isInterrupted() || stop) {
                            throw e;
                        }
                    } finally {
                        completedResponses.add(response);
                    }
                }
            } catch (Throwable e) {
                if (downloadHandler != null) {
                    downloadHandler.onDownloadFailed(downloadController, e);
                }
                if (Thread.interrupted()) { // clear interrupt flag
                    if (!restartFlag) {
                        Thread.currentThread().interrupt(); // set interrupt flag
                        throw e;
                    } else {
                        // restart and continue work
                        continue;
                    }
                } else {
                    throw e;
                }
            }
            HttpDownloadResponse downloadResponse = new HttpDownloadResponse(downloadRequest, new ArrayList<>(completedResponses));
            if (downloadHandler != null) {
                downloadHandler.onDownloadFinished(downloadController, downloadResponse);
            }
            checkInterrupted();
            return downloadResponse;
        }
        // never be reached
        throw new RuntimeException("Unreachable");
    }

    private void checkInterrupted() throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
    }

    private HttpURLConnection connect(URLRequest urlRequest) throws InterruptedException, IOException {
        waitPause();
        URLConnection urlConnection = urlRequest.getURL().openConnection();
        if (!(urlConnection instanceof HttpURLConnection))
            throw new IOException("Only http download is supported");

        HttpURLConnection connection = (HttpURLConnection) urlConnection;
        connection.setConnectTimeout(downloadController.getRequest().getConnectionTimeout());
        connection.connect();
        return connection;
    }

    public void restart() {
        restartFlag = true;
        Thread runningThread = getRunningThread();
        if (runningThread != null) {
            getRunningThread().interrupt();
        }
    }

    void stop() {
        restartFlag = false;
        Thread runningThread = getRunningThread();
        if (runningThread != null) {
            runningThread.interrupt();
        }
    }

    private void downloadContent(HttpURLConnection connection, DownloadHandler downloadHandler, URLRequest urlRequest, HttpURLResponse response) throws IOException, InterruptedException, UserBreakException, ExecutionException {
        waitPause();
        InputStream inputStream = connection.getInputStream();
        if (downloadHandler instanceof HttpDownloadHandler) {
            boolean stop = ((HttpDownloadHandler) downloadHandler).onContent(downloadController, urlRequest, inputStream);
            if (stop) throw new UserBreakException();
        }
        checkInterrupted();
        int contentLength = connection.getContentLength();
        currentContentLength = contentLength;
        response.setContentLength(contentLength);
        if (contentLength < 0)
            throw new IOException("Content length is unknown. Try to download by yourself with handler");
        ByteBuffer byteBuffer = downloadStream(inputStream, contentLength);
        response.setData(byteBuffer.array());
    }

    private void downloadHeader(HttpURLConnection connection, DownloadHandler downloadHandler, URLRequest urlRequest, HttpURLResponse response) throws InterruptedException, UserBreakException, ExecutionException {
        waitPause();
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        HttpResponseHeader httpResponseHeader = new HttpResponseHeader(headerFields);
        response.setHeader(httpResponseHeader);
        if (downloadHandler instanceof HttpDownloadHandler) {
            boolean stop = ((HttpDownloadHandler) downloadHandler).onHeader(downloadController, urlRequest, httpResponseHeader);
            if (stop) throw new UserBreakException();
        }
    }

    private int downloadStatus(HttpURLConnection connection, DownloadHandler downloadHandler, URLRequest urlRequest, HttpURLResponse response) throws IOException, InterruptedException, UserBreakException, ExecutionException {
        waitPause();
        int statusCode = connection.getResponseCode();
        response.setStatusCode(statusCode);
        if (downloadHandler instanceof HttpDownloadHandler) {
            boolean stop = ((HttpDownloadHandler) downloadHandler).onHttpStatus(downloadController, urlRequest, statusCode);
            if (stop) throw new UserBreakException();
        }
        return statusCode;
    }

    private ByteBuffer downloadStream(InputStream inputStream, int contentLength) throws IOException, InterruptedException {
        ReadableByteChannel channel = Channels.newChannel(inputStream);
        ByteBuffer buffer = ByteBuffer.allocate(contentLength);
        int bytesRead = 0;
        while (bytesRead >= 0 && bytesRead < buffer.limit()) {
            waitPause();
            checkInterrupted();
            int count = channel.read(buffer);
            if (count > 0) {
                bytesRead += count;
                currentContentDownloaded = bytesRead;
            }
            if (count < 0)
                throw new IOException("Premature end of stream");
        }
        buffer.flip();
        return buffer;
    }

    public List<URLResponse> getCompletedResponses() {
        return new ArrayList<>(completedResponses);
    }

    public DownloadInfo getCurrentDownloadInfo() {
        return new HttpDownloadInfo(
                currentRequest,
                currentContentLength,
                currentContentDownloaded,
                getState()
        );
    }

    void setController(HttpDownloadController controller) {
        this.downloadController = controller;
    }


    private static class UserBreakException extends Exception {
    }


}
