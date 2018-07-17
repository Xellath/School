package se.mah.af2015.worldandfriends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupAdapter extends ArrayAdapter<String> {
    private ViewHolder mViewHolder;

    public GroupAdapter(Context context, ArrayList<String> groups) {
        super(context, 0, groups);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);

            mViewHolder = new ViewHolder();
            mViewHolder.tvName = (TextView) convertView.findViewById(android.R.id.text1);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.tvName.setText(name);

        return convertView;
    }

    private static class ViewHolder {
        TextView tvName;
    }
}
