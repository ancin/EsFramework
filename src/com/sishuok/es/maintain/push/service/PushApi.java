/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.maintain.push.service;

import java.util.List;
import java.util.Map;

public interface PushApi {

    public void pushUnreadMessage(final Long userId, Long unreadMessageCount);

    public void pushNewNotification(final Long userId, List<Map<String, Object>> notifiations);

    void offline(Long userId);
}
