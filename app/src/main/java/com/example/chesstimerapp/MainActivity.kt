package com.example.chesstimerapp

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.chesstimerapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var player1TimeMillis: Long = 0
    private var player2TimeMillis: Long = 0
    private var activePlayer = 1
    private var lastStartTime: Long = 0L
    private var isPaused = false
    private var totalTimeMillis: Long = 0L
    private lateinit var name1: String
    private lateinit var name2: String
    private var warningPlayedP1 = false
    private var warningPlayedP2 = false
    private var mediaPlayer: MediaPlayer? = null
    private val player1Moves = mutableListOf<String>()
    private val player2Moves = mutableListOf<String>()

    private var timer: CountDownTimer? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        name1 = intent.getStringExtra("name1") ?: "Player 1"
        name2 = intent.getStringExtra("name2") ?: "Player 2"
        binding.Name1.text = name1
        binding.Name2.text = name2
        totalTimeMillis = intent.getIntExtra("total_time", 5).toLong() * 60 * 1000
        player1TimeMillis = totalTimeMillis
        player2TimeMillis = totalTimeMillis
        binding.Name1.setOnClickListener {
            val intent = Intent(this, History::class.java)
            intent.putExtra("playerName", name1)
            intent.putStringArrayListExtra("moveTimes", ArrayList(player1Moves))
            startActivity(intent)
        }

        binding.Name2.setOnClickListener {
            val intent = Intent(this, History::class.java)
            intent.putExtra("playerName", name2)
            intent.putStringArrayListExtra("moveTimes", ArrayList(player2Moves))
            startActivity(intent)
        }

        showStartCard()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showStartCard() {
        binding.CardView3.visibility = View.VISIBLE
        val rules = """
    1. Starting the Game
    i.Tap the Start button to begin the timer.
    ii.The timer starts for Player 1 by default.

    2. Switching Turns
    i.Each player has a dedicated timer area.
    ii.When a player completes their move, they must tap their dedicated screen area to switch turns.
    iii.The timer will pause for the current player and start for the other player.

    3. Time Countdown
    i.Each player has a fixed amount of time to use throughout the game.
    ii.The timer counts downwards.
    iii.If a player's time reaches zero, they lose on time.

    4. Pausing the Game
    i.You can pause the entire game using the Pause button.
    ii.During pause, both timers will stop.

    5. Resetting the Game
    i.Tap the Stop button and then Reset button to restart both timers and clear any progress.

    6. Viewing Time History
    i.Tap on a Player's name to view their move-by-move time history.

    7. Custom Time Settings 
    i.Before starting the game, you can set custom timer durations for each player.
    ii.You can choose different time control styles like:
    - Classical (e.g., 30 minutes each)
    - Rapid (e.g., 10 minutes each)
    - Blitz (e.g., 5 minutes each)
    - Bullet (e.g., 1 minute each)

    8. Fair Play
    i.Players must tap immediately after completing their move to avoid running down their own clock.
    ii.Any disputes must be resolved by mutual agreement.

    9. End of Game
    i.The game ends when:
    - A player runs out of time, or
    - The game is completed manually, or
    - A player resets or stops the timer manually.
""".trimIndent()

        binding.Rules.text = rules
        binding.StartBtn.setOnClickListener {
            binding.CardView3.visibility = View.GONE
            gameSetUp()
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun gameSetUp() {
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


    @SuppressLint("DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun switchPlayer() {
        timer?.cancel()
        mediaPlayer?.pause()
        val currentPlayerTimeMillis =
            if (activePlayer == 1) player1TimeMillis else player2TimeMillis
        val minutes = (currentPlayerTimeMillis / 1000) / 60
        val seconds = (currentPlayerTimeMillis / 1000) % 60
        val formattedTime = String.format("%02d:%02d", minutes, seconds)

        if (activePlayer == 1) {
            player1Moves.add(formattedTime)
        } else {
            player2Moves.add(formattedTime)
        }
        activePlayer = if (activePlayer == 1) 2 else 1


        val newTimeMillis = if (activePlayer == 1) player1TimeMillis else player2TimeMillis
        if (newTimeMillis <= 30_000L) {
            playWarningSound()
        }
        updateUI()
        startTimer()
    }

    @SuppressLint("SetTextI18n")
    private fun resetTimers(time: Long) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        player1TimeMillis = time
        player2TimeMillis = time
        activePlayer = 1
        isPaused = false
        binding.Fab1.setImageResource(R.drawable.pause)
        binding.Fab3.setImageResource(R.drawable.pause)
        binding.Fab1TV.text = "Pause"
        binding.Fab3TV.text = "Pause"
        binding.InnerCardView1.setBackgroundColor(
            ContextCompat.getColor(
                this@MainActivity, R.color.cardbg
            )
        )
        binding.InnerCardView3.setBackgroundColor(
            ContextCompat.getColor(
                this@MainActivity, R.color.cardbg
            )
        )
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
        mediaPlayer?.pause()

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
        val timeLeft = if (activePlayer == 1) player1TimeMillis else player2TimeMillis
        if (timeLeft <= 30_000L) {
            playWarningSound()
        }
        startTimer()
        if (activePlayer == 1) {
            binding.Fab1.setImageResource(R.drawable.pause)
            binding.Fab1TV.text = "Pause"
        } else {
            binding.Fab3.setImageResource(R.drawable.pause)
            binding.Fab3TV.text = "Pause"
        }
    }

    private fun playWarningSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.timer_warning)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } else if (!mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
        }
    }

    private fun playClickSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.click)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } else if (!mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
        }
    }

    private fun startTimer() {
        timer?.cancel()
        lastStartTime = System.currentTimeMillis()

        val startingTime = if (activePlayer == 1) player1TimeMillis else player2TimeMillis

        timer = object : CountDownTimer(startingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000

                if (activePlayer == 1) {
                    player1TimeMillis = millisUntilFinished
                    if (secondsLeft == 30L && !warningPlayedP1) {
                        playWarningSound()
                        warningPlayedP1 = true
                        binding.InnerCardView1.setBackgroundColor(
                            ContextCompat.getColor(
                                this@MainActivity, R.color.warningColor
                            )
                        )
                    }
                } else {
                    player2TimeMillis = millisUntilFinished
                    if (secondsLeft == 30L && !warningPlayedP2) {
                        playWarningSound()
                        warningPlayedP2 = true
                        binding.InnerCardView3.setBackgroundColor(
                            ContextCompat.getColor(
                                this@MainActivity, R.color.warningColor
                            )
                        )
                    }
                }
                updateTimerUI()
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null

                if (activePlayer == 1) {
                    player1TimeMillis = 0
                    binding.Time1.text = "Time's up!"
                } else {
                    player2TimeMillis = 0
                    binding.Time2.text = "Time's up!"
                }
                updateTimerUI()
                binding.Fab1.isEnabled = false
                binding.Fab3.isEnabled = false
                binding.InnerCardView1.isEnabled = false
                binding.InnerCardView3.isEnabled = false
                binding.InnerCardView1.isClickable = false
                binding.InnerCardView3.isClickable = false
                AlertDialog.Builder(this@MainActivity).setTitle("Game Over")
                    .setMessage("Player ${if (activePlayer == 1) name1 else name2} lost! Do you want to restart?")
                    .setPositiveButton("Yes") { _, _ -> resetTimers(totalTimeMillis) }
                    .setNegativeButton("No", null).show()


            }
        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        updateTimerUI()

        isPaused = false
        binding.Fab1.setImageResource(R.drawable.pause)
        binding.Fab3.setImageResource(R.drawable.pause)
        binding.Fab1TV.text = "Pause"
        binding.Fab3TV.text = "Pause"

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