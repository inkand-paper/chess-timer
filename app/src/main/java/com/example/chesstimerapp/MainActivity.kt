package com.example.chesstimerapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chesstimerapp.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var player1TimeMillis: Long = 0
    private var player2TimeMillis: Long = 0
    private var activePlayer = 1
    private var lastStartTime: Long = 0L
    private var isPaused = false
    private var totalTimeMillis: Long = 0L
    private var timer: CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.Name1.text = intent.getStringExtra("name1") ?: "Player 1"
        binding.Name2.text = intent.getStringExtra("name2") ?: "Player 2"
        totalTimeMillis = intent.getIntExtra("total_time", 5).toLong() * 60 * 1000
        player1TimeMillis = totalTimeMillis
        player2TimeMillis = totalTimeMillis

        showStartBottomSheet()

    }
    @SuppressLint("SetTextI18n", "MissingInflatedId", "InflateParams")
    private fun showStartBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        val tvRules = dialogView.findViewById<TextView>(R.id.TvStart)
        val nextBtn = dialogView.findViewById<MaterialButton>(R.id.NextBtn)

        tvRules.text = """Start The Game!"""
        nextBtn.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Enjoy the game!", Toast.LENGTH_SHORT).show()
            gameSetUp()
        }
        dialog.show()
    }

    private fun gameSetUp(){
        updateUI()
        binding.apply {
            Fab1.setOnClickListener {
                if (isPaused) resumeTimer() else pauseTimer()
            }
            Fab3.setOnClickListener {
                if (isPaused) resumeTimer() else pauseTimer()
            }
        }
        binding.apply {
            Fab2.setOnClickListener {
                stopTimer()
                isPaused = true
                InnerCardView2.visibility = View.GONE
                InnerCardViewForRetry1.visibility = View.VISIBLE
            }
            Fab4.setOnClickListener {
                stopTimer()
                isPaused = true
                InnerCardView4.visibility = View.GONE
                InnerCardViewForRetry2.visibility = View.VISIBLE
            }
        }
        binding.apply {
            FabRetry1.setOnClickListener {
                resetTimers(totalTimeMillis)
            }
            FabRetry2.setOnClickListener {
                resetTimers(totalTimeMillis)
            }
        }
        binding.apply {
            InnerCardView1.setOnClickListener {
                if (activePlayer == 1) switchPlayer()
            }
            InnerCardView3.setOnClickListener {
                if (activePlayer == 2) switchPlayer()
            }
        }
        startTimer()
    }


    private fun switchPlayer() {
        timer?.cancel()
        activePlayer = if (activePlayer == 1) 2 else 1
        updateUI()
        startTimer()
    }

    private fun resetTimers(time: Long) {
        player1TimeMillis = time
        player2TimeMillis = time
        activePlayer = 1
        isPaused = false
        binding.apply {
            InnerCardView2.visibility = View.VISIBLE
            InnerCardView4.visibility = View.VISIBLE
            InnerCardViewForRetry1.visibility = View.GONE
            InnerCardViewForRetry2.visibility = View.GONE

            binding.Fab1.isEnabled = true
            binding.Fab3.isEnabled = true
            binding.InnerCardView1.isEnabled = true
            binding.InnerCardView3.isEnabled = true
        }
        updateUI()
        startTimer()
    }

    private fun stopTimer() {
        timer?.cancel()
    }

    @SuppressLint("SetTextI18n")
    private fun pauseTimer() {
        isPaused = true
        timer?.cancel()
        if (activePlayer == 1) {
            binding.Fab1.setImageResource(R.drawable.play)
            binding.Fab1TV.text = "Play"
        } else {
            binding.Fab3.setImageResource(R.drawable.play)
            binding.Fab3TV.text = "Play"
        }
        updateTimerUI()
    }

    @SuppressLint("SetTextI18n")
    private fun resumeTimer() {
        isPaused = false
        startTimer()
        if (activePlayer == 1) {
            binding.Fab1.setImageResource(R.drawable.pause)
            binding.Fab1TV.text = "Pause"
        } else {
            binding.Fab3.setImageResource(R.drawable.pause)
            binding.Fab3TV.text = "Pause"
        }
    }

    private fun startTimer() {
        lastStartTime = System.currentTimeMillis()

        val startingTime = if (activePlayer == 1) player1TimeMillis else player2TimeMillis

        timer = object : CountDownTimer(startingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (activePlayer == 1) {
                    player1TimeMillis = millisUntilFinished
                } else {
                    player2TimeMillis = millisUntilFinished
                }
                updateTimerUI()
            }

            override fun onFinish() {
                if (activePlayer == 1) {
                    player1TimeMillis = 0
                } else {
                    player2TimeMillis = 0
                }
                updateTimerUI()
                binding.Fab1.isEnabled = false
                binding.Fab3.isEnabled = false
                binding.InnerCardView1.isEnabled = false
                binding.InnerCardView3.isEnabled = false
            }
        }.start()
    }

    private fun updateUI() {
        updateTimerUI()
        binding.InnerCardView1.visibility = if (activePlayer == 1) View.VISIBLE else View.GONE
        binding.InnerCardView3.visibility = if (activePlayer == 2) View.VISIBLE else View.GONE

        binding.InnerCardView2.visibility = if (activePlayer == 1) View.VISIBLE else View.GONE
        binding.InnerCardView4.visibility = if (activePlayer == 2) View.VISIBLE else View.GONE
    }

    @SuppressLint("DefaultLocale")
    private fun updateTimerUI() {
        val minutes1 = (player1TimeMillis / 1000) / 60
        val seconds1 = (player1TimeMillis / 1000) % 60
        val minutes2 = (player2TimeMillis / 1000) / 60
        val seconds2 = (player2TimeMillis / 1000) % 60

        binding.Time1.text = String.format("%02d:%02d", minutes1, seconds1)
        binding.Time2.text = String.format("%02d:%02d", minutes2, seconds2)
    }

}