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

package cn.bbwres.biscuit.security.captcha.endpoint;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 验证码端点
 *
 * @author zhanglinfeng
 */
@Controller
@RequestMapping("/captcha")
public class CaptchaEndpoint {

    private final ImageCaptchaApplication imageCaptchaApplication;

    public CaptchaEndpoint(ImageCaptchaApplication imageCaptchaApplication) {
        this.imageCaptchaApplication = imageCaptchaApplication;
    }

    /**
     * 创建图形验证码
     *
     * @return
     */
    @GetMapping("/create")
    @ResponseBody
    public ApiResponse<ImageCaptchaVO> createCaptcha(@RequestParam(value = "type", required = false) String type) {
        if (ObjectUtils.isEmpty(type)) {
            type = CaptchaTypeConstant.SLIDER;
        }
        return imageCaptchaApplication.generateCaptcha(type);
    }


    /**
     * 检查图形验证码
     *
     * @param data
     * @return
     */
    @PostMapping("/checkCaptcha")
    @ResponseBody
    public ApiResponse<?> checkCaptcha(@RequestBody ImageCaptchaTrackData data) {
        return imageCaptchaApplication.matching(data.id, data.imageCaptchaTrack);
    }


    public record ImageCaptchaTrackData(String id, ImageCaptchaTrack imageCaptchaTrack) {
    }


}
