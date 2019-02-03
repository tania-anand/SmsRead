package readsms.com.readsms.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import readsms.com.readsms.R;
import readsms.com.readsms.model.Sms;
import readsms.com.readsms.utils.Helper;


public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.MyViewHolder>{

    private ArrayList<Sms> mSmsArrayList;
    private Context mContext;

    public SmsAdapter(Context context,ArrayList<Sms> arrayList){
        mSmsArrayList = arrayList;
        mContext = context;

    }
    @Override
    public SmsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.read_sms_listitem,parent,false);
        return new MyViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(SmsAdapter.MyViewHolder holder, int position) {
        holder.bindView(mSmsArrayList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mSmsArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtGroupNo,txtMessage,txtMobileNo,txtDate,txtShowMore;
        ConstraintLayout messageBoxConstraint;
        MyViewHolder(View itemView) {
            super(itemView);
            txtGroupNo = itemView.findViewById(R.id.group_tittle);
            txtMessage = itemView.findViewById(R.id.sms_message);
            txtMobileNo = itemView.findViewById(R.id.sms_phone_no);
            txtDate = itemView.findViewById(R.id.sms_date);
            txtShowMore = itemView.findViewById(R.id.show_more);
            messageBoxConstraint = itemView.findViewById(R.id.messageBoxConstraint);
        }
        void bindView(final Sms object , final int position){
            if( position>0 && !mSmsArrayList.get(position-1).getGroup().equals(object.getGroup())){
                txtGroupNo.setVisibility(View.VISIBLE);
            }else{
                txtGroupNo.setVisibility(View.GONE);
            }

            if(position == 0){
                txtGroupNo.setVisibility(View.VISIBLE);

                if(Helper.getInstance(mContext).getNotiFlag()) {
                    Animation animation = AnimationUtils.loadAnimation(mContext,
                            R.anim.fade);
                    messageBoxConstraint.startAnimation(animation);

                    Helper.getInstance(mContext).saveFromNotiFlag(false);
                }


            }

            txtGroupNo.setText("less than "+object.getGroup()+" hrs ago");
            txtMobileNo.setText(object.getMobile());
            if(object.isTextLong()){
                if(object.isShowMore()) {
                    txtMessage.setText(object.getMessage());
                    txtShowMore.setText("show less");

                }else{
                    txtMessage.setText(object.getMessage().substring(0, Sms.txtLimit) + "...");
                    txtShowMore.setText("show more");

                }
                txtShowMore.setVisibility(View.VISIBLE);
                txtShowMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(object.isShowMore()){
                            mSmsArrayList.get(position).setShowMore(false);
                        }else{
                            mSmsArrayList.get(position).setShowMore(true);
                        }
                        notifyItemChanged(position);
                    }
                });

            }else {
                txtMessage.setText(object.getMessage());
                txtShowMore.setVisibility(View.GONE);
            }

               txtDate.setText(getTimsString(Long.parseLong(object.getDate())));
        }

        private String getTimsString(Long timeStamp){
            String timeStr;
            Date date = new Date();
            date.setTime(timeStamp);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            timeStr = sdf.format(date);
            return  timeStr;
        }


    }


}
