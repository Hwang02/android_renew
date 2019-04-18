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
import com.hotelnow.model.SubCityItem;

import java.util.List;

public class AreaResultAdapter extends ArrayAdapter<SubCityItem> {
    private Context mContext;
    private List<SubCityItem> objects;

    public AreaResultAdapter(Context context, int textViewResourceId, List<SubCityItem> objects) {
        super(context, textViewResourceId, objects);

        mContext = context;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_subcity_item, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        final SubCityItem entry = getItem(position);

        holder.tv_subcity.setText(entry.getSubcity_ko());

        return v;
    }


    private class ViewHolder {
        TextView tv_subcity;

        public ViewHolder(View v) {
            tv_subcity = (TextView) v.findViewById(R.id.tv_subcity);
            tv_subcity.setTag(R.id.id_holder);
        }
    }

}
