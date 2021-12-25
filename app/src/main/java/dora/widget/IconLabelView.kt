package dora.widget

import android.content.Context
import android.graphics.*
import android.os.Looper
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatRadioButton
import kotlin.math.ceil

class IconLabelView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatRadioButton(context, attrs, defStyleAttr) {

    private var textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private var iconPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var textRect = Rect()

    private var iconRect = Rect()

    private var iconLabelGap: Int

    private var iconScaleX = 1f
    private var iconScaleY = 1f

    private lateinit var cacheBitmap: Bitmap

    private var iconBitmap: Bitmap

    fun setIconBitmap(bitmap: Bitmap) {
        this.iconBitmap = bitmap
        invalidateView()
    }

    /**
     *
     */
    var ratio: Float = 0f
        set(value) {
            if (field != value) {
                field = value
            }
            invalidateView()
        }

    private var text: String = ""
        set(value) {
            if (field != value) {
                field = value
            }
            invalidateView()
        }

    private var labelTextSize: Float = 12f
        set(value) {
            if (field != value) {
                field = value
                textPaint.textSize = field
            }
            invalidateView()
        }

    private var labelTextColor: Int = Color.BLACK
        set(value) {
            if (field != value) {
                field = value
                textPaint.color = field
            }
            invalidateView()
        }

    private var hoverColor: Int = Color.BLACK
        set(value) {
            if (field != value) {
                field = value
            }
            invalidateView()
        }

    override fun setChecked(checked: Boolean) {
        super.setChecked(checked)
        ratio = (if (checked) 1f else 0f)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (iconBitmap != null) {
            var iconWidth = iconBitmap.width
            var iconHeight = iconBitmap.height
            val textBounds = Rect()
            textPaint.getTextBounds(text, 0, text.length, textBounds)
            val viewWidth = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
                iconWidth.coerceAtLeast(textBounds.width()) + paddingLeft + paddingRight
            } else {
                MeasureSpec.getSize(widthMeasureSpec)
            }
            val viewHeight = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
                iconHeight + iconLabelGap + textBounds.height() + paddingTop + paddingBottom
            } else {
                MeasureSpec.getSize(heightMeasureSpec)
            }
            val iconLeft = paddingLeft + (viewWidth - paddingLeft - paddingRight - iconWidth) / 2
            val iconRight = iconLeft + iconWidth
            val textLeft = paddingLeft + (viewWidth - paddingLeft - paddingRight - textBounds.width()) / 2
            val textRight = textLeft + textBounds.width()
            val totalHeight = iconHeight + iconLabelGap + textBounds.height()
            val iconTop = paddingTop + (viewHeight - paddingTop - paddingBottom - totalHeight) / 2
            val textTop = iconTop + iconHeight + iconLabelGap
            iconRect.set(iconLeft, iconTop, iconRight, iconTop + iconHeight)
            textRect.set(textLeft, textTop, textRight, textTop + textBounds.height())
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(
                    viewWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY))
        }
    }

    override fun onDraw(canvas: Canvas) {
        val alpha = ceil((255 * ratio).toDouble()).toInt()
        resetIcon(canvas)
        drawIcon(alpha)
        drawCacheText(canvas, alpha)
        drawHoverText(canvas, alpha)
    }

    private fun resetIcon(canvas: Canvas) {
        canvas.drawBitmap(iconBitmap, null, iconRect, null)
    }

    private fun drawIcon(alpha: Int) {
        cacheBitmap = Bitmap.createBitmap(
                measuredWidth,
                measuredHeight,
                Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(cacheBitmap)
        iconPaint.reset()
        iconPaint.color = hoverColor
        iconPaint.isDither = true
        iconPaint.alpha = alpha
        canvas.drawRect(iconRect, iconPaint)
        iconPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        iconPaint.alpha = 255
        canvas.drawBitmap(iconBitmap, null, iconRect, iconPaint)
    }

    private fun drawCacheText(canvas: Canvas, alpha: Int) {
        textPaint.color = labelTextColor
        textPaint.alpha = 255 - alpha
        val topY = textRect.top
        val baselineY: Float = topY - textPaint.fontMetrics.top
        for (index in 0 .. text.length) {
            val subText = text.substring(0, text.length - index)
            val textWidth = textPaint.measureText(subText)
            // 获取最后一个文字的宽度，用于排版
            val lastTextWidth = textPaint.measureText(subText[subText.length-1].toString())
            if (textWidth < measuredWidth - paddingLeft - paddingRight + lastTextWidth) {
                textRect.left = paddingLeft + ((measuredWidth - paddingLeft - paddingRight - textWidth) / 2).toInt()
                canvas.drawText(
                        subText, textRect.left.toFloat(), baselineY - textRect.height() / 2, textPaint
                )
                return
            }
        }
    }

    private fun drawHoverText(canvas: Canvas, alpha: Int) {
        textPaint.color = hoverColor
        textPaint.alpha = alpha
        val topY = textRect.top
        val baselineY: Float = topY - textPaint.fontMetrics.top
        for (index in 0 .. text.length) {
            val subText = text.substring(0, text.length - index)
            val textWidth = textPaint.measureText(subText)
            val lastTextWidth = textPaint.measureText(subText[subText.length-1].toString())
            // 获取最后一个文字的宽度，用于排版
            if (textWidth < measuredWidth - paddingLeft - paddingRight + lastTextWidth) {
                textRect.left = paddingLeft + ((measuredWidth - paddingLeft - paddingRight - textWidth) / 2).toInt()
                canvas.drawText(
                        subText, textRect.left.toFloat(), baselineY - textRect.height() / 2, textPaint
                )
                return
            }
        }
    }

    private fun invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate()
        } else {
            postInvalidate()
        }
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.IconLabelView, defStyleAttr, 0)
        iconScaleX = a.getFraction(R.styleable.IconLabelView_dora_iconScaleX, 1, 1, 1.0f)
        iconScaleY = a.getFraction(R.styleable.IconLabelView_dora_iconScaleY, 1, 1, 1.0f)
        val drawable = a.getDrawable(
                R.styleable.IconLabelView_dora_icon
        )
        if (iconScaleX == 0f || iconScaleY == 0f || drawable == null) {
            throw IllegalArgumentException("icon attribute error.")
        }
        val bmpWidth = drawable.intrinsicWidth * iconScaleX
        val bmpHeight = drawable.intrinsicHeight * iconScaleY
        iconBitmap = Bitmap.createBitmap(bmpWidth.toInt(), bmpHeight.toInt(),
                if (drawable.opacity
                != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
        val canvas = Canvas(iconBitmap)
        drawable.setBounds(0, 0, bmpWidth.toInt(), bmpHeight.toInt())
        drawable.draw(canvas)
        text = a.getString(R.styleable.IconLabelView_dora_text).toString()
        hoverColor = a.getColor(R.styleable.IconLabelView_dora_hoverColor, Color.BLACK)
        iconLabelGap = a.getDimensionPixelSize(R.styleable.IconLabelView_dora_iconLabelGap, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt())
        labelTextSize = a.getDimension(
                R.styleable.IconLabelView_dora_textSize,
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics)
        )
        labelTextColor = a.getColor(R.styleable.IconLabelView_dora_textColor, textColors.defaultColor)
        ratio = a.getFraction(R.styleable.IconLabelView_dora_ratio, 1, 1, 0f)
        a.recycle()
        textPaint.textSize = labelTextSize
        textPaint.color = labelTextColor
    }
}