package com.example.candycrushgame

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.widget.OnSwipe
import com.example.candycrushgame.uiltel.onSwipeListener
import java.util.Arrays.asList
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences

    // create transparent drawable
    //adding candies

    var candies = intArrayOf(
        R.drawable.bluecandy,
        R.drawable.greencandy,
        R.drawable.orangecandy,
        R.drawable.purplecandy,
        R.drawable.redcandy,
        R.drawable.yellowcandy,
    )

    var widthOfBlock: Int = 0
    var noOfBlock: Int = 8
    var widthOfScreen: Int = 0
    lateinit var candy: ArrayList<ImageView>
    var candyToBeDragged: Int = 0
    var candyToBeReplaced: Int = 0
    var notCandy: Int = R.drawable.transparent

    lateinit var mHandler: Handler
    private lateinit var scoreReult: TextView
    var score = 0
    var interval = 100L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreReult = findViewById(R.id.score)

        sharedPreferences = getSharedPreferences("candy_crush_preferences", MODE_PRIVATE)
        score = sharedPreferences.getInt("score", 0)
        scoreReult.text = "$score"

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        widthOfScreen = displayMetrics.widthPixels

        var heightOfScreen = displayMetrics.heightPixels

        widthOfBlock = widthOfScreen / noOfBlock

        candy = ArrayList()
        createBoard()


        for (imageView in candy) {

            imageView.setOnTouchListener(
                object : onSwipeListener(this) {
                    override fun onSwipeRight() {
                        super.onSwipeRight()
                        candyToBeDragged = imageView.id
                        candyToBeReplaced = candyToBeDragged + 1
                        candyInterChange()
                    }

                    override fun onSwipeLift() {
                        super.onSwipeLift()
                        candyToBeDragged = imageView.id
                        candyToBeReplaced = candyToBeDragged - 1
                        candyInterChange()
                    }

                    override fun onSwipeTop() {
                        super.onSwipeBottom()
                        candyToBeDragged = imageView.id
                        candyToBeReplaced = candyToBeDragged + noOfBlock
                        candyInterChange()

                    }

                    override fun onSwipeBottom() {
                        super.onSwipeTop()
                        candyToBeDragged = imageView.id
                        candyToBeReplaced = candyToBeDragged - noOfBlock
                        candyInterChange()

                    }
                }
            )

        }

        mHandler = Handler()
        startRepeat()
    }

    

    private fun candyInterChange() {

        var background: Int = candy.get(candyToBeReplaced).tag as Int
        var background1: Int = candy.get(candyToBeDragged).tag as Int

        candy.get(candyToBeDragged).setImageResource(background)
        candy.get(candyToBeReplaced).setImageResource(background1)

        candy.get(candyToBeDragged).setTag(background)
        candy.get(candyToBeReplaced).setTag(background1)
    }

    private fun updateScore(points: Int) {
        // Update the score
        score += points

        // Update the TextView to display the new score
        scoreReult.text = "$score"

        // Save the updated score in SharedPreferences
        with(sharedPreferences.edit()) {
            putInt("score", score)
            apply()
        }
    }



    private fun checkRowForThree() {
        for (i in 0..61) {
            var chosedCandy = candy.get(i).tag
            var isBlank: Boolean = candy.get(i).tag == notCandy
            val notValid = arrayOf(6, 7, 14, 15, 22, 23, 30, 31, 38, 39, 46, 47, 54, 55)
            val list = asList(*notValid)
            if (!list.contains(i)) {
                var x = i
                if (candy.get(x++).tag as Int == chosedCandy
                    && !isBlank
                    && candy.get(x++).tag as Int == chosedCandy
                    && candy.get(x).tag as Int == chosedCandy
                ) {
                    score = score + 3
                    scoreReult.text = "$score"
                    candy.get(x).setImageResource(notCandy)
                    candy.get(x).setTag(notCandy)
                    x--
                    candy.get(x).setImageResource(notCandy)
                    candy.get(x).setTag(notCandy)
                    x--
                    candy.get(x).setImageResource(notCandy)
                    candy.get(x).setTag(notCandy)

                }
            }

        }
        moveDownCandies()
    }
    @SuppressLint("SuspiciousIndentation")
    private fun checkColumnForThree() {
        for (i in 0..47) {
            var chosedCandy = candy.get(i).tag
            var isBlank: Boolean = candy.get(i).tag == notCandy
            var x = i
                if (candy.get(x).tag as Int == chosedCandy
                    && !isBlank
                    && candy.get(x+noOfBlock).tag as Int == chosedCandy
                    && candy.get(x+2*noOfBlock).tag as Int == chosedCandy
                ) {
                    score = score + 3
                    scoreReult.text = "$score"
                    candy.get(x).setImageResource(notCandy)
                    candy.get(x).setTag(notCandy)
                    x = x + noOfBlock
                    candy.get(x).setImageResource(notCandy)
                    candy.get(x).setTag(notCandy)
                    x = x + noOfBlock
                    candy.get(x).setImageResource(notCandy)
                    candy.get(x).setTag(notCandy)

                }


        }
        moveDownCandies()

    }
    private fun moveDownCandies() {

        val firstRow = arrayOf(1,2,3,4,5,6,7)
        val list = asList(*firstRow)
        for (i in 55 downTo 0){
            if (candy.get(i+noOfBlock).tag as Int == notCandy){
                candy.get(i+noOfBlock).setImageResource(candy.get(i).tag as Int)
                candy.get(i+noOfBlock).setTag(candy.get(i).tag as Int)

                candy.get(i).setImageResource(notCandy)
                candy.get(i).setTag(notCandy)
                if (list.contains(i) && candy.get(i).tag == notCandy){
                    var randomColor :Int = Math.abs(Math.random() * candies.size).toInt()
                    candy.get(i).setImageResource(candies[randomColor])
                    candy.get(i).setTag(candies[randomColor])
                }
            }
        }
        for (i in 0..7){
            if (candy.get(i).tag as Int == notCandy){

                var randomColor :Int = Math.abs(Math.random() * candies.size).toInt()
                candy.get(i).setImageResource(candies[randomColor])
                candy.get(i).setTag(candies[randomColor])
            }
        }

    }
    val repeatChecker :Runnable = object :Runnable{
        override fun run() {
            try {
                checkRowForThree()
                checkColumnForThree()
                moveDownCandies()
            }
            finally {
                mHandler.postDelayed(this,interval)
            }
        }

    }
    private fun startRepeat() {
        repeatChecker.run()

    }

    private fun createBoard() {

        val gridLayout = findViewById<GridLayout>(R.id.board)
        gridLayout.rowCount = noOfBlock
        gridLayout.columnCount = noOfBlock
        gridLayout.layoutParams.width = widthOfScreen
        gridLayout.layoutParams.height = widthOfScreen

        for (i in 0 until noOfBlock * noOfBlock){
            val imageView = ImageView(this)
            imageView.id = i
            imageView.layoutParams = android.view.ViewGroup.LayoutParams(widthOfBlock,widthOfBlock)

            imageView.maxHeight = widthOfBlock
            imageView.maxWidth = widthOfBlock

            var random :Int = Math.floor(Math.random() * candies.size).toInt()

                //randomIndex from candies array
            imageView.setImageResource(candies[random])
            imageView.setTag(candies[random])
            candy.add(imageView)
            gridLayout.addView(imageView)
        }




    }


}


