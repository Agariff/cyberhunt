package com.example.cyberhunt

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity
import androidx.core.content.ContextCompat

class MainActivity : Activity() {
    private val REQ_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#0d1117"))
            setPadding(50, 50, 50, 50)
        }

        val title = TextView(this).apply {
            text = "CyberHunt Screen Share"
            textSize = 24f
            setTextColor(Color.parseColor("#39ff14"))
            gravity = Gravity.CENTER
        }

        val btn = Button(this).apply {
            text = "Start Screen Share"
            textSize = 18f
            setOnClickListener { requestScreenShare() }
        }

        layout.addView(title)
        layout.addView(btn)
        setContentView(layout)
    }

    private fun requestScreenShare() {
        val mpm = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mpm.createScreenCaptureIntent(), REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE && resultCode == RESULT_OK && data != null) {
            val intent = Intent(this, ScreenShareService::class.java).apply {
                putExtra("resultCode", resultCode)
                putExtra("data", data)
            }
            ContextCompat.startForegroundService(this, intent)
        }
    }
}