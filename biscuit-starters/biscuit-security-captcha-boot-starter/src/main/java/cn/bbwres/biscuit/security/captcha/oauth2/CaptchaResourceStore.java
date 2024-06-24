/*
 *
 *  * Copyright 2024 bbwres
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package cn.bbwres.biscuit.security.captcha.oauth2;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.common.constant.SliderCaptchaConstant;
import cloud.tianai.captcha.generator.impl.StandardSliderImageCaptchaGenerator;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import cloud.tianai.captcha.resource.impl.DefaultResourceStore;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;

import static cloud.tianai.captcha.generator.impl.StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH;

/**
 * 验证码资源信息
 * @author zhanglinfeng
 */
public class CaptchaResourceStore extends DefaultResourceStore {


    public CaptchaResourceStore() {

        // 滑块验证码 模板 (系统内置)
        ResourceMap template1 = new ResourceMap("default",4);
        template1.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/active.png")));
        template1.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/fixed.png")));
        ResourceMap template2 = new ResourceMap("default",4);
        template2.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/active.png")));
        template2.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/fixed.png")));
        // 旋转验证码 模板 (系统内置)
        ResourceMap template3 = new ResourceMap("default",4);
        template3.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/active.png")));
        template3.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/fixed.png")));

        // 1. 添加一些模板
        addTemplate(CaptchaTypeConstant.SLIDER, template1);
        addTemplate(CaptchaTypeConstant.SLIDER, template2);
        addTemplate(CaptchaTypeConstant.ROTATE, template3);

        // 2. 添加自定义背景图片
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "bgimages/a.jpg","default"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "bgimages/b.jpg","default"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "bgimages/c.jpg","default"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "bgimages/d.jpg","default"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "bgimages/e.jpg","default"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "bgimages/g.jpg","default"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "bgimages/h.jpg","default"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "bgimages/i.jpg","default"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "bgimages/j.jpg","default"));
        addResource(CaptchaTypeConstant.ROTATE, new Resource("classpath", "bgimages/48.jpg","default"));
        addResource(CaptchaTypeConstant.CONCAT, new Resource("classpath", "bgimages/48.jpg","default"));
        addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource("classpath", "bgimages/c.jpg","default"));
    }
}
