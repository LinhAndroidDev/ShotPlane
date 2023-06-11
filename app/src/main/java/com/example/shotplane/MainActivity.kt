package com.example.shotplane

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Display
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    var screenWidth: Int = 0
    var screenHeight: Int = 0
    var xDown: Float = 0f
    var yDown: Float = 0f
    var TIME_CHANGE: Int = 0
    lateinit var increasePoint: MediaPlayer
    lateinit var fail: MediaPlayer
    lateinit var enlarge: Animation
    lateinit var zoomOut: Animation
    lateinit var createBullet: CountDownTimer
    lateinit var moveBullet: CountDownTimer
    var listHeader: ArrayList<ImageView> = arrayListOf()
    var listCount: ArrayList<Int> = arrayListOf()
    var listSpeech: ArrayList<Int> = arrayListOf()
    lateinit var moveItemTime: CountDownTimer
    var SCORE: Int = 50

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        increasePoint = MediaPlayer.create(this,R.raw.getpoint)
        fail = MediaPlayer.create(this,R.raw.tiengbaoloi)
        enlarge = AnimationUtils.loadAnimation(this, R.anim.enlarge)
        zoomOut = AnimationUtils.loadAnimation(this, R.anim.zoom_out)
        score.text = "Điểm của bạn $SCORE"

        //add list header
        listHeader.add(header1)
        listHeader.add(header2)
        listHeader.add(header3)
        listHeader.add(header4)
        listHeader.add(header5)
        listHeader.add(header6)

        //add count
        for(i in 0 until listHeader.size){
            listCount.add(0)
        }

        //add speed header
        for(i in 0 until listHeader.size){
            listSpeech.add(1)
        }

        setMoveItem()

        setWindow()

        getSizeWindow()

        moveBackGround()

        setPositionItem()

        initUi()
    }

    private fun setMoveItem() {
        moveItemTime = object : CountDownTimer(600000, 10) {
            @SuppressLint("SetTextI18n")
            override fun onTick(p0: Long) {
                for(i in 0 until listHeader.size){
                    listHeader[i].y = listHeader[i].y + listSpeech[i]
                    if (listHeader[i].y - screenHeight >= 0) {
                        if(listHeader[i].visibility == View.VISIBLE && SCORE > 0){
                            SCORE -= 10
                            score.text = "Điểm của bạn $SCORE"
                        }
                        setVisibleHeaderAgain(listHeader[i])
                    }
                    setCollidePLaneWithHeader(listHeader[i],plane)
                }
                TIME_CHANGE += 10
            }

            override fun onFinish() {

            }
        }
    }

    private fun moveBackGround() {
        object : CountDownTimer(600000, 10) {
            override fun onTick(p0: Long) {
                screenFirst.y = screenFirst.y + 6
                screenSecond.y = screenSecond.y + 6
                if ((screenFirst.y - screenHeight) >= 0) {
                    screenFirst.y = (-1 * screenHeight).toFloat()
                }
                if ((screenSecond.y - screenHeight) >= 0) {
                    screenSecond.y = (-1 * screenHeight).toFloat()
                }
            }

            override fun onFinish() {

            }

        }.start()
    }

    private fun setPositionItem() {
        screenSecond.y = (-1 * screenHeight).toFloat()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUi() {
        play.setOnClickListener {
            createBullet()
            plane.isEnabled = true
            plane.x = (screenWidth/2 - plane.width/2).toFloat()
            plane.y = (screenHeight/2 - plane.height/2).toFloat()
            play.startAnimation(zoomOut)
            play.visibility = View.GONE
            SCORE = 50
            setVisibleHeader()

            moveItemTime.start()
        }

        //move plane
        plane.setOnTouchListener { _, even ->
            when (even?.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    xDown = even.x
                    yDown = even.y
                }

                MotionEvent.ACTION_MOVE -> {
                    val movedX: Float = even.x
                    val movedY: Float = even.y

                    /** Calcualate how much the user moved */
                    /** Calcualate how much the user moved */
                    val distanceX: Float = movedX - xDown
                    val distanceY: Float = movedY - yDown

                    /** Now move view to that position */

                    /** Now move view to that position */
                    plane.x = plane.x + distanceX
                    plane.y = plane.y + distanceY
                }
            }
            true
        }
    }

    private fun createBullet() {
        createBullet = object : CountDownTimer(600000, 200) {
            override fun onTick(p0: Long) {
                val bullet = ImageView(applicationContext)
                val params = RelativeLayout.LayoutParams(30, 45)
                bullet.layoutParams = params
                layoutGame.addView(bullet)
                bullet.setBackgroundResource(R.drawable.icon_dan)
                bullet.x = plane.x + 45
                bullet.y = plane.y - 30

                moveBullet = object : CountDownTimer(600000, 10) {
                    @SuppressLint("SetTextI18n")
                    override fun onTick(p0: Long) {
                        bullet.y = bullet.y - 5
                        for(i in 0 until listHeader.size){
                            if(setCollideBulletWithHeader(listHeader[i], bullet, listCount[i]) == 0){
                                listHeader[i].visibility = View.GONE
                                bullet.visibility = View.GONE
                                setVisibleHeaderAgain(listHeader[i])
                                increasePoint.start()
                                listCount[i] = 0
                                listSpeech[i] = 1
                                SCORE += 10
                                score.text = "Điểm của bạn $SCORE"
                            }else if(setCollideBulletWithHeader(listHeader[i], bullet, listCount[i]) == 1){
                                bullet.visibility = View.GONE
                                bullet.y = bullet.y + 100000
                                bullet.x = bullet.x + 100000
                                listCount[i]++
                                listSpeech[i] = listCount[i] + 2
                            }
                        }
                    }

                    override fun onFinish() {

                    }

                }.start()
            }

            override fun onFinish() {

            }

        }.start()
    }

    private fun setCollideBulletWithHeader(header: ImageView, bullet: ImageView, count: Int) : Int{
        val distanceFromBulletToHeader = sqrt(
            abs((header.x + header.width / 2) - bullet.x).toDouble()
                .pow(2.0) + abs((header.y + header.height / 6) - bullet.y).toDouble()
                .pow(2.0)
            )

        if (-70 <= distanceFromBulletToHeader && distanceFromBulletToHeader <= 70 && header.y >= 0) {
            if (header.visibility == View.VISIBLE) {
                return if(count == 5){
                    0
                }else{
                    1
                }
            }
        }
        return 2
    }

    private fun setCollidePLaneWithHeader(header: ImageView, pl: ImageView){
        val distanceFromPlaneToHeader = sqrt(
            abs((header.x + header.width / 2) - (pl.x + pl.width/2)).toDouble()
                .pow(2.0) + abs((header.y + header.height / 6) - (pl.y + pl.height/2)).toDouble()
                .pow(2.0)
        )
        if(-70 <= distanceFromPlaneToHeader && distanceFromPlaneToHeader <= 70
            && header.y >= 0 && header.visibility == View.VISIBLE){
            fail.start()
            moveItemTime.cancel()
            createBullet.cancel()
            plane.isEnabled = false
            play.visibility = View.VISIBLE
            play.startAnimation(enlarge)
        }
    }

    private fun setVisibleHeader() {
        val random: java.util.Random = java.util.Random()
        do {
            for(i in 0 until listHeader.size){
                listHeader[i].y = random.nextInt(screenHeight / 2) * (-1).toFloat()
            }
        } while (header1.y == header2.y || header1.y == header3.y || header1.y == header4.y || header1.y == header5.y || header1.y == header6.y ||
            header2.y == header3.y || header2.y == header4.y || header2.y == header5.y || header2.y == header6.y ||
            header3.y == header4.y || header3.y == header5.y || header3.y == header6.y ||
            header4.y == header5.y || header4.y == header5.y ||
            header5.y == header6.y
        )

        for(i in 0 until listHeader.size){
            listHeader[i].visibility = View.VISIBLE
            setAnimationView(listHeader[i])
        }
    }

    private fun setVisibleHeaderAgain(header: ImageView){
        val random: java.util.Random = java.util.Random()
        header.y = random.nextInt(screenHeight / 2) * (-1).toFloat()
        header.visibility = View.VISIBLE
        setAnimationView(header)
    }

    private fun setAnimationView(image: ImageView) {
        val anim = ValueAnimator.ofFloat(1f, 0.7f)
        anim.duration = 1000
        anim.addUpdateListener { animation ->
            image.scaleX = animation.animatedValue as Float
            image.scaleY = animation.animatedValue as Float
        }
        anim.repeatCount = 20
        anim.repeatMode = ValueAnimator.REVERSE
        anim.start()
    }

    private fun getSizeWindow() {
        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
        screenHeight = size.y
    }

    private fun setWindow() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}