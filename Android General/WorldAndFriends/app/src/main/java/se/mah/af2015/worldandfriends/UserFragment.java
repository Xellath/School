package se.mah.af2015.worldandfriends;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import static android.content.Context.MODE_PRIVATE;

public class UserFragment extends Fragment {

    public UserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);

        final EditText etUsername = (EditText) rootView.findViewById(R.id.et_username);
        final Button btnContinue = (Button) rootView.findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("user", MODE_PRIVATE).edit();
                editor.putString("username", username);
                editor.apply();

                ((MainActivity) getActivity()).showFragment(MainActivity.mGroupFragment, "GroupFragment");
            }
        });

        return rootView;
    }
}
