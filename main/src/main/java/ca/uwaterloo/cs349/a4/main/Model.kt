package ca.uwaterloo.cs349.a4.main

import android.util.Log

class Model constructor(numButtons: Int, difficulty: Model.Difficulty, debug: Boolean) {
    // possible game states:
    // START - nothing happening
    // COMPUTER - computer is play a sequence of buttons
    // HUMAN - human is guessing the sequence of buttons
    // LOSE and WIN - game is over in one of these states
    enum class State {
        START,
        COMPUTER,
        HUMAN,
        LOSE,
        WIN
    }

    // levels of difficulty
    enum class Difficulty {
        EASY,
        NORMAL,
        HARD
    }

    // the game state and score
    var state: State = State.START

    var score: Int = 0
        set(score: Int) {
            field = score
            notifyObservers()
        }

    // the length of the sequence
    var length: Int = 1
        set(length: Int) {
            field = length
            notifyObservers()
        }

    // number of possible buttons
    var numButtons: Int = numButtons
        set(numButtons: Int) {
            field = numButtons
            notifyObservers()
        }

    // difficulty of the game
    var difficulty: Difficulty = difficulty
        set(difficulty: Difficulty) {
            field = difficulty
            notifyObservers()
        }

    // the sequence of buttons and current button
    var sequence: MutableList<Int> = mutableListOf()
    var index: Int = 0

    // debug flag
    var debug: Boolean = debug
        set(debug: Boolean) {
            field = debug
            notifyObservers()
        }

    // logging tag
    val TAG: String = "Model"

    // observers
    var observers: MutableList<Observer> = mutableListOf()

    // static members
    companion object {
        // our model instance
        val ourInstance = Model(4, Difficulty.NORMAL, true)

        fun getModelInstance(): Model {
            return ourInstance
        }
    }


    // get state as string
    fun getStateAsString():String {
        val result =
                when(this.state) {
                    State.START -> "START"
                    State.COMPUTER -> "COMPUTER"
                    State.HUMAN -> "HUMAN"
                    State.LOSE -> "LOSE"
                    State.WIN -> "WIN"
                }
        return result
    }


    // start a new round
    fun newRound() {
        if (debug) {
            Log.d(TAG,"[DEBUG] newRound, state: " + getStateAsString())
        }

        // reset if they lost last time
        if (state == State.LOSE) {
            if (debug) {
                Log.d(TAG, "[DEBUG] reset length and score after loss")
            }
            length = 1
            score = 0
        }

        sequence.clear()

        if (debug) {
            Log.d(TAG, "[DEBUG] new sequence: ")
        }

        for (i in 0 until length) {
            val b: Int = (Math.floor(Math.random() * (numButtons)) + 1).toInt()
            sequence.add(b)
            if (debug) {
                Log.d(TAG, b.toString() + " ")
            }
        }

        index = 0
        state = State.COMPUTER
        notifyObservers()
    }


    // call this to get the next button to show when computer is playing
    fun nextButton(): Int {
        if (state != State.COMPUTER) {
            Log.d(TAG, "[WARNING] nextButton called in " + getStateAsString())
            notifyObservers()
            return -1
        }

        // this is the next button to show in the sequence
        val button = sequence[index]

        if (debug) {
            Log.d(TAG, "[DEBUG] nextButton:  index " + index + " button " + button)
        }

        // advance to the next button
        ++index

        // if all the buttons were shown, give the human a chance to guess the sequence
        if (index >= sequence.size) {
            index = 0
            state = State.HUMAN
        }

        notifyObservers()
        return button
    }


    // verify if button pressed is correct
    fun verifyButton(button: Int): Boolean {
        if (state != State.HUMAN) {
            Log.d(TAG, "[WARNING] verifyButton called in " + getStateAsString())
            notifyObservers()
            return false
        }

        // did they press the right button?
        val correct = (button == sequence[index])

        if (debug) {
            Log.d(TAG, "[DEBUG] verifyButton: index " + index +
                    ", pushed " + button + ", sequence " + sequence[index])
        }

        // advance to next button
        ++index

        // pushed the wrong button
        if (!correct) {
            state = State.LOSE
            if (debug) {
                Log.d(TAG, ", wrong. ")
                Log.d(TAG, "[DEBUG] state is now " + getStateAsString())
            }
        // they got it right
        } else {
            if (debug) {
                Log.d(TAG, ", correct.")

                // if last button, then win the round
                if (index == sequence.size) {
                    state = State.WIN
                    ++score
                    ++length

                    if (debug) {
                        Log.d(TAG, "[DEBUG] state is now " + getStateAsString())
                        Log.d(TAG, "[DEBUG] new score " + score)
                        Log.d(TAG, ", length increased to " + length)
                    }
                }
            }
        }
        notifyObservers()
        return correct
    }


    fun getDelay(): Int {
        return when(this.difficulty) {
            Difficulty.EASY -> 2000
            Difficulty.NORMAL -> 1000
            Difficulty.HARD -> 500
        }
    }

    fun reset() {
        length = 1
        score = 0
        sequence.clear()
        index = 0
        state = State.START
        notifyObservers()
        Log.d(TAG, "[DEBUG] reset.")
    }


    fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    fun notifyObservers() {
        for (observer in observers) {
            observer.update()
        }
    }
}