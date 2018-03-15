package com.sishuok.es.monitor.web.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.es.framework.common.utils.PrettyMemoryUtils;
import org.es.framework.common.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@SuppressWarnings("rawtypes")
@Controller
@RequestMapping("/admin/monitor/ehcache")
@RequiresPermissions("monitor:ehcache:*")
public class EhcacheMonitorController extends BaseController {

    @Autowired
    private CacheManager cacheManager;

    @RequestMapping()
    public String index(Model model) {
        model.addAttribute("cacheManager", cacheManager);
        return viewName("index");
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("{cacheName}/details")
    public String details(@PathVariable("cacheName") String cacheName,
                          @RequestParam(value = "searchText", required = false, defaultValue = "") String searchText,
                          Model model) {

        model.addAttribute("cacheName", cacheName);
        List allKeys = cacheManager.getCache(cacheName).getKeys();

        List showKeys = Lists.newArrayList();

        for (Object key : allKeys) {
            if (key.toString().contains(searchText)) {
                showKeys.add(key);
            }
        }

        model.addAttribute("keys", showKeys);

        return viewName("details");
    }

    @RequestMapping("{cacheName}/{key}/details")
    @ResponseBody
    public Object keyDetail(@PathVariable("cacheName") String cacheName,
                            @PathVariable("key") String key, Model model) {

        Element element = cacheManager.getCache(cacheName).get(key);

        String dataPattern = "yyyy-MM-dd hh:mm:ss";
        Map<String, Object> data = Maps.newHashMap();
        data.put("objectValue", element.getObjectValue().toString());
        data.put("size", PrettyMemoryUtils.prettyByteSize(element.getSerializedSize()));
        data.put("hitCount", element.getHitCount());

        Date latestOfCreationAndUpdateTime = new Date(element.getLatestOfCreationAndUpdateTime());
        data.put("latestOfCreationAndUpdateTime",
            DateFormatUtils.format(latestOfCreationAndUpdateTime, dataPattern));
        Date lastAccessTime = new Date(element.getLastAccessTime());
        data.put("lastAccessTime", DateFormatUtils.format(lastAccessTime, dataPattern));
        if (element.getExpirationTime() == Long.MAX_VALUE) {
            data.put("expirationTime", "������");
        } else {
            Date expirationTime = new Date(element.getExpirationTime());
            data.put("expirationTime", DateFormatUtils.format(expirationTime, dataPattern));
        }

        data.put("timeToIdle", element.getTimeToIdle());
        data.put("timeToLive", element.getTimeToLive());
        data.put("version", element.getVersion());

        return data;

    }

    @RequestMapping("{cacheName}/{key}/delete")
    @ResponseBody
    public Object delete(@PathVariable("cacheName") String cacheName,
                         @PathVariable("key") String key) {

        Cache cache = cacheManager.getCache(cacheName);

        cache.remove(key);

        return "�����ɹ���";

    }

    @RequestMapping("{cacheName}/clear")
    @ResponseBody
    public Object clear(@PathVariable("cacheName") String cacheName) {

        Cache cache = cacheManager.getCache(cacheName);
        cache.clearStatistics();
        cache.removeAll();

        return "�����ɹ���";

    }

}