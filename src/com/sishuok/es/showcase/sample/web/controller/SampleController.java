package com.sishuok.es.showcase.sample.web.controller;

import java.util.Date;

import org.es.framework.common.entity.enums.BooleanEnum;
import org.es.framework.common.web.controller.BaseCRUDController;
import org.es.framework.common.web.validate.ValidateResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sishuok.es.showcase.sample.entity.Sample;
import com.sishuok.es.showcase.sample.entity.Sex;
import com.sishuok.es.showcase.sample.service.SampleService;

@Controller
@RequestMapping(value = "/showcase/sample")
public class SampleController extends BaseCRUDController<Sample, Long> {

    private SampleService getSampleService() {
        return (SampleService) baseService;
    }

    public SampleController() {
        setResourceIdentity("showcase:sample");
    }

    @Override
    protected void setCommonData(Model model) {
        model.addAttribute("sexList", Sex.values());
        model.addAttribute("booleanList", BooleanEnum.values());
    }

    /**
     * ��֤ʧ�ܷ���true
     *
     * @param m
     * @param result
     * @return
     */
    @Override
    protected boolean hasError(Sample m, BindingResult result) {
        Assert.notNull(m);

        //�ֶδ��� ǰ̨ʹ��<es:showFieldError commandName="showcase/sample"/> ��ʾ
        if (m.getBirthday() != null && m.getBirthday().after(new Date())) {
            //ǰ̨�ֶ�����ǰ̨ʹ��[name=�ֶ���]ȡ��dom���� ������Ϣ������
            result.rejectValue("birthday", "birthday.past");
        }

        //ȫ�ִ��� ǰ̨ʹ��<es:showGlobalError commandName="showcase/sample"/> ��ʾ
        if (m.getName().contains("admin")) {
            result.reject("name.must.not.admin");
        }

        return result.hasErrors();
    }

    /**
     * ��֤���ظ�ʽ
     * ������[fieldId, 1|0, msg]
     * �����[[fieldId, 1|0, msg],[fieldId, 1|0, msg]]
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

        if ("name".equals(fieldId)) {
            Sample sample = getSampleService().findByName(fieldValue);
            if (sample == null
                || (sample.getId().equals(id) && sample.getName().equals(fieldValue))) {
                //���msg ��Ϊ�� ��������ʾ��
                response.validateSuccess(fieldId, "");
            } else {
                response.validateFail(fieldId, "�������ѱ�������ʹ��");
            }
        }
        return response.result();
    }

}
