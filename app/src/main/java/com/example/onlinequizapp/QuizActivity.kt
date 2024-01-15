package com.example.onlinequizapp

import android.graphics.Color
import android.health.connect.datatypes.units.Length
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.onlinequizapp.databinding.ActivityQuizBinding
import com.example.onlinequizapp.databinding.ScoreDialogBinding

class QuizActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityQuizBinding

    private var currentQuestionIndex = 0
    var selectedAnswer = ""
    var score = 0

    companion object {
        var questionModelList: List<QuestionModel> = listOf()
        var time: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.apply {
            buttonA.setOnClickListener(this@QuizActivity)
            buttonB.setOnClickListener(this@QuizActivity)
            buttonC.setOnClickListener(this@QuizActivity)
            buttonD.setOnClickListener(this@QuizActivity)
            buttonNext.setOnClickListener(this@QuizActivity)

        }

        loadQuestion()

        startTimer()
    }

    private fun startTimer() {
        val totalTimeInMilliseconds = time.toInt() * 60 * 1000L
        object : CountDownTimer(totalTimeInMilliseconds,1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60

                binding.timerIndicatorText.text = String.format("%02d:%02d", minutes, remainingSeconds)
            }

            override fun onFinish() {
                //  It sets condition to finish quiz
            }

        }.start()
    }

    private fun loadQuestion() {

        selectedAnswer = ""

        if(currentQuestionIndex == questionModelList.size) {
            finishQuiz()
            return
        }

        binding.apply {
            questionNoIndicatorText.text = "Question ${currentQuestionIndex+1}/${questionModelList.size}"
            questionProgressIndicator.progress = ((currentQuestionIndex.toFloat() / questionModelList.size.toFloat()) * 100).toInt()
            questionText.text = questionModelList[currentQuestionIndex].question

            buttonA.text = questionModelList[currentQuestionIndex].options[0]
            buttonB.text = questionModelList[currentQuestionIndex].options[1]
            buttonC.text = questionModelList[currentQuestionIndex].options[2]
            buttonD.text = questionModelList[currentQuestionIndex].options[3]

        }
    }

    override fun onClick(v: View?) {

        binding.apply {
            buttonA.setBackgroundColor(getColor(R.color.grey))
            buttonB.setBackgroundColor(getColor(R.color.grey))
            buttonC.setBackgroundColor(getColor(R.color.grey))
            buttonD.setBackgroundColor(getColor(R.color.grey))

        }

        val clickedButton = v as Button

        if(clickedButton.id == R.id.button_next) {
            // next Button

            if(selectedAnswer.isEmpty()) {
                Toast.makeText(this,"Please select an answer to continue", Toast.LENGTH_SHORT).show()
                return
            }

            if(selectedAnswer == questionModelList[currentQuestionIndex].correct) {
                score++
                Log.d("Score of quiz: ",score.toString())
            }

            currentQuestionIndex++
            loadQuestion()

        }
        else {
            // options button is clicked

            selectedAnswer = clickedButton.text.toString()
            clickedButton.setBackgroundColor(getColor(R.color.orange))
        }
    }

    private fun finishQuiz() {
        val totalQuestion = questionModelList.size
        val percentage = ((score.toFloat() / totalQuestion.toFloat()) * 100).toInt()

        val dialogBinding = ScoreDialogBinding.inflate(layoutInflater)

        dialogBinding.apply {
            scoreProgressIndicator.progress = percentage
            scoreProgressValueText.text = "$percentage %"

            if(percentage > 60) {
                scoreTitle.text = "Congrats! You have passed"
                scoreTitle.setTextColor(Color.BLUE)
            }
            else {
                scoreTitle.text = "Oops! You have failed"
                scoreTitle.setTextColor(Color.RED)
            }

            scoreDescriptionSubtitleText.text = "$score out of $totalQuestion are correct"

            finishButton.setOnClickListener {
                finish()
            }
        }

        AlertDialog.Builder(this).setView(dialogBinding.root).setCancelable(false).show()
    }
}