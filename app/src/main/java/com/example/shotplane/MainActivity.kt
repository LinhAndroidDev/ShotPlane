package com.example.shotplane

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Display
import android.view.MotionEvent
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
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
    var listHeader: ArrayList<ImageView> = arrayListOf()
    var listCount: ArrayList<Int> = arrayListOf()
    var listSpeech: ArrayList<Int> = arrayListOf()
    var SCORE: Int = 50
    val compositeDisposable = CompositeDisposable()

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

        setWindow()

        getSizeWindow()

        moveBackGround()

        setPositionItem()

        initUi()
    }

    private fun setMoveItem() {
        val moveItemTime = Observable.interval(10, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                for(i in 0 until listHeader.size){
                    listHeader[i].y = listHeader[i].y + listSpeech[i]
                    if (listHeader[i].y + listHeader[i].height >= screenHeight) {
                        if(listHeader[i].visibility == View.VISIBLE && SCORE > 0){
                            SCORE -= 10
                            score.text = "Điểm của bạn $SCORE"
                        }
                        listCount[i] = 0
                        listSpeech[i] = 1
                        setVisibleHeaderAgain(listHeader[i])
                    }
                    setCollidePLaneWithHeader(listHeader[i],plane)
                }
                TIME_CHANGE += 10
            }

        compositeDisposable.add(moveItemTime)
    }

    private fun moveBackGround() {
        Observable.interval(40, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                screenFirst.y = screenFirst.y + 6
                screenSecond.y = screenSecond.y + 6
                if ((screenFirst.y - screenHeight) >= 0) {
                    screenFirst.y = (-1 * screenHeight).toFloat()
                }
                if ((screenSecond.y - screenHeight) >= 0) {
                    screenSecond.y = (-1 * screenHeight).toFloat()
                }
            }
    }

    private fun setPositionItem() {
        screenSecond.y = (-1 * screenHeight).toFloat()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUi() {
        play.setOnClickListener {
            rotationAndTranslate(test)
            createBullet()
            plane.isEnabled = true
            plane.x = (screenWidth/2 - plane.width/2).toFloat()
            plane.y = (screenHeight/2 - plane.height/2).toFloat()
            play.startAnimation(zoomOut)
            play.visibility = View.GONE
            SCORE = 50
            setVisibleHeader()

            setMoveItem()
        }

        //move plane
        plane.setOnTouchListener { _, even ->
            when (even?.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    /** Now move view to that position */
                    xDown = even.x
                    yDown = even.y
                }

                MotionEvent.ACTION_MOVE -> {
                    val movedX: Float = even.x
                    val movedY: Float = even.y

                    /** Calcualate how much the user moved */
                    val distanceX: Float = movedX - xDown
                    val distanceY: Float = movedY - yDown

                    plane.x = plane.x + distanceX
                    plane.y = plane.y + distanceY

                    if (plane.x < 0) {
                        plane.x = 0f
                    } else if (plane.x > screenWidth - plane.width) {
                        plane.x = (screenWidth - plane.width).toFloat()
                    }

                    if (plane.y < 0) {
                        plane.y = 0f
                    } else if (plane.y > screenHeight - plane.height) {
                        plane.y = (screenHeight - plane.height).toFloat()
                    }
                }
            }
            true
        }
    }

    private fun createBullet() {
        val createBullet = Observable.interval(200, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val bullet = ImageView(applicationContext)
                val bullet1 = ImageView(applicationContext)
                val bullet2 = ImageView(applicationContext)

                addViewBullet(bullet)
                addViewBullet(bullet1)
                addViewBullet(bullet2)

                Observable.interval(30, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        bullet.y = bullet.y - 5
                        bullet1.x = (bullet1.x - 5/ sqrt(3.0)).toFloat()
                        bullet1.y = bullet1.y - 5
                        bullet2.x = (bullet2.x + 5/ sqrt(3.0)).toFloat()
                        bullet2.y = bullet2.y - 5

                        countCollideBullet(bullet)
                        countCollideBullet(bullet1)
                        countCollideBullet(bullet2)
                    }
            }
        compositeDisposable.add(createBullet)
    }

    private fun countCollideBullet(bullet: ImageView) {
        for(i in 0 until listHeader.size){
            if(setCollideBulletWithHeader(listHeader[i], bullet, listCount[i]) == 0){
                val explosion = ImageView(applicationContext)
                val params = RelativeLayout.LayoutParams(200, 150)
                explosion.layoutParams = params
                layoutGame.addView(explosion)
                explosion.setBackgroundResource(R.drawable.explosion0)
                explosion.x = bullet.x - 90
                explosion.y = bullet.y - 90

                var k = 0
                Observable.interval(100, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        when (k) {
                            0 -> {
                                explosion.setBackgroundResource(R.drawable.explosion0)
                                k = 1
                            }
                            1 -> {
                                explosion.setBackgroundResource(R.drawable.explosion1)
                                k = 2
                            }
                            2 -> {
                                explosion.setBackgroundResource(R.drawable.explosion2)
                                k = 3
                            }
                            3 -> {
                                explosion.setBackgroundResource(R.drawable.explosion3)
                                k = 4
                            }
                            4 -> {
                                explosion.setBackgroundResource(R.drawable.explosion4)
                                k = 5
                            }
                            5 -> {
                                explosion.setBackgroundResource(R.drawable.explosion5)
                                k = 6
                            }
                            6 -> {
                                explosion.setBackgroundResource(R.drawable.explosion6)
                                k = 7
                            }
                            7 -> {
                                explosion.setBackgroundResource(R.drawable.explosion7)
                                k = 8
                            }
                            8 -> {
                                explosion.setBackgroundResource(R.drawable.explosion8)
                                k = 0
                                explosion.visibility = View.GONE
                            }
                        }
                    }

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

    private fun addViewBullet(bl: ImageView) {
        val params = RelativeLayout.LayoutParams(20, 45)
        bl.layoutParams = params
        layoutGame.addView(bl)
        bl.setBackgroundResource(R.drawable.rgrg)
        bl.x = plane.x + 45
        bl.y = plane.y - 30
    }

    private fun setCollideBulletWithHeader(header: ImageView, bullet: ImageView, count: Int) : Int{
        val distanceFromBulletToHeader = sqrt(
            abs((header.x + header.width / 2) - bullet.x).toDouble()
                .pow(2.0) + abs((header.y + header.height / 6) - bullet.y).toDouble()
                .pow(2.0)
            )

        if (-70 <= distanceFromBulletToHeader && distanceFromBulletToHeader <= 70 && header.y >= 0) {
            if (header.visibility == View.VISIBLE) {
                return if(count == 3){
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
            compositeDisposable.clear()
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

    private fun rotationAndTranslate(view: View){
        val rotate = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 1000
        rotate.repeatCount = Animation.INFINITE

        val translate = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f,
            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f)
        translate.duration = 1000
        translate.repeatCount = Animation.INFINITE

        val set = AnimationSet(true)
        set.addAnimation(rotate)
        set.addAnimation(translate)

        view.startAnimation(set)
    }

    private fun getSizeWindow() {
        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
        screenHeight = size.y
    }

    private fun setWindow() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}