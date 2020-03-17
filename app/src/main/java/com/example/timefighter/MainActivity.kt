package com.example.timefighter

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var time = 60
    private var score = 0
    lateinit var tapMeButton: Button
    lateinit var gameScoreTextView: TextView
    lateinit var timeLeftTextView: TextView
    val initialCountDown: Long = 60000
    var gameStarted = false
    val countDownInterval: Long = 1000
    lateinit var countDownTimer: CountDownTimer
    internal var timeLeftOnTimer: Long = 60000

    companion object{
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tapMeButton = findViewById(R.id.button)
        gameScoreTextView = findViewById(R.id.textViewScore)
        timeLeftTextView = findViewById(R.id.textViewTime)

        Log.d(TAG, "onCreate called score : $score")
        tapMeButton.setOnClickListener {view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()
        }

        textViewScore.text = getString(R.string.yourScore, score)

        if(savedInstanceState != null){
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            restoreGame()
        }
        else{
            resetGame()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
        countDownTimer.cancel()

        Log.d(TAG, "saving score : $score and saving time left : $timeLeftOnTimer")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
    }

    private fun restoreGame(){
        textViewScore.text = getString(R.string.yourScore, score)
        val restoreTime = timeLeftOnTimer / 1000
        textViewTime.text = getString(R.string.timeLeft, restoreTime)

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished/1000
                val blinkAnimation = AnimationUtils.loadAnimation(this@MainActivity,R.anim.blink)
                timeLeftTextView.startAnimation(blinkAnimation)
                timeLeftTextView.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }
        countDownTimer.start()
        gameStarted = true
    }

    private fun resetGame(){
        score = 0
        gameScoreTextView.text = getString(R.string.yourScore, score)
        val initialTimeLeft = initialCountDown / 1000
        timeLeftTextView.text = getString(R.string.timeLeft, initialTimeLeft)

        countDownTimer = object:CountDownTimer(initialCountDown, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                val blinkAnimation = AnimationUtils.loadAnimation(this@MainActivity,R.anim.blink)
                timeLeftTextView.startAnimation(blinkAnimation)
                timeLeftTextView.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }
        gameStarted = false
    }
    private fun incrementScore(){
        if(!gameStarted){
            startGame()
        }
        score += 1
        val newScore = getString(R.string.yourScore, score)
        gameScoreTextView.text = newScore

        val blinkAnimation = AnimationUtils.loadAnimation(this,R.anim.blink)
        gameScoreTextView.startAnimation(blinkAnimation)
    }

    private fun startGame(){
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame(){
        Toast.makeText(this,getString(R.string.gameOverMessage, score), Toast.LENGTH_LONG).show()
        resetGame()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.info, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.about){
            showInfo()
        }
        return true

    }

    @SuppressLint("StringFormatInvalid")
    private fun showInfo(){
        val dialogTitle = getString(R.string.aboutTitle, BuildConfig.VERSION_CODE)
        val dialogMessage = getString(R.string.aboutMessage)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }
}
