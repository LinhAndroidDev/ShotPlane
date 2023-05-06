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
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.text.Typography.bullet


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    var screenWidth: Int = 0
    var screenHeight: Int = 0
    var xDown: Float = 0f
    var yDown: Float = 0f
    var TIME_CHANGE: Int = 0
    var count1: Int = 0
    var count2: Int = 0
    var count3: Int = 0
    var count4: Int = 0
    var count5: Int = 0
    var count6: Int = 0
    lateinit var increasePoint: MediaPlayer
    lateinit var createBullet: CountDownTimer
    lateinit var moveBullet: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        increasePoint = MediaPlayer.create(this,R.raw.getpoint)

        setWindow()

        getSizeWindow()

        moveBackGround()

        setPositionItem()

        initUi()
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

//            plane.x = ((screenWidth/2 - plane.width/2).toFloat())
//            plane.y = ((screenHeight/2 - plane.height/2).toFloat())

            createBullet()

            play.visibility = View.GONE
            setVisibleHeader()

            object : CountDownTimer(600000, 10) {
                override fun onTick(p0: Long) {
                    header1.y = header1.y + 1
                    header2.y = header1.y + 1
                    header3.y = header1.y + 1
                    header4.y = header1.y + 1
                    header5.y = header1.y + 1
                    header6.y = header1.y + 1
                    if (header1.y - screenHeight >= 0) {
                        setVisibleHeader()
                    }
                    TIME_CHANGE += 10
                }

                override fun onFinish() {

                }

            }.start()
        }

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
                val params = RelativeLayout.LayoutParams(40, 60)
                bullet.layoutParams = params
                layoutGame.addView(bullet)
                bullet.setBackgroundResource(R.drawable.icon_dan)
                bullet.x = plane.x + 40
                bullet.y = plane.y - 40

                moveBullet = object : CountDownTimer(600000, 10) {
                    override fun onTick(p0: Long) {
                        bullet.y = bullet.y - 5
                        if(setCollide(header1, bullet, count1) == 0){
                            header1.visibility = View.GONE
                            bullet.visibility = View.GONE
                            increasePoint.start()
                            count1 = 0
                        }else if(setCollide(header1, bullet, count1) == 1){
                            bullet.visibility = View.GONE
                            bullet.y = bullet.y + 500
                            count1++
                        }

                        if(setCollide(header2, bullet, count2) == 0){
                            header2.visibility = View.GONE
                            bullet.visibility = View.GONE
                            increasePoint.start()
                            count2 = 0
                        }else if(setCollide(header2, bullet, count2) == 1){
                            bullet.visibility = View.GONE
                            bullet.y = bullet.y + 500
                            count2++
                        }

                        if(setCollide(header3, bullet, count3) == 0){
                            header3.visibility = View.GONE
                            bullet.visibility = View.GONE
                            increasePoint.start()
                            count3 = 0
                        }else if(setCollide(header3, bullet, count3) == 1){
                            bullet.visibility = View.GONE
                            bullet.y = bullet.y + 500
                            count3++
                        }

                        if(setCollide(header4, bullet, count4) == 0){
                            header4.visibility = View.GONE
                            bullet.visibility = View.GONE
                            increasePoint.start()
                            count4 = 0
                        }else if(setCollide(header4, bullet, count4) == 1){
                            bullet.visibility = View.GONE
                            bullet.y = bullet.y + 500
                            count4++
                        }

                        if(setCollide(header5, bullet, count5) == 0){
                            header5.visibility = View.GONE
                            bullet.visibility = View.GONE
                            increasePoint.start()
                            count5 = 0
                        }else if(setCollide(header5, bullet, count5) == 1){
                            bullet.visibility = View.GONE
                            bullet.y = bullet.y + 500
                            count5++
                        }

                        if(setCollide(header6, bullet, count6) == 0){
                            header6.visibility = View.GONE
                            bullet.visibility = View.GONE
                            increasePoint.start()
                            count6 = 0
                        }else if(setCollide(header6, bullet, count6) == 1){
                            bullet.visibility = View.GONE
                            bullet.y = bullet.y + 500
                            count6++
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

    private fun setCollide(header: ImageView, bullet: ImageView, count: Int) : Int{
        val distanceFromBulletToHeader = sqrt(
            abs((header.x + header.width / 2) - bullet.x).toDouble()
                .pow(2.0) + abs((header.y + header.height / 2) - bullet.y).toDouble()
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

    private fun setVisibleHeader() {
        val random: java.util.Random = java.util.Random()
        do {
            header1.y = random.nextInt(screenHeight / 2) * (-1).toFloat()
            header2.y = random.nextInt(screenHeight / 2) * (-1).toFloat()
            header3.y = random.nextInt(screenHeight / 2) * (-1).toFloat()
            header4.y = random.nextInt(screenHeight / 2) * (-1).toFloat()
            header5.y = random.nextInt(screenHeight / 2) * (-1).toFloat()
            header6.y = random.nextInt(screenHeight / 2) * (-1).toFloat()
        } while (header1.y == header2.y || header1.y == header3.y || header1.y == header4.y || header1.y == header5.y || header1.y == header6.y ||
            header2.y == header3.y || header2.y == header4.y || header2.y == header5.y || header2.y == header6.y ||
            header3.y == header4.y || header3.y == header5.y || header3.y == header6.y ||
            header4.y == header5.y || header4.y == header5.y ||
            header5.y == header6.y
        )

        header1.visibility = View.VISIBLE
        header2.visibility = View.VISIBLE
        header3.visibility = View.VISIBLE
        header4.visibility = View.VISIBLE
        header5.visibility = View.VISIBLE
        header6.visibility = View.VISIBLE

        setAnimationView(header1)
        setAnimationView(header2)
        setAnimationView(header3)
        setAnimationView(header4)
        setAnimationView(header5)
        setAnimationView(header6)
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