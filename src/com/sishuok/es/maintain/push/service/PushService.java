package com.sishuok.es.maintain.push.service;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

@Service
public class PushService {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private volatile Map<Long, Queue<DeferredResult<Object>>> userIdToDeferredResultMap = new ConcurrentHashMap();

    public boolean isOnline(final Long userId) {
        return userIdToDeferredResultMap.containsKey(userId);
    }

    /**
     * ���ߺ� ����һ���ն��У���ֹ����ж�
     * @param userId
     */
    public void online(final Long userId) {
        Queue<DeferredResult<Object>> queue = userIdToDeferredResultMap.get(userId);
        if (queue == null) {
            //���jdk 1.7 ���Ի���ConcurrentLinkedQueue
            queue = new LinkedBlockingDeque<DeferredResult<Object>>();
            userIdToDeferredResultMap.put(userId, queue);
        }
    }

    public void offline(final Long userId) {

        Queue<DeferredResult<Object>> queue = userIdToDeferredResultMap.remove(userId);
        if (queue != null) {
            for (DeferredResult<Object> result : queue) {
                try {
                    result.setResult("");
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public DeferredResult newDeferredResult(final Long userId) {
        final DeferredResult<Object> deferredResult = new DeferredResult<Object>();
        deferredResult.onCompletion(new Runnable() {
            @Override
            public void run() {
                Queue<DeferredResult<Object>> queue = userIdToDeferredResultMap.get(userId);
                if (queue != null) {
                    queue.remove(deferredResult);
                    deferredResult.setResult("");
                }
            }
        });
        deferredResult.onTimeout(new Runnable() {
            @Override
            public void run() {
                deferredResult.setErrorResult("");
            }
        });
        Queue<DeferredResult<Object>> queue = userIdToDeferredResultMap.get(userId);
        if (queue == null) {
            queue = new LinkedBlockingDeque<DeferredResult<Object>>();
            userIdToDeferredResultMap.put(userId, queue);
        }
        queue.add(deferredResult);

        return deferredResult;
    }

    public void push(final Long userId, final Object data) {
        Queue<DeferredResult<Object>> queue = userIdToDeferredResultMap.get(userId);
        if (queue == null) {
            return;
        }
        for (DeferredResult<Object> deferredResult : queue) {
            if (!deferredResult.isSetOrExpired()) {
                try {
                    deferredResult.setResult(data);
                } catch (Exception e) {
                    queue.remove(deferredResult);
                }
            }
        }
    }

    /**
     * ������ն��� ��ֹ�м�������Ϣʱ�ж������Ϣ��ʧ
     */
    @Scheduled(fixedRate = 5L * 60 * 1000)
    public void sync() {
        Map<Long, Queue<DeferredResult<Object>>> oldMap = userIdToDeferredResultMap;
        userIdToDeferredResultMap = new ConcurrentHashMap<Long, Queue<DeferredResult<Object>>>();
        for (Queue<DeferredResult<Object>> queue : oldMap.values()) {
            if (queue == null) {
                continue;
            }

            for (DeferredResult<Object> deferredResult : queue) {
                try {
                    deferredResult.setResult("");
                } catch (Exception e) {
                    queue.remove(deferredResult);
                }
            }

        }
    }

}
