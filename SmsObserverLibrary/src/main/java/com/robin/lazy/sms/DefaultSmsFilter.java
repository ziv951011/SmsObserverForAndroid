

package com.robin.lazy.sms;

/**
 * 默认的短信过滤器
 */
public class DefaultSmsFilter implements SmsFilter{

    @Override
    public String filter(String address, String smsContent) {
        return smsContent;
    }
}
