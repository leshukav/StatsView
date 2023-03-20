package ru.netology.statsview.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(
    context,
    attr,
    defStyleAttr,
    defStyleRes,
) {
    private var textSize = AndroidUtils.dp(context, 20F).toFloat()
    private var lineWidth = AndroidUtils.dp(context, 20F)
    private var colors = emptyList<Int>()

    init {
        context.withStyledAttributes(attr, R.styleable.StatsView) {
            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth.toFloat()).toInt()
            colors = listOf(
                getColor(R.styleable.StatsView_color1, randomColor()),
                getColor(R.styleable.StatsView_color2, randomColor()),
                getColor(R.styleable.StatsView_color3, randomColor()),
                getColor(R.styleable.StatsView_color4, randomColor()),
            )
        }
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    private var oval = RectF()
    private var radius = 0F
    private var centr = PointF()

    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = this@StatsView.lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }
    private val textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = this@StatsView.textSize
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth
        centr = PointF(w / 2F, h / 2F)
        oval = RectF(
            centr.x - radius,
            centr.y - radius,
            centr.x + radius,
            centr.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }

        var startAngle = -90F
        var angle: Float = 0F

        when (data.size) {
            3 -> {
                val sum = data.sum() + (data.sum() / 3).toFloat()
                val parts = data.map { it / (sum / 100F) / 100F }
                paint.color = 0xFFCACACA.toInt()
                canvas.drawCircle(centr.x, centr.y, radius, paint)
                parts.forEachIndexed { index, datum ->
                    angle = datum * 360F
                    paint.color = colors.getOrElse(index) { randomColor() }
                    canvas.drawArc(
                        oval,
                        startAngle,
                        angle,
                        false,
                        paint
                    )
                    startAngle += angle
                }
                canvas.drawText(
                    "%.2f%%".format(
                        parts.sum() * 100
                    ),
                    centr.x,
                    centr.y + textPaint.textSize / 4,
                    textPaint,
                )
            }
            else -> {
                val sum = data.sum()
                val parts = data.map { it / (sum / 100F) / 100F }
                parts.forEachIndexed { index, datum ->
                    angle = datum * 360F
                    paint.color = colors.getOrElse(index) { randomColor() }
                    canvas.drawArc(
                        oval,
                        startAngle,
                        angle,
                        false,
                        paint
                    )
                    startAngle += angle
                }
                angle = parts.first() * 360F
                paint.color = colors.get(0)
                canvas.drawArc(
                    oval,
                    -90F,
                    angle / 5,
                    false,
                    paint
                )
                canvas.drawText(
                    "%.2f%%".format(
                        parts.sum() * 100
                    ),
                    centr.x,
                    centr.y + textPaint.textSize / 4,
                    textPaint,
                )
            }
        }
    }

    private fun randomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())

}