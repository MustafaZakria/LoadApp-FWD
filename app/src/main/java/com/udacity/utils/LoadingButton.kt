package com.udacity.utils

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.udacity.R
import com.udacity.models.ButtonState
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new -> }


    init {
        isClickable = true
    }


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var degree = 1f

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
            it.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

            var btnText = ""
            if(buttonState == ButtonState.Loading) {
                btnText = context.getString(R.string.downloading)
                it.drawLoadingCircle(degree)
            }else {
                btnText = context.getString(R.string.download)
            }

            paint.color = Color.WHITE
            val textHeight = (paint.ascent() + paint.descent()) / 2f
            it.drawText(btnText, widthSize / 2f, heightSize / 2f - textHeight, paint)


        }

    }

    private fun Canvas.drawLoadingCircle(sweepAngle: Float) {
        paint.color = ContextCompat.getColor(context, R.color.colorAccent)

        val circleWidth = width / 10f
        val circleHeight = height / 2f
        drawArc(
            width / 1.3f,
            (height / 2f) - circleHeight/2f,
            (width / 1.3f) + circleWidth,
            (height / 2f) + circleHeight/2f,
            0f,
            sweepAngle,
            true,
            paint
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun performClick(): Boolean {
//        if () return true
        buttonState = ButtonState.Loading

        startTimer()

        invalidate()
        return super.performClick()
    }

    private fun startTimer() {
        val countdownTimer = object : CountDownTimer(15000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                degree += 24
                invalidate()
            }

            override fun onFinish() {
                degree = 1f
                buttonState = ButtonState.Completed
                invalidate()
            }
        }.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}