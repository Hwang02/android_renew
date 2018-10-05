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
import com.hotelnow.fragment.model.CityItem;
import java.util.List;

public class AreaSelectAdapter extends ArrayAdapter<CityItem> {
    private Context mContext;

    public AreaSelectAdapter(Context context, int textViewResourceId, List<CityItem> objects) {
        super(context, textViewResourceId, objects);

        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_city_item, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        final CityItem entry = getItem(position);

        holder.tv_city.setText(entry.getCity_ko());

        return v;
    }


    private class ViewHolder {
        TextView tv_city;

        public ViewHolder(View v) {
            tv_city = (TextView) v.findViewById(R.id.tv_city);
            tv_city.setTag(R.id.id_holder);
        }
    }

}
