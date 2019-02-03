package readsms.com.readsms.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Date;
import java.util.Objects;

import readsms.com.readsms.model.Sms;

public class MySmsReceiver extends BroadcastReceiver {
    private static String TAG = MySmsReceiver.class.getSimpleName();




    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // Get Bundle object contained in the SMS intent passed in


        Log.d(TAG,"on receive sms");
        Bundle bundle = intent.getExtras();
        SmsMessage[] smsm;
        String sms_str ="";

        if (bundle != null) {
            // Get the SMS message
            Object[] pdus = (Object[]) bundle.get("pdus");
            String format = bundle.getString("format");
            if (pdus != null) {
                boolean isVersionM = (Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.M);
                smsm = new SmsMessage[Objects.requireNonNull(pdus).length];
                for (int i = 0; i < smsm.length; i++) {
                    // Check Android version and use appropriate createFromPdu.
                    if (isVersionM) {
                        // If Android version M or newer:
                        smsm[i] =
                                SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    } else {
                        // If Android version L or older:
                        smsm[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }

                    sms_str += "\r\nMessage: ";
                    sms_str += smsm[i].getMessageBody();
                    sms_str += "\r\n";
                    //Check here sender is yours
                    String Sender = smsm[i].getOriginatingAddress();
                    Long timeInMillis = smsm[i].getTimestampMillis();

                    Log.d("sender is", Sender);
                    try {
                        Log.d("SmsReceiver", "match" + sms_str + " time in millis " + timeInMillis);

                        Sms sms = new Sms();
                        sms.setDate(String.valueOf(timeInMillis));
                        sms.setMessage(sms_str);
                        sms.setMobile(Sender);

                        // difference between current time and and sms date in millisecs
                        long differenceInMillsecs = new Date().getTime() - timeInMillis;

                        long differenceInSecs = differenceInMillsecs / 1000;

                        sms.setGroup(Helper.getGroupName(differenceInSecs));

                        Intent intent1 = new Intent(context, MySmsService.class);
                        intent1.putExtra("newSmsObject",sms);
                        context.startService(intent1);



                    } catch (Exception e) {
                        Log.d("SmsReceiver", "error ocurred in matching ");
                    }


                }
            }
        }
    }









}
