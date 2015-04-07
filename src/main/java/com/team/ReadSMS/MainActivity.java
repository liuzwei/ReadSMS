package com.team.ReadSMS;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView textView;
    private SmsObserver smsObserver;
    private static final String SMS_INBOX = "content://sms";

    private static String checkId = "";

    private Handler smsHandler = new Handler(){

    };
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView) this.findViewById(R.id.text_view);
//        readShortMessage();
        smsObserver = new SmsObserver(this,smsHandler);
        getContentResolver().registerContentObserver(Uri.parse(SMS_INBOX), true,
                smsObserver);
    }

    public void readShortMessage() {
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(Uri.parse("content://sms/sent"), null, null, null, null);
        String msg = "";
        while(cursor.moveToNext()) {
            int phoneColumn = cursor.getColumnIndex("address");
            int smsColumn = cursor.getColumnIndex("body");

            msg += cursor.getString(phoneColumn) + ":" + cursor.getString(smsColumn) + "\n";
        }
        textView.setText(msg);
    }

    class SmsObserver extends ContentObserver{

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public SmsObserver(Context context, Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getSMS();
        }
    }

    private void getSMS(){
        String flagId = "";
        String where = "type='1' AND date >"+ (System.currentTimeMillis() - 60*1000);
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(Uri.parse("content://sms/inbox"), null, where, null, null);
        String msg = "";
        while(cursor.moveToNext()) {
            int phoneColumn = cursor.getColumnIndex("address");
            int smsColumn = cursor.getColumnIndex("body");
            int typeColumn = cursor.getColumnIndex("type");
            int threadId = cursor.getColumnIndex("thread_id");
            flagId = cursor.getString(threadId);
            msg += cursor.getString(phoneColumn) + ":" + cursor.getString(smsColumn) + "  type:"+cursor.getString(typeColumn)+ "  thread_id:"+cursor.getString(threadId)+"\n";
        }
        cursor.close();
        if (flagId.equals(checkId)){
            return;
        }else {
            checkId = flagId;
        }
        textView.setText(msg);
        Log.e(TAG, msg);
    }
}
