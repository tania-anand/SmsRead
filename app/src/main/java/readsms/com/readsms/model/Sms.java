package readsms.com.readsms.model;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by taniaanand on 03/02/19.
 */

public class Sms implements Serializable{

   private String message;
   private String group;
   private String date;
   private String mobile;
   private boolean isTextLong = false;
   private boolean showMore = false;
   public final static int txtLimit = 50;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;

        if(message.length()>txtLimit)
            isTextLong = true;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isTextLong() {
        return isTextLong;
    }

    public void setTextLong(boolean textLong) {
        isTextLong = textLong;
    }

    public boolean isShowMore() {
        return showMore;
    }

    public void setShowMore(boolean showMore) {
        this.showMore = showMore;
    }

    @Override
    public String toString() {
        return "Sms{" +
                "message='" + message + '\'' +
                ", group='" + group + '\'' +
                ", date='" + date + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sms)) return false;

        Sms sms = (Sms) o;

        return (date != null ? date.equals(sms.date) : sms.date == null);
    }

    public static Comparator MyTimestampComparator = new Comparator<Sms>(){
        @Override
        public int compare(Sms o1, Sms o2) {
            Long t1 = Long.parseLong(o1.getDate());
            Long t2 = Long.parseLong(o2.getDate());

            if(t2 > t1)
                return 1;
            else if(t1 > t2)
                return -1;
            else
                return 0;

        }
    };





}
