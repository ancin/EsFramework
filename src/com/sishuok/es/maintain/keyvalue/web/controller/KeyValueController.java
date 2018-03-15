package com.sishuok.es.maintain.keyvalue.web.controller;

import org.es.framework.common.web.controller.BaseCRUDController;
import org.es.framework.common.web.validate.ValidateResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sishuok.es.maintain.keyvalue.entity.KeyValue;
import com.sishuok.es.maintain.keyvalue.service.KeyValueService;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: KeyValueController.java, v 0.1 2014年11月19日 下午3:13:09 kejun.song Exp $
 */
@Controller
@RequestMapping(value = "/admin/maintain/keyvalue")
public class KeyValueController extends BaseCRUDController<KeyValue, Long> {

    private KeyValueService getKeyValueService() {
        return (KeyValueService) baseService;
    }

    public KeyValueController() {
        setResourceIdentity("maintain:icon");
    }

    /**
     * 验证返回格式
     * 单个：[fieldId, 1|0, msg]
     * 多个：[[fieldId, 1|0, msg],[fieldId, 1|0, msg]]
     *
     * @param fieldId
     * @param fieldValue
     * @return
     */
    @RequestMapping(value = "validate", method = RequestMethod.GET)
    @ResponseBody
    public Object validate(@RequestParam("fieldId") String fieldId,
                           @RequestParam("fieldValue") String fieldValue,
                           @RequestParam(value = "id", required = false) Long id) {
        ValidateResponse response = ValidateResponse.newInstance();

        if ("key".equals(fieldId)) {
            KeyValue keyValue = getKeyValueService().findByKey(fieldValue);
            if (keyValue == null
                || (keyValue.getId().equals(id) && keyValue.getKey().equals(fieldValue))) {
                //如果msg 不为空 将弹出提示框
                response.validateSuccess(fieldId, "");
            } else {
                response.validateFail(fieldId, "该键已被使用");
            }
        }
        return response.result();
    }

}
