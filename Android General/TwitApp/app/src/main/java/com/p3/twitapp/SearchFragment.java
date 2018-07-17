package com.p3.twitapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SearchFragment extends Fragment {
    private EditText edSearch;
    private Button btnSearch;

    public SearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        edSearch = (EditText) rootView.findViewById(R.id.edSearch);
        btnSearch = (Button) rootView.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get search string (from edSearch)
                showYoutubeList();
            }
        });

        return rootView;
    }

    private void showYoutubeList() {
        // perform youtube search, pass with bundle to fragment(?)
        // replace fragment (container)

        ThumbnailFragment thumbFragment = new ThumbnailFragment();

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, thumbFragment).commit();

        fm.popBackStack();
    }
}
