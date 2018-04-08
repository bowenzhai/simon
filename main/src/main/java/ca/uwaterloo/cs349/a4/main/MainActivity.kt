package ca.uwaterloo.cs349.a4.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    lateinit var settingsButton: Button
    lateinit var playButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            openSettings()
        }
        playButton = findViewById<Button>(R.id.playButton)
        playButton.setOnClickListener {
            startGame()
        }
    }

    fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun startGame() {
        val intent = Intent(this, PlayActivity::class.java)
        startActivity(intent)
    }
}
