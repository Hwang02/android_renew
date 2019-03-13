package com.hotelnow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.fragment.model.ActivityThemeItem;
import com.hotelnow.fragment.model.CityItem;

import java.util.List;

public class ActivityFilterAdapter extends ArrayAdapter<ActivityThemeItem> {
    private Context mContext;
    private String tv_category;

    public ActivityFilterAdapter(Context context, int textViewResourceId, List<ActivityThemeItem> objects, String tv_category) {
        super(context, textViewResourceId, objects);

        mContext = context;
        this.tv_category = tv_category;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_activity_theme_item, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        final ActivityThemeItem entry = getItem(position);

        if (tv_category.equals(entry.getQcategory_ko())) {
            holder.parent_view.setActivated(true);
        } else {
            holder.parent_view.setActivated(false);
        }

        holder.tv_city.setText(entry.getQcategory_ko());
        holder.tv_id.setText(entry.getQcategory_id());

        return v;
    }


    private class ViewHolder {
        TextView tv_city, tv_id;
        LinearLayout parent_view;

        public ViewHolder(View v) {
            tv_city = (TextView) v.findViewById(R.id.tv_city);
            tv_id = (TextView) v.findViewById(R.id.tv_id);
            parent_view = (LinearLayout) v.findViewById(R.id.parent_view);
            tv_city.setTag(R.id.id_holder);
            tv_id.setTag(R.id.id_holder);
            parent_view.setTag(R.id.id_holder);
        }
    }

}
