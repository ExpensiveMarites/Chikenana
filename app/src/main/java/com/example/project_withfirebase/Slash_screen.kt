package com.example.project_withfirebase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity


class Slash_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slash_screen)

        val videoView = findViewById<VideoView>(R.id.videoViewSplash)
        val videoPath = "android.resource://" + packageName + "/" + R.raw.chicken_full
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