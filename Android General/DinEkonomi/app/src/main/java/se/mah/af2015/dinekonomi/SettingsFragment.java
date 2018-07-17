package se.mah.af2015.dinekonomi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SettingsFragment extends Fragment {
    private EditText etPersonalIdentity;
    private EditText etName;
    private EditText etSurname;

    private String personalIdentity;
    private String name;
    private String surname;

    private Controller controller;

    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        controller = new Controller((MainActivity) getActivity());

        etPersonalIdentity = (EditText) rootView.findViewById(R.id.personal_identity);
        etName = (EditText) rootView.findViewById(R.id.name);
        etSurname = (EditText) rootView.findViewById(R.id.surname);

        final Button btnUpdate = (Button) rootView.findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.updateSettings(etName.getText().toString(), etSurname.getText().toString());
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        etPersonalIdentity.setText(personalIdentity);
        etName.setText(name);
        etSurname.setText(surname);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("personal_id", personalIdentity);
        outState.putString("name", etName.getText().toString());
        outState.putString("surname", etSurname.getText().toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            personalIdentity = savedInstanceState.getString("personal_id");
            name = savedInstanceState.getString("name");
            surname = savedInstanceState.getString("surname");

            controller.setFabVisibility(View.INVISIBLE);
        }
    }

    public void setStrings(String id, String name, String surname) {
        this.personalIdentity = id;
        this.name = name;
        this.surname = surname;
    }
}
