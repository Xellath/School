package se.mah.af2015.p1;

import android.content.Context;
import android.hardware.Sensor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * SensorAdapter extends ArrayAdapter and lets us populate a ListView with data from given Sensor list
 *
 * @author Alexander Johansson (AF2015)
 */
public class SensorAdapter extends ArrayAdapter<Sensor> {
    private ViewHolder viewHolder;

    /**
     * Constructor for SensorAdapter, calls super on ArrayAdapter with supplied list
     * @param context Context
     * @param sensors ArrayList<Sensor>
     */
    public SensorAdapter(Context context, ArrayList<Sensor> sensors) {
        super(context, 0, sensors);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get Sensor at position
        Sensor sensor = getItem(position);
        // If view is not inflated
        if(convertView == null) {
            // Inflate view with android standard list item style
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);

            // Init view holder
            viewHolder = new ViewHolder();
            // Find TextView in list item style, s
            viewHolder.tvName = (TextView) convertView.findViewById(android.R.id.text1);

            // Set ViewHolder as tag for inflated view
            convertView.setTag(viewHolder);
        } else {
            // Retrieve tag (ViewHolder)
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Set element to Sensor name
        if(sensor != null) {
            viewHolder.tvName.setText(sensor.getName());
        }
        return convertView;
    }

    /**
     * ViewHolder to retain views, not having to look them up (find) for each re-rendering
     */
    private static class ViewHolder {
        TextView tvName;
    }
}
