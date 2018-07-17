package se.mah.af2015.worldandfriends;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class GroupFragment extends Fragment {
    private GroupAdapter mGroupAdapter;

    public GroupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_group, container, false);

        final EditText etGroup = (EditText) rootView.findViewById(R.id.et_group_name);

        SharedPreferences preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        final String name = preferences.getString("username", "");
        final TextView tvName = (TextView) rootView.findViewById(R.id.name);
        if(name.equals("")) {
            tvName.setText(String.format("%s%s", getResources().getText(R.string.user), getResources().getText(R.string.undefined)));
        } else {
            tvName.setText(String.format("%s%s", getResources().getText(R.string.user), name));
        }

        ListView listView = (ListView) rootView.findViewById(R.id.group_list);
        mGroupAdapter = new GroupAdapter(getActivity(), MainActivity.mGroups);
        listView.setAdapter(mGroupAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity activity = ((MainActivity) getActivity());
                activity.connectToGroup((String) parent.getItemAtPosition(position), name);

                activity.showFragment(MainActivity.mMapFragment, "MapFragment");
            }
        });

        final Button btnCreateGroup = (Button) rootView.findViewById(R.id.btn_create_group);
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = ((MainActivity) getActivity());
                activity.connectToGroup(etGroup.getText().toString(), name);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                activity.showFragment(MainActivity.mMapFragment, "MapFragment");
            }
        });

        final Button btnRefresh = (Button) rootView.findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BtnRefresh", "Refreshing");
                ((MainActivity) getActivity()).updateGroups();
            }
        });

        final Button btnDisconnect = (Button) rootView.findViewById(R.id.btn_disconnect);
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).disconnect();
            }
        });

        return rootView;
    }

    public void updateAdapter() {
        mGroupAdapter.notifyDataSetChanged();
    }
}
