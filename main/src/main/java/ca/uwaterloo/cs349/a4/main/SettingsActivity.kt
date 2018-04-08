package ca.uwaterloo.cs349.a4.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.NumberPicker

class SettingsActivity : AppCompatActivity(), Observer {
    val TAG: String = "SettingsActivity"
    var model: Model = Model.getModelInstance()
    lateinit var buttonsNumberPicker: NumberPicker
    lateinit var difficultyNumberPicker: NumberPicker
    var buttonsData = arrayOf("1", "2", "3", "4", "5", "6")
    var difficultyData = arrayOf("Easy", "Normal", "Hard")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        model.addObserver(this)
        buttonsNumberPicker = findViewById<NumberPicker>(R.id.buttonsNumberPicker)
        buttonsNumberPicker.minValue = 0
        buttonsNumberPicker.maxValue = buttonsData.size - 1
        buttonsNumberPicker.displayedValues = buttonsData
        buttonsNumberPicker.wrapSelectorWheel = false
        buttonsNumberPicker.value = model.numButtons - 1
        class MyButtonsListener : NumberPicker.OnValueChangeListener {
            override fun onValueChange(picker: NumberPicker, oldVal: Int, newVal: Int) {
                model.numButtons = newVal + 1
            }
        }
        buttonsNumberPicker.setOnValueChangedListener(MyButtonsListener())

        difficultyNumberPicker = findViewById<NumberPicker>(R.id.difficultyNumberPicker)
        difficultyNumberPicker.minValue = 0
        difficultyNumberPicker.maxValue = difficultyData.size - 1
        difficultyNumberPicker.displayedValues = difficultyData
        difficultyNumberPicker.wrapSelectorWheel = false
        setDifficultyPickerValue()
        class MyDifficultyListener : NumberPicker.OnValueChangeListener {
            override fun onValueChange(picker: NumberPicker, oldVal: Int, newVal: Int) {
                model.difficulty = when (newVal) {
                    0 -> Model.Difficulty.EASY
                    1 -> Model.Difficulty.NORMAL
                    2 -> Model.Difficulty.HARD
                    else -> Model.Difficulty.NORMAL
                }
            }
        }
        difficultyNumberPicker.setOnValueChangedListener(MyDifficultyListener())
    }

    fun setDifficultyPickerValue() {
        difficultyNumberPicker.value = when(model.difficulty) {
            Model.Difficulty.EASY -> 0
            Model.Difficulty.NORMAL -> 1
            Model.Difficulty.HARD -> 2
        }
    }

    override fun update() {
        //Log.d(TAG, "[DEBUG] Updating")
        buttonsNumberPicker.value = model.numButtons - 1
        setDifficultyPickerValue()
        //Log.d(TAG, "[DEBUG] Updated")
    }
}
