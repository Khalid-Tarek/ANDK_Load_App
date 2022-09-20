package com.udacity

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

private const val TAG = "LoadingButton"

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var roundFactor = 50f

    private var circleEnabled by Delegates.notNull<Boolean>()
    private var barEnabled by Delegates.notNull<Boolean>()
    private var myBackgroundColor by Delegates.notNull<Int>()
    private var loadingCircleColor by Delegates.notNull<Int>()
    private lateinit var downloadString: String
    private lateinit var loadingString: String

    private val rect by lazy {
        RectF(
            width - (2f * height / 4f),
            height / 4f,
            width + 0f,
            3f * height / 4f
        )
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
    }

    private var progress = 0f
    private val valueAnimator = ValueAnimator()

    var buttonState: ButtonState by
    Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        if (new == ButtonState.Loading) {
            startAnimation()
        }
        invalidate()
    }

    private fun startAnimation() {
        valueAnimator.apply {
            setFloatValues(0f, 1f)
            duration = 3000
            interpolator = LinearInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    buttonState = ButtonState.Completed
                }

                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationRepeat(animation: Animator?) {}
            })
            start()
        }
    }

    init {

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            circleEnabled = getBoolean(R.styleable.LoadingButton_enableLoadingCircle, true)
            barEnabled = getBoolean(R.styleable.LoadingButton_enableLoadingBar, true)
            myBackgroundColor = getColor(R.styleable.LoadingButton_myBackground, Color.CYAN)
            loadingCircleColor = getColor(R.styleable.LoadingButton_loadingCircleColor, Color.YELLOW)
            downloadString = getString(R.styleable.LoadingButton_downloadString) ?: "Download"
            loadingString = getString(R.styleable.LoadingButton_loadingString) ?: "We are Loading"

        }

        isClickable = true
        contentDescription = context.getString(R.string.stringCustomButtonDesc)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawBackground(canvas)
        when (buttonState) {
            ButtonState.Completed -> write(canvas, downloadString)
            ButtonState.Loading -> {
                if(barEnabled) drawProgressBar(canvas)
                if(circleEnabled) drawProgressCircle(canvas)
                write(canvas, loadingString)
            }
            ButtonState.Clicked -> {}
        }
    }

    private fun drawBackground(canvas: Canvas?) {
        paint.color = myBackgroundColor
        canvas?.drawRoundRect(
            0f, 0f,
            width.toFloat(), height.toFloat(), roundFactor, roundFactor, paint
        )
    }

    private fun drawProgressBar(canvas: Canvas?) {
        paint.color = Color.argb(50, 0, 0, 0)
        canvas?.drawRoundRect(
            (width / 2f) - (width / 2f * progress), 0f,
            (width / 2f) + (width / 2f * progress), height.toFloat(), roundFactor, roundFactor, paint
        )
    }

    private fun drawProgressCircle(canvas: Canvas?) {
        paint.color = loadingCircleColor
        canvas?.drawArc(
            rect,
            0f, progress * 360, true, paint
        )
    }

    private fun write(canvas: Canvas?, string: String) {
        paint.color = Color.BLACK
        canvas?.drawText(string, width / 2f, (height / 2f) + 15f, paint)
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