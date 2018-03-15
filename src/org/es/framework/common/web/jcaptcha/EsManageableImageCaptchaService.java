package org.es.framework.common.web.jcaptcha;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: EsManageableImageCaptchaService.java, v 0.1 2014年11月19日 上午11:37:39 kejun.song Exp $
 */
public class EsManageableImageCaptchaService extends DefaultManageableImageCaptchaService {

    public EsManageableImageCaptchaService(com.octo.captcha.service.captchastore.CaptchaStore captchaStore,
                                           com.octo.captcha.engine.CaptchaEngine captchaEngine,
                                           int minGuarantedStorageDelayInSeconds,
                                           int maxCaptchaStoreSize,
                                           int captchaStoreLoadBeforeGarbageCollection) {
        super(captchaStore, captchaEngine, minGuarantedStorageDelayInSeconds, maxCaptchaStoreSize,
            captchaStoreLoadBeforeGarbageCollection);
    }

    public boolean hasCapcha(String id, String userCaptchaResponse) {
        return store.getCaptcha(id).validateResponse(userCaptchaResponse);
    }
}
