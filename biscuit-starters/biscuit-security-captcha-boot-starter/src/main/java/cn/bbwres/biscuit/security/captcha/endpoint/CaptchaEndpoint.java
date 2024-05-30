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

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cn.bbwres.biscuit.dto.Result;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 验证码端点
 *
 * @author zhanglinfeng
 */
@RequestMapping("/captcha")
public class CaptchaEndpoint {

    private ImageCaptchaApplication imageCaptchaApplication;

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
    public Result<?> createCaptcha(@RequestParam(value = "type", required = false) String type) {
        if (ObjectUtils.isEmpty(type)) {
            type = CaptchaTypeConstant.SLIDER;
        }
        CaptchaResponse<ImageCaptchaVO> res1 = imageCaptchaApplication.generateCaptcha(type);
        return Result.success(res1);
    }

    /**
     * 检查图形验证码
     *
     * @param captchaId
     * @param sliderCaptchaTrack
     * @return
     */
    @PostMapping("/checkCaptcha/{captchaId}")
    @ResponseBody
    public Result<Boolean> checkCaptcha(@PathVariable("captchaId") String captchaId, @RequestBody ImageCaptchaTrack sliderCaptchaTrack) {
        ApiResponse<?> check = imageCaptchaApplication.matching(captchaId, sliderCaptchaTrack);
        return Result.success(check.isSuccess());
    }


}
