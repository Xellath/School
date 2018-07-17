package se.mah.af2015.dinekonomi;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class EconomyFragment extends Fragment {
    private Controller controller;

    private TextView tvTitle;
    private ImageButton ibDatePicker;
    private ImageButton ibDateClear;

    private SQLiteCursorAdapter cursorAdapter;
    private Cursor currentCursor;
    private String title;
    private int fragmentType;

    public EconomyFragment() {

    }

    public static EconomyFragment createWithCursor(Cursor cursor, String title, int fragmentType) {
        EconomyFragment fragment = new EconomyFragment();
        fragment.setCursor(cursor);
        fragment.setTitle(title);
        fragment.setFragmentType(fragmentType);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_economy, container, false);
        controller = new Controller((MainActivity) getActivity());
        tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        ibDatePicker = (ImageButton) rootView.findViewById(R.id.date_picker);
        ibDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.showDatePicker(EconomyFragment.this, fragmentType);
            }
        });

        ibDateClear = (ImageButton) rootView.findViewById(R.id.clear_date);
        ibDateClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursorAdapter.changeCursor(controller.getNewCursor(fragmentType, "order by date DESC"));
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tvTitle.setText(getTitle());
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        cursorAdapter = new SQLiteCursorAdapter(getActivity(), getCursor());
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = cursorAdapter.getCursor();
                cursor.moveToPosition(position);
                controller.showInfoDialog(cursor);
            }
        });
    }

    private Cursor getCursor() {
        return currentCursor;
    }

    public void setCursor(Cursor cursor) {
        this.currentCursor = cursor;
    }

    public void updateCursor() {
        this.cursorAdapter.changeCursor(currentCursor);
    }

    public void forceUpdateCursor() {
        this.cursorAdapter.changeCursor(controller.getNewCursor(fragmentType, "order by date DESC"));
    }

    private String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private void setFragmentType(int fragmentType) {
        this.fragmentType = fragmentType;
    }

    @Override
    public void onResume() {
        super.onResume();

        tvTitle.setText(title);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", title);
        outState.putInt("fragment_type", fragmentType);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            title = savedInstanceState.getString("title");
            fragmentType = savedInstanceState.getInt("fragment_type");
            cursorAdapter.changeCursor(controller.getNewCursor(fragmentType, "order by date DESC"));

            controller.setFabVisibility(View.VISIBLE);
        }
    }
}
