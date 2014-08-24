package downloader.impl;

import downloader.State;

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Callable task which can be temporary paused
 * Running task acquires semaphore.
 * Paused task releases semaphore.
 *
 * @author pjalybin
 * @since 22.08.14 16:33
 */
public abstract class PauseableCallableWithSemaphore<T> implements Callable<T> {
    /**
     * flag of pause request
     */
    private boolean pausedFlag = false;
    /**
     * lock to synchronize pause states
     */
    private final ReentrantLock pauseLock = new ReentrantLock();
    /**
     * Condition for waiting on pause
     */
    private final Condition resumePause = pauseLock.newCondition();
    /**
     * Provided semaphore
     */
    private final Semaphore semaphore;

    /**
     * State of task lifecycle
     */
    private volatile State state = State.NEW;

    /**
     * Executing Thread
     */
    private volatile Thread runningThread;

    protected PauseableCallableWithSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    @Override
    public final T call() throws Exception {
        if (state != State.NEW) throw new IllegalStateException();
        runningThread = Thread.currentThread();
        state = State.WAITING;
        try {
            semaphore.acquire();
            state = State.RUNNING;
            try {
                T result = execute();
                state = State.FINISHED;
                return result;
            } finally {
                semaphore.release();
            }
        } catch (Throwable e) {
            state = State.FAILED;
            throw e;
        }
    }

    protected abstract T execute() throws Exception;

    /**
     * check pause flag and wait if it is raised
     * releases semaphore when waiting
     *
     * @throws InterruptedException thread interrupted
     */
    protected void waitPause() throws InterruptedException {
        if (state == State.RUNNING) {
            pauseLock.lockInterruptibly();
            try {
                if (pausedFlag) {
                    semaphore.release();
                    state = State.PAUSED;
                    while (pausedFlag) {
                        resumePause.await();
                    }
                    state = State.WAITING;
                    semaphore.acquire();
                    state = State.RUNNING;
                }
            } finally {
                pauseLock.unlock();
            }
        }
        if (Thread.interrupted()) throw new InterruptedException();
    }

    /**
     * ask task to pause
     */
    public void pause() {
        pauseLock.lock();
        try {
            pausedFlag = true;
        } finally {
            pauseLock.unlock();
        }
    }

    /**
     * ask task to resume pause waiting
     */
    public void resume() {
        pauseLock.lock();
        try {
            pausedFlag = false;
            resumePause.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }

    /**
     * @return state of task lifecycle
     */
    public State getState() {
        return state;
    }

    /**
     * @return running thread of task
     */
    protected Thread getRunningThread() {
        return runningThread;
    }

}
