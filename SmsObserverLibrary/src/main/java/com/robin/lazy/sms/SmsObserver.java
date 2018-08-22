package com.robin.lazy.sms;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 短信接收观察者
 */
public class SmsObserver extends ContentObserver {

    private Context mContext;
    public static final int MSG_RECEIVED_CODE = 1001;
    private SmsHandler mHandler;

    /***
     * 构造器
     * @param context
     * @param callback 短信接收器
     * @param smsFilter 短信过滤器
     */
    public SmsObserver(Activity context, SmsResponseCallback callback,SmsFilter smsFilter) {
        this(new SmsHandler(callback,smsFilter));
        this.mContext = context;
    }

    public SmsObserver(Activity context, SmsResponseCallback callback) {
        this(new SmsHandler(callback));
        this.mContext = context;
    }

    public SmsObserver(SmsHandler handler) {
        super(handler);
        this.mHandler = handler;
    }

    /***
     * 设置短信过滤器
     * @param smsFilter
     */
    public void setSmsFilter(SmsFilter smsFilter) {
        mHandler.setSmsFilter(smsFilter);
    }

    /***
     * 注册短信变化观察者
     *
     * @see [类、类#方法、类#成员]
     */
    public void registerSMSObserver() {
        Uri uri = Uri.parse("content://sms");
        if (mContext != null) {
            mContext.getContentResolver().registerContentObserver(uri,
                    true, this);
        }
    }

    /***
     * 注销短信变化观察者
     *
     * @see [类、类#方法、类#成员]
     */
    public void unregisterSMSObserver() {
        if (mContext != null) {
            mContext.getContentResolver().unregisterContentObserver(this);
        }
        if (mHandler != null) {
            mHandler = null;
        }
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        if (uri.toString().equals("content://sms/raw")) {
            return;
        }
        Uri inboxUri = Uri.parse("content://sms/inbox");//收件箱
        try {
            Cursor cursor = mContext.getContentResolver().query(inboxUri, null, null, null, null);
            if (cursor.moveToFirst()) {
                String body = cursor.getString(cursor.getColumnIndex("body"));
                String address = cursor.getString(cursor.getColumnIndex("address"));
                Log.i(getClass().getName(), "发件人为：" + address + " " + "短信内容为：" + body);
                mHandler.obtainMessage(MSG_RECEIVED_CODE, new String[]{address, getDynamicPassword(body)})
                        .sendToTarget();
            }
        } catch (SecurityException e) {
            Log.e(getClass().getName(), "获取短信权限失败", e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getDynamicPassword(String str) {
        Pattern continuousNumberPattern = Pattern.compile("[0-9\\.]+");
        Matcher m = continuousNumberPattern.matcher(str);
        String dynamicPassword = "";
        while(m.find()){
            if(m.group().length() == 6) {
                System.out.print(m.group());
                dynamicPassword = m.group();
            }
        }

        return dynamicPassword;
    }

}
