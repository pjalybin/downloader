package downloader.impl;

import downloader.*;

import java.util.concurrent.*;

/**
 * @author pjalybin
 * @since 23.08.14 11:39
 */
public class HttpDownloadManager implements DownloadManager {

    private final ExecutorService executorService;

    private final Semaphore parallelDownloadSemaphore;

    public HttpDownloadManager() {
        this(1);
    }
    public HttpDownloadManager(int maxParallelDownloads) {
        this(maxParallelDownloads, maxParallelDownloads*2);
    }
    public HttpDownloadManager(int maxParallelDownloads, int workingThreads) {
        parallelDownloadSemaphore = new Semaphore(maxParallelDownloads, true);
        executorService = new ThreadPoolExecutor(
                maxParallelDownloads,
                workingThreads,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    @Override
    public DownloadRequestBuilder download() {
        return new HttpDownloadRequestBuilder(this);
    }

    @Override
    public DownloadController download(DownloadRequest request) {
        HttpDownloadTask task = new HttpDownloadTask(parallelDownloadSemaphore);
        HttpDownloadController controller = new HttpDownloadController(this, request, task);
        task.setController(controller);
        Future<DownloadResponse> future = executorService.submit(task);
        controller.setFuture(future);
        return controller;
    }

    @Override
    public void close() throws Exception {
        executorService.shutdown();
    }
}
