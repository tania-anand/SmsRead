package readsms.com.readsms.app;

import android.app.Application;
import android.content.Context;
import android.provider.Telephony;
import android.util.Log;

import readsms.com.readsms.utils.MySmsReceiver;

/**
 * Created by taniaanand on 03/02/19.
 */

public class SmsReadApplication extends Application {
    public static volatile Context applicationContext;
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(SmsReadApplication.class.getSimpleName(),"in application on create");
        applicationContext = getApplicationContext();



    }
}
