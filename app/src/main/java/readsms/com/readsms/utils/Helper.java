package readsms.com.readsms.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by taniaanand on 03/02/19.
 */

public class Helper {

    private static Helper instance = null;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;


    private String TAG = Helper.class.getSimpleName();

    private Helper() {
    }

    public static Helper getInstance(Context context) {
        if (instance == null) {
            instance = new Helper();
            String SHARE_SMS_DATA = "smsData";
            preferences = context.getSharedPreferences(SHARE_SMS_DATA, Context.MODE_PRIVATE);
            editor = preferences.edit();
            editor.apply();
        }
        return instance;
    }


    public void saveFromNotiFlag(boolean flag){
        editor.putBoolean("isNoti",flag);
        editor.commit();
    }

    public  boolean getNotiFlag(){
        return preferences.getBoolean("isNoti",false);
    }



    public static String getGroupName(long differenceInSecs){
        String name = "";

        if(differenceInSecs < 3600){ // for less than 1 hrs ago
            name = "1";
        }else if(differenceInSecs < 7200){ // for less than  2 hrs ago
            name = "2";
        }else if(differenceInSecs < 10800){ // for less than 3 hrs ago
            name = "3";
        }else if(differenceInSecs < 21600){ // for less than 6 hrs ago
            name = "6";
        }else if(differenceInSecs < 43200){ // less than 12 hrs ago
            name = "12";
        }
        else if(differenceInSecs < 86400){ // for less than 24 hrs  ago
            name = "24";
        }

        return  name;
    }



}
