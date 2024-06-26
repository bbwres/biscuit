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

package cn.bbwres.biscuit.i18n.support;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

/**
 * 国际化支持类
 *
 * @author zhanglinfeng
 */
public class SupportMessageSource implements MessageSource {

    private final List<MessageSource> messageSources;


    public SupportMessageSource(List<MessageSource> messageSources) {
        this.messageSources = messageSources;
    }


    /**
     * Try to resolve the message. Return default message if no message was found.
     *
     * @param code           the message code to look up, e.g. 'calculator.noRateSet'.
     *                       MessageSource users are encouraged to base message names on qualified class
     *                       or package names, avoiding potential conflicts and ensuring maximum clarity.
     * @param args           an array of arguments that will be filled in for params within
     *                       the message (params look like "{0}", "{1,date}", "{2,time}" within a message),
     *                       or {@code null} if none
     * @param defaultMessage a default message to return if the lookup fails
     * @param locale         the locale in which to do the lookup
     * @return the resolved message if the lookup was successful, otherwise
     * the default message passed as a parameter (which may be {@code null})
     * @see #getMessage(MessageSourceResolvable, Locale)
     * @see MessageFormat
     */
    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return getMessageBySource(code, args, defaultMessage, locale);
    }

    /**
     * 可以支持多个MessageSource的国际化
     *
     * @param code
     * @param args
     * @param defaultMessage
     * @param locale
     * @return
     */
    protected String getMessageBySource(String code, Object[] args, String defaultMessage, Locale locale) {
        String message = null;
        for (MessageSource messageSource : messageSources) {
            message = messageSource.getMessage(code, args, null, locale);
            if (!ObjectUtils.isEmpty(message)) {
                return message;
            }
        }
        return ObjectUtils.isEmpty(message) ? defaultMessage : message;
    }

    /**
     * Try to resolve the message. Treat as an error if the message can't be found.
     *
     * @param code   the message code to look up, e.g. 'calculator.noRateSet'.
     *               MessageSource users are encouraged to base message names on qualified class
     *               or package names, avoiding potential conflicts and ensuring maximum clarity.
     * @param args   an array of arguments that will be filled in for params within
     *               the message (params look like "{0}", "{1,date}", "{2,time}" within a message),
     *               or {@code null} if none
     * @param locale the locale in which to do the lookup
     * @return the resolved message (never {@code null})
     * @throws NoSuchMessageException if no corresponding message was found
     * @see #getMessage(MessageSourceResolvable, Locale)
     * @see MessageFormat
     */
    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return getMessageBySource(code, args, null, locale);
    }

    /**
     * Try to resolve the message using all the attributes contained within the
     * {@code MessageSourceResolvable} argument that was passed in.
     * <p>NOTE: We must throw a {@code NoSuchMessageException} on this method
     * since at the time of calling this method we aren't able to determine if the
     * {@code defaultMessage} property of the resolvable is {@code null} or not.
     *
     * @param resolvable the value object storing attributes required to resolve a message
     *                   (may include a default message)
     * @param locale     the locale in which to do the lookup
     * @return the resolved message (never {@code null} since even a
     * {@code MessageSourceResolvable}-provided default message needs to be non-null)
     * @throws NoSuchMessageException if no corresponding message was found
     *                                (and no default message was provided by the {@code MessageSourceResolvable})
     * @see MessageSourceResolvable#getCodes()
     * @see MessageSourceResolvable#getArguments()
     * @see MessageSourceResolvable#getDefaultMessage()
     * @see MessageFormat
     */
    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        String message = null;
        String defaultMessage = resolvable.getDefaultMessage();
        for (MessageSource messageSource : messageSources) {
            message = messageSource.getMessage(resolvable, locale);
            if (!ObjectUtils.isEmpty(message)) {
                return message;
            }
        }
        return ObjectUtils.isEmpty(message) ? defaultMessage : message;
    }
}
