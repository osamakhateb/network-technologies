package com.example.pomodoro

import android.content.IntentSender.OnFinished
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.example.pomodoro.R
//import com.example.pomodoro.databinding.ActivityMainBinding
import com.example.pomodoro.databinding.ActivityFeedBinding

import java.lang.Exception

class FeedActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityMainBinding
    private lateinit var binding: ActivityFeedBinding

    private var studyMinute: Int? = null
    private var breakMinute: Int? = null
    private var roundCount: Int? = null

    private var restTimer: CountDownTimer? = null
    private var studyTimer: CountDownTimer? = null
    private var breakTimer: CountDownTimer? = null

    private var mRound = 1

    private var isStudy = true

    private var isStop = false

    private var mp: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studyMinute = intent.getIntExtra("study", 0) * 60 * 1000
        breakMinute = intent.getIntExtra("break", 0) * 60 * 1000
        roundCount = intent.getIntExtra("round", 0)

        binding.tvRound.text = "$mRound/$roundCount"

        setRestTimer()

        binding.ivStop.setOnClickListener{
            resetOrStart()
        }

    }

    private fun setRestTimer() {
         playSound()
        binding.tvStatus.text = "Get Ready"
        binding.progressBar.progress = 0
        binding.progressBar.max = 10
        restTimer = object : CountDownTimer(10500, 1000) {
            override fun onTick(p0: Long) {
                binding.progressBar.progress = (p0 / 1000).toInt()
                binding.tvTimer.text = (p0 / 1000).toString()
            }

            override fun onFinish() {
                mp?.reset()
                if (isStudy) {
                    setupStusyView()

                } else {
                    setupBreakView()
                }
            }

        }.start()
    }

    private fun setupBreakView() {
        binding.tvStatus.text = "Break timer"
        binding.progressBar.max = breakMinute!!/1000

        if (studyTimer!=null)
            studyTimer=null
        setbreakTimer()
    }

    private fun setbreakTimer() {
        studyTimer = object : CountDownTimer(breakMinute!!.toLong() + 500, 1000) {
            override fun onTick(p0: Long) {
                binding.progressBar.progress = (p0 / 1000).toInt()
                binding.tvTimer.text = createTimeLabel(p0.toInt())
            }

            override fun onFinish() {
                isStudy = true
                setRestTimer()

            }
        }.start()
    }

    private fun setupStusyView() {
        binding.tvRound.text = "$mRound/$roundCount"
        binding.tvStatus.text = "study time"
        binding.progressBar.max = studyMinute!!/1000

        if (studyTimer!=null)
            studyTimer=null
        setRestTimer()
    }

    private fun playSound() {
        try {
            val soundUrl = Uri.parse("android.resource://com.example.pomodoro/"+ R.raw.vi)
            mp = MediaPlayer.create(this,soundUrl)
            mp?.isLooping = false
            mp?.start()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun setStudyTimer() {
        studyTimer = object : CountDownTimer(studyMinute!!.toLong() + 500, 1000) {
            override fun onTick(p0: Long) {
                binding.progressBar.progress = (p0 / 1000).toInt()
                binding.tvTimer.text = createTimeLabel(p0.toInt())
            }

            override fun onFinish() {
                if (mRound<roundCount!!){
                    isStudy = false
                    setRestTimer()
                    mRound++
                }else{
                    clearAttribute()
                    binding.tvStatus.text = "You have finisf your rounds :)"
                }
            }

        }
    }

    private fun clearAttribute() {
        binding.tvStatus.text = "Press play Button to Restart"
        binding.ivStop.setImageResource(R.drawable.ic_play)
        binding.progressBar.progress = 0
        binding.tvTimer.text = "0"
        mRound = 1
        binding.tvRound.text = "$mRound/$roundCount"
        restTimer?.cancel()
        studyTimer?.cancel()
        breakTimer?.cancel()
        mp?.reset()
        isStop = true
    }

    private fun createTimeLabel(time: Int): String {

        var timeLabel = ""
        val minutes = time / 1000 / 60
        val secends = time / 1000 % 60

        if (minutes < 10) timeLabel += "0"
        timeLabel += "$minutes:"

        if (secends <10)timeLabel +="0"
        timeLabel += secends

        return timeLabel

    }
    private fun resetOrStart(){
        if (isStop){
            binding.ivStop.setImageResource(R.drawable.ic_stop)
            setRestTimer()
            isStop = false
        }else{
            clearAttribute()
        }
    }


}



