package com.example.chesstimerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chesstimerapp.databinding.ActivityPlayerSelectionBinding

class PlayerSelection : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerSelectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.startButton.setOnClickListener {
            val name1 = binding.player1Input.text.toString()
            val name2 = binding.player2Input.text.toString()
            val totalTime = binding.timeInput.text.toString()
            if (name1.isEmpty() || name2.isEmpty() || totalTime.isEmpty()){
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
            }
            else{
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("name1",name1)
                intent.putExtra("name2",name2)
                intent.putExtra("total_time",totalTime.toInt())
                startActivity(intent)
            }


        }

    }
}