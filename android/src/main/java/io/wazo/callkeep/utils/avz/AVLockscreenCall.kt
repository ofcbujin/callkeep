package io.wazo.callkeep.utils.avz

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import io.wazo.callkeep.databinding.ActivityLockscreenBinding


class AVLockscreenCall : AppCompatActivity() {

    private lateinit var binding: ActivityLockscreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        showWhenLockedAndTurnScreenOn()
        super.onCreate(savedInstanceState)

        binding = ActivityLockscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

}