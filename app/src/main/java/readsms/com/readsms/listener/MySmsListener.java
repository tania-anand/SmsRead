package readsms.com.readsms.listener;

import readsms.com.readsms.model.Sms;

/**
 * Created by taniaanand on 03/02/19.
 */

public interface MySmsListener {

     void onMessageReceived(Sms object);

}
