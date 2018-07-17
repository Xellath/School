package se.mah.af2015.dinekonomi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {
    private Controller controller;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView homeText = (TextView) rootView.findViewById(R.id.home_text);
        controller = new Controller((MainActivity) getActivity());
        controller.formatHomeText(homeText);

        return rootView;
    }

}
