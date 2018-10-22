package com.hotelnow.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.activity.MyCardActivity;
import com.hotelnow.activity.MyCouponActivity;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.dialog.DialogConfirm;
import com.hotelnow.fragment.model.CardEntry;
import com.hotelnow.fragment.model.CouponEntry;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by susia on 15. 12. 10..
 */

public class MyCardAdapter extends ArrayAdapter<CardEntry> {
    Context mContext;
    String hotels = "";
    DialogAlert dialogAlert;
    HashMap<String, Integer> map;
    String selectedCardId="";
    DialogConfirm dialogConfirm;
    List<CardEntry> mlist;

    public MyCardAdapter(Context context, int textViewResourceId, List<CardEntry> objects) {
        super(context, textViewResourceId, objects);

        mContext = context;
        mlist = objects;

        map = new HashMap<String, Integer>();
        map.put("01", R.drawable.img_card_01); // 하나
        map.put("03", R.drawable.img_card_03); // 롯데
        map.put("04", R.drawable.img_card_04); // 현대
        map.put("06", R.drawable.img_card_06); // 국민
        map.put("11", R.drawable.img_card_11); // BC
        map.put("12", R.drawable.img_card_12); // 삼성
        map.put("14", R.drawable.img_card_14); // 신한
        map.put("15", R.drawable.img_card_15); // 씨티
        map.put("16", R.drawable.img_card_16); // NH
        map.put("17", R.drawable.img_card_17); // 하나SK
        map.put("21", R.drawable.img_card_21); // 해외비자
        map.put("22", R.drawable.img_card_22); // 해외마스터
        map.put("23", R.drawable.img_card_23); // JCB
        map.put("24", R.drawable.img_card_24); // 해외아멕스
        map.put("25", R.drawable.img_card_25); // 해외다이너스
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_my_card_item, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        final CardEntry entry = getItem(position);

        if(!entry.getId().equals("empty")) {
            holder.view_item.setVisibility(View.VISIBLE);
            holder.empty_item.setVisibility(View.GONE);
            holder.card_num.setText(entry.getCardno());
            holder.card_type.setText(entry.getCardtype());
            if(map.containsKey(entry.getCardcd())) {
                holder.card_img.setImageDrawable(mContext.getResources().getDrawable(map.get(entry.getCardcd())));
            }

            holder.card_delete.setTag(entry.getId());

            holder.card_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedCardId = (String) v.getTag();

                    dialogConfirm = new DialogConfirm(
                            mContext.getString(R.string.alert_notice),
                            "선택하신 카드 정보를 삭제하시겠습니까?",
                            mContext.getString(R.string.alert_no),
                            mContext.getString(R.string.alert_yes),
                            mContext,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogConfirm.dismiss();
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    JSONObject params = new JSONObject();
                                    try {
                                        params.put("id", selectedCardId);
                                    } catch (JSONException e) {
                                    }

                                    Api.post(CONFIG.cardRemoveUrl, params.toString(), new Api.HttpCallback() {
                                        @Override
                                        public void onFailure(Response response, Exception e) {
                                            Toast.makeText(mContext, mContext.getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onSuccess(Map<String, String> headers, String body) {
                                            try {
                                                JSONObject obj = new JSONObject(body);

                                                if (!obj.getString("result").equals("success")) {
                                                    Toast.makeText(mContext, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                Toast.makeText(mContext, "선택하신 카드를 삭제하였습니다.", Toast.LENGTH_SHORT).show();

                                                ((MyCardActivity)mContext).getCardList();
                                                ((MyCardActivity)mContext).mRefresh = true;
                                            } catch (Exception e) {
                                            }
                                        }
                                    });
                                    dialogConfirm.dismiss();
                                }
                            });
                    dialogConfirm.setCancelable(false);
                    dialogConfirm.show();
                }
            });

            if(mlist.size()-1 == position){
                holder.end_bar.setVisibility(View.INVISIBLE);
            }
            else{
                holder.end_bar.setVisibility(View.GONE);
            }
        }
        else{
            holder.view_item.setVisibility(View.GONE);
            holder.empty_item.setVisibility(View.VISIBLE);
            ((MyCardActivity)mContext).getEmptyHeight(holder.empty_item);
        }

        return v;
    }

    private class ViewHolder {
        ImageView card_img;
        ImageView card_delete;
        TextView card_num;
        TextView card_type;
        LinearLayout view_item, empty_item;
        View end_bar;

        public ViewHolder(View v) {
            card_num = (TextView) v.findViewById(R.id.card_num);
            card_type = (TextView) v.findViewById(R.id.card_type);
            card_delete = (ImageView) v.findViewById(R.id.card_delete);
            card_img = (ImageView) v.findViewById(R.id.card_img);
            empty_item = (LinearLayout) v.findViewById(R.id.empty_item);
            view_item = (LinearLayout) v.findViewById(R.id.view_item);
            end_bar = (View) v.findViewById(R.id.end_bar);
            v.setTag(R.id.id_holder);
        }

    }
}
