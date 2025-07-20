package com.example.chesstimerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chesstimerapp.databinding.ActivityHistoryBinding

class History : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val playerName = intent.getStringExtra("playerName") ?: "Player"
        val moveTimes = intent.getStringArrayListExtra("moveTimes") ?: arrayListOf()

        binding.historyPlayerName.text = playerName
        binding.summaryValue.text = moveTimes.size.toString()

        val adapter = HistoryAdapter(moveTimes)
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = adapter

        binding.historyBack.setOnClickListener { finish() }
    }
}