package downloader.impl;

import downloader.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author pjalybin
 * @since 23.08.14 11:54
 */
public class HttpDownloadController implements DownloadController {

    private final DownloadRequest downloadRequest;
    private final HttpDownloadTask downloadTask;
    private final DownloadManager manager;
    private Future<DownloadResponse> future;

    public HttpDownloadController(DownloadManager manager, DownloadRequest request, HttpDownloadTask downloadTask) {
        this.downloadRequest = request;
        this.downloadTask = downloadTask;
        this.manager = manager;
    }

    @Override
    public DownloadRequest getRequest() {
        return downloadRequest;
    }

    @Override
    public void pause() {
        downloadTask.pause();
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        if(mayInterruptIfRunning)downloadTask.stop();
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public DownloadResponse get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Override
    public DownloadResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    @Override
    public DownloadManager getManager() {
        return manager;
    }

    @Override
    public void restart() {
        downloadTask.restart();
    }

    @Override
    public void resume() {
        downloadTask.resume();
    }

    @Override
    public void stop() {
        downloadTask.stop();
        future.cancel(true);
    }

    @Override
    public List<URLResponse> getCompletedResponses() {
        return downloadTask.getCompletedResponses();
    }

    @Override
    public DownloadInfo getCurrentDownloadInfo() {
        return downloadTask.getCurrentDownloadInfo();
    }

    void setFuture(Future<DownloadResponse> future) {
        this.future = future;
    }
}
