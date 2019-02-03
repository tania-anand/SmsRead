package readsms.com.readsms.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import readsms.com.readsms.R;
import readsms.com.readsms.activity.MainActivity;
import readsms.com.readsms.listener.MySmsListener;
import readsms.com.readsms.model.Sms;

public class MySmsService extends Service {

    private static MySmsListener mMySmsListener;
    private  static  final String TAG = MySmsService.class.getSimpleName();
    MySmsReceiver mSMSreceiver;
    IntentFilter mIntentFilter;

    public MySmsService() {

    }


    @Override
    public void onCreate() {
        //SMS event receiver
        mSMSreceiver = new MySmsReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSMSreceiver, mIntentFilter);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG,"in service");

        if(intent.hasExtra("newSmsObject")){
            Sms sms = (Sms)intent.getSerializableExtra("newSmsObject");
            Log.d(TAG,"in service sms object exists  "+sms.toString());


            Helper.getInstance(this).saveFromNotiFlag(true);
            if (MainActivity.active) {
                updateSmsList(getApplicationContext(),sms);
            } else {
                updateSmsList(getApplicationContext(),sms);
                showNotification(getApplicationContext(),sms);

            }

        }
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public static void bindListener(MySmsListener listener){

        if(mMySmsListener==null){
            mMySmsListener = listener;
        }
    }


    private void updateSmsList(Context context ,Sms object){

        if(mMySmsListener!=null){
            mMySmsListener.onMessageReceived(object);
        }

    }


    private void showNotification(Context context , Sms object){

        NotificationChannel channel ;
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.putExtra("mySmsObject",object);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "101";
        Notification summaryNotification =
                new NotificationCompat.Builder(context, channelId)
                        .setContentTitle(object.getMobile())
                        //set content text to support devices running API level < 24
                        .setContentText(object.getMessage())
                        .setSmallIcon(R.drawable.ic_chat_noti)
                        .setColor(context.getResources().getColor(R.color.colorPrimary))
                        //build summary info into InboxStyle template
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        //set this notification as the summary for the group
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(object.getMessage()))
                        .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert manager != null;
            manager.createNotificationChannel(channel);
        }
        notificationManager.notify(100, summaryNotification);

    }
}
