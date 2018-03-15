package org.es.framework.common.utils.fetch;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: RemoteFileFetcher.java, v 0.1 2014年11月19日 上午11:15:53 kejun.song Exp $
 */
public class RemoteFileFetcher {

    private static final Logger                   LOGGER                   = LoggerFactory
                                                                               .getLogger(RemoteFileFetcher.class);

    private byte[]                                fileConent;
    private final String                          url;
    private long                                  lastModified;
    @SuppressWarnings("unused")
    private final int                             connectTimeout;
    @SuppressWarnings("unused")
    private final int                             readTimeout;
    private final FileChangeListener              listener;

    private static final ScheduledExecutorService scheduledExecutorService = Executors
                                                                               .newSingleThreadScheduledExecutor(new ThreadFactory() {
                                                                                   @Override
                                                                                   public Thread newThread(Runnable r) {
                                                                                       return new Thread(
                                                                                           r,
                                                                                           "RemoteFileFetcher_Schedule_Thread");
                                                                                   }
                                                                               });

    private RemoteFileFetcher(String url, int reloadInterval, FileChangeListener listener) {
        this.connectTimeout = 1000;
        this.readTimeout = 1000;

        this.url = url;
        this.listener = listener;
        if (reloadInterval > 0) {
            scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    RemoteFileFetcher.this.doFetch();
                }
            }, reloadInterval, reloadInterval, TimeUnit.MILLISECONDS);
        }
        doFetch();
    }

    private void doFetch() {
        if (url == null) {
            return;
        }
        LOGGER.info("Begin fetch remote file... url = {}", this.url);
        try {
            URL target = new URL(this.url);
            this.fileConent = IOUtils.toByteArray(target);
            this.lastModified = System.currentTimeMillis();
            if (this.listener != null && this.fileConent != null) {
                this.listener.fileReloaded(this.fileConent);
            }
        } catch (Exception e) {
            LOGGER.error("read from url failed", e);
        }
    }

    public static RemoteFileFetcher createPeriodFetcher(String url, int reloadInterval,
                                                        FileChangeListener listener) {

        return new RemoteFileFetcher(url, reloadInterval, listener);

    }

    public long getLastModified() {
        return this.lastModified;
    }

    public byte[] getFileByteArray() {
        return this.fileConent;
    }

    public interface FileChangeListener {
        public abstract void fileReloaded(byte[] contentBytes) throws IOException;
    }
}
