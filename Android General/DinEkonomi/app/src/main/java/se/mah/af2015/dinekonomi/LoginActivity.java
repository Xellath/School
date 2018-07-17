package se.mah.af2015.dinekonomi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.orangegangsters.lollipin.lib.PinActivity;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;

public class LoginActivity extends PinActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etPersonalIdentity = (EditText) findViewById(R.id.personal_identity);
        final Button btnContinue = (Button) findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String personalIdentity = etPersonalIdentity.getText().toString();
                if (!personalIdentity.isEmpty() && personalIdentity.length() == 10) {
                    SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                    String personalId = prefs.getString("personal_id", null);
                    Intent intent = new Intent(LoginActivity.this, CustomPinActivity.class);
                    if (personalId != null) {
                        if (personalId.equals(personalIdentity)) {
                            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                            startActivity(intent);
                        } else {
                            Snackbar.make(v, getResources().getString(R.string.wrong_id), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    } else {
                        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                        editor.putString("personal_id", personalIdentity);
                        editor.apply();

                        intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                        startActivity(intent);
                    }
                } else {
                    Snackbar.make(v, getResources().getString(R.string.id_too_short), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
    }
}
