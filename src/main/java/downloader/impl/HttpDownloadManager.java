package downloader.impl;

import downloader.*;

import java.util.concurrent.*;

/**
 * Download manager implementation on executor service.
 * @author pjalybin
 * @since 23.08.14 11:39
 */
public class HttpDownloadManager implements DownloadManager {
    /**
     * Executor service for parallel processing of download tasks
     */
    private final ExecutorService executorService;

    /**
     * Semaphore which used to limit number of parallel download
     * Paused task release semaphore but still use a running thread
     */
    private final Semaphore parallelDownloadSemaphore;

    /**
     * Single thread downloader
     * Paused task blocks all other download tasks
     */
    public HttpDownloadManager() {
        this(1, 1);
    }

    /**
     * Download manager for parallel downloading.
     * It safe to pause up to {@code maxParallelDownloads} tasks.
     * Pause double {@code maxParallelDownloads} tasks will block download
     *
     * @param maxParallelDownloads maximum number of simultaneous downloads
     */
    public HttpDownloadManager(int maxParallelDownloads) {
        this(maxParallelDownloads, maxParallelDownloads*2);
    }

    /**
     * Download manager for parallel downloading.
     * Pause all of working threads will block download
     * @param maxParallelDownloads maximum number of simultaneous downloads
     * @param workingThreads number of threads for paused and running download tasks. Must be greater or equal to maxParallelDownloads
     */
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
