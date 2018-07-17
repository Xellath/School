package se.mah.af2015.p4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * LoginActivity authorizes a user to the database, and sends us to next Activity
 *
 * @author Alexander Johansson (AF2015)
 */
public class LoginActivity extends AppCompatActivity {
    private EditText mUsername;
    private EditText mPassword;

    private Button mAuthorize;

    private PathfinderDBHelper mPathfinderDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Init EditText elements
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);

        // Init database
        mPathfinderDb = new PathfinderDBHelper(this);

        // Set click listener on button
        mAuthorize = (Button) findViewById(R.id.authorize);
        mAuthorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Authorize user to database
                int success = mPathfinderDb.authorizeUser(mUsername.getText().toString().trim(), mPassword.getText().toString());
                // If success does not equal -1, our user exists in the database
                if(success != -1) {
                    startMovementActivity(success);
                } else {
                    // User does not exist, notify user
                    Toast.makeText(getApplication(), getResources().getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Method starts new Activity upon success
     * @param userId Integer
     */
    private void startMovementActivity(int userId) {
        // Bundle user id
        Bundle bundle = new Bundle();
        bundle.putInt("userid", userId);

        // Create intent and put bundle as extra, start activity intent
        Intent intent = new Intent(LoginActivity.this, MovementActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
