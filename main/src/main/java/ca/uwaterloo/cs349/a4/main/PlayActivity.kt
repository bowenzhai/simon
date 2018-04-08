package ca.uwaterloo.cs349.a4.main

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity(), Observer {
    val TAG: String = "PlayActivity"
    var model: Model = Model.getModelInstance()
    lateinit var playLayout: ConstraintLayout
    lateinit var scoreTextView: TextView
    lateinit var gameStatusTextView: TextView
    lateinit var sequenceTextView: TextView
    var numButtons: Int = 0
    var simonButtons: MutableList<FloatingActionButton> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        model.addObserver(this)

        scoreTextView = findViewById<TextView>(R.id.scoreTextView)
        scoreTextView.text = model.score.toString()

        gameStatusTextView = findViewById<TextView>(R.id.gameStatusTextView)
        setStatusText()

        playLayout = findViewById<ConstraintLayout>(R.id.playLayout)
        playLayout.setOnClickListener {
            startGame()
        }

        sequenceTextView = findViewById<TextView>(R.id.sequenceTextView)
        sequenceTextView.text = "Sequence: "
        sequenceTextView.visibility = View.INVISIBLE

        simonButtons.add(simonButton1)
        simonButton1.setOnClickListener {
            model.verifyButton(1)
        }

        simonButtons.add(simonButton2)
        simonButton2.setOnClickListener {
            model.verifyButton(2)
        }

        simonButtons.add(simonButton3)
        simonButton3.setOnClickListener {
            model.verifyButton(3)
        }

        simonButtons.add(simonButton4)
        simonButton4.setOnClickListener {
            model.verifyButton(4)
        }

        simonButtons.add(simonButton5)
        simonButton5.setOnClickListener {
            model.verifyButton(5)
        }

        simonButtons.add(simonButton6)
        simonButton6.setOnClickListener {
            model.verifyButton(6)
        }

        numButtons = model.numButtons
        lightUpButtons()
        Log.d(TAG, "[DEBUG] onCreate")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "[DEBUG] onPause")
    }

    override fun onResume() {
        super.onResume()
        model.reset()
        sequenceTextView.text = "Sequence: "
        numButtons = model.numButtons
        lightUpButtons()
        disableButtons()
        playLayout.setOnClickListener {
            startGame()
        }
        Log.d(TAG, "[DEBUG] onResume")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_play, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_settings -> openSettings()
        }
        return true
    }

    fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun setStatusText() {
        gameStatusTextView.text = when(model.getStateAsString()) {
            "START" -> "Touch anywhere to play"
            "COMPUTER" -> "Watch what I do..."
            "HUMAN" -> "Now it’s your turn"
            "LOSE" -> "You lose. Touch anywhere to play again"
            "WIN" -> "You won! Touch anywhere to continue"
            else -> "What? Status is null"
        }
    }

    fun startGame() {
        model.newRound()
        sequenceTextView.text = "Sequence: "
        numButtons = model.numButtons
        playLayout.setOnClickListener(null)
        var delay = model.getDelay()
        for (i in model.sequence) {
            /*val newString = sequenceTextView.text.toString() + model.nextButton().toString() + " "
            sequenceTextView.text = newString*/
            val handler = Handler()
            var currButton = simonButtons.get(i - 1)
            var color = currButton.backgroundTintList
            handler.postDelayed(Runnable {
                currButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE))
                Log.d(TAG, "computer pressed")
            }, delay.toLong())

            handler.postDelayed(Runnable {
                currButton.setBackgroundTintList(color)
                Log.d(TAG, "computer released")
                model.nextButton()
            }, delay.toLong() + (model.getDelay() / 4))
            delay += model.getDelay()
        }
        enableButtons()
    }

    fun lightUpButtons() {
        for (i in 0 until numButtons) {
            simonButtons.get(i).visibility = View.VISIBLE
        }
    }

    fun enableButtons() {
        for (i in 0 until numButtons) {
            simonButtons.get(i).isClickable = true
        }
    }

    fun disableButtons() {
        for (i in 0 until numButtons) {
            simonButtons.get(i).isClickable = false
        }
    }

    fun setRippleColor(color: String) {
        if (color == "white") {
            for (button in simonButtons) {
                button.rippleColor = Color.WHITE
            }
        } else {
            for (button in simonButtons) {
                button.rippleColor = Color.BLACK
            }
        }
    }

    override fun update() {
        //Log.d(TAG, "[DEBUG] Updating")
        scoreTextView.setText(model.score.toString())
        setStatusText()
        /*
        if (model.getStateAsString() == "HUMAN") {
            setRippleColor("white")
        } else {
            setRippleColor("black")
        }
        */
        if (model.getStateAsString() == "WIN" || model.getStateAsString() == "LOSE") {
            playLayout.setOnClickListener {
                startGame()
            }
            disableButtons()
        }
        //Log.d(TAG, "[DEBUG] Updated")
    }
}
