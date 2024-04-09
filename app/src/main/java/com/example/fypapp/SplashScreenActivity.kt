package com.example.fypapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.motion.widget.MotionLayout

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var motionLayout: MotionLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets layout to full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_splash_screen)

        // Removes action bar
        supportActionBar?.hide()

        // Get the splash screen MotionLayout and
        // start the layout animation of the MotionLayout, which triggers the splash screen animation
        motionLayout = findViewById(R.id.splashLayout)
        motionLayout.startLayoutAnimation()

        motionLayout.setTransitionListener(object :MotionLayout.TransitionListener{
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
                // not important
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                // not important
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                // This method is called when the transition animation completes
                // Navigates to the MainActivity after the splash screen animation finishes
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))

                // Finish the current activity (SplashScreenActivity) to prevent it from
                // being shown again when the user presses on the back button
                finish()
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
                // not important
            }

        })
    }
}