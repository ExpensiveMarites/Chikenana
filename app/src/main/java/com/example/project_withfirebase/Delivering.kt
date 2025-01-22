package com.example.project_withfirebase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Delivering : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivering)

        val videoView = findViewById<VideoView>(R.id.videoEnding)
        val videoPath = "android.resource://" + packageName + "/" + R.raw.endingvid
        val videoUri = Uri.parse(videoPath)
        videoView.setVideoURI(videoUri)
        videoView.start()

        videoView.setOnCompletionListener {
            val intent = Intent (this, LogIn_activity::class.java)
            startActivity(intent)
            finish()
        }
    }
}