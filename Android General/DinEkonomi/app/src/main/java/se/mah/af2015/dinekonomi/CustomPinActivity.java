package se.mah.af2015.dinekonomi;

import android.content.Intent;
import android.widget.Toast;

import com.github.orangegangsters.lollipin.lib.managers.AppLockActivity;

public class CustomPinActivity extends AppLockActivity {

    @Override
    public void showForgotDialog() {
    }

    @Override
    public void onPinFailure(int attempts) {
        Toast.makeText(this, getResources().getString(R.string.incorrect_pin), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPinSuccess(int attempts) {
        Intent intent = new Intent(CustomPinActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public int getPinLength() {
        return super.getPinLength();
    }
}