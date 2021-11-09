package io.wazo.callkeep.avz;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.wazo.callkeep.databinding.ActivityLockscreenBinding;

public class AVLockscreenCalling extends AppCompatActivity {

    private ActivityLockscreenBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        showWhenLockedAndTurnScreenOn();
        super.onCreate(savedInstanceState);

        binding = ActivityLockscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            );
        }
    }
}
