package com.hotelnow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.fragment.model.ActivityThemeItem;
import com.hotelnow.fragment.model.CityItem;

import java.util.List;

public class ActivityFilterAdapter extends ArrayAdapter<ActivityThemeItem> {
    private Context mContext;

    public ActivityFilterAdapter(Context context, int textViewResourceId, List<ActivityThemeItem> objects) {
        super(context, textViewResourceId, objects);

        mContext = context;
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

        holder.tv_city.setText(entry.getQcategory_ko());
        holder.tv_id.setText(entry.getQcategory_id());

        return v;
    }


    private class ViewHolder {
        TextView tv_city, tv_id;

        public ViewHolder(View v) {
            tv_city = (TextView) v.findViewById(R.id.tv_city);
            tv_id = (TextView) v.findViewById(R.id.tv_id);
            tv_city.setTag(R.id.id_holder);
            tv_id.setTag(R.id.id_holder);
        }
    }

}
