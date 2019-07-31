package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
import android.graphics.Bitmap
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import kotlin.math.min
import android.graphics.BitmapShader








class CircleImageView @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ImageView(context, attrs, defStyleAttr) {
    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()
    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    companion object {
        private const val BORDER_COLOR_DEFAULT: Int = Color.WHITE
    }

    private var borderColor = Color.WHITE
    private var borderWidth = 2.px


    private lateinit var mBitmapShader: Shader
    private var mShaderMatrix: Matrix

    private var mBitmapDrawBounds: RectF
    private var mStrokeBounds: RectF

    private var mBitmap: Bitmap? = null

    private var mBitmapPaint: Paint
    private var mStrokePaint: Paint

    private var mInitialized: Boolean = false


    init {
        if (attrs != null) {
            val attrVal = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = attrVal.getColor(R.styleable.CircleImageView_cv_borderColor, BORDER_COLOR_DEFAULT)
            borderWidth = attrVal.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, borderWidth)
            attrVal.recycle()
        }
        mShaderMatrix = Matrix()
        mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mStrokeBounds = RectF()
        mBitmapDrawBounds = RectF()
        mStrokePaint.color = borderColor
        mStrokePaint.style = Paint.Style.STROKE
        mStrokePaint.strokeWidth = borderWidth.toFloat()

        mInitialized = true

        setupBitmap()

    }

    @Dimension
    fun getBorderWidth(): Int = borderWidth.dp

    fun setBorderWidth(@Dimension dp: Int) {
        borderWidth = dp.px
        this.invalidate()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        this.invalidate()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = ContextCompat.getColor(App.applicationContext(), colorId)
        this.invalidate()
    }

    private fun setupBitmap() {
        if (!mInitialized) {
            return
        }
        mBitmap = getBitmapFromDrawable(drawable)
        if (mBitmap == null) {
            return
        }

        mBitmapShader = BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint.shader = mBitmapShader

        updateBitmapSize()
    }

    private fun updateBitmapSize() {
        if (mBitmap == null) return

        // trainslate bitmap in the BitmapShader using dx and dy so that it's centered
        val dx: Float
        val dy: Float

        // scale factor
        val scale: Float

        // scale up/down with respect to this view size and maintain aspect ratio
        // translate bitmap position with dx/dy to the center of the image
        //
        // check do we want to scale based on width or height
        if (mBitmap!!.width < mBitmap!!.height) {
            // if bitmp with is less than its height, we wanna scale based on its width
            // assign scale factor based on difference (ratio) between bitmap width and bitmap draw bounds
            scale = mBitmapDrawBounds.width() / mBitmap!!.width as Float
            // because we know that the scale was based on width, the width would fit
            // exaclty with bounds, so we just translate x with its left padding
            dx = mBitmapDrawBounds.left
            // we want to center y(height) axis of the scaled bitmap,
            // the logial way to see this is:
            // at the first state bitmap would rendered at the top left area
            // by translating with top padding of the view,
            // translate up by half of bitmap height (so center of bitmap now in the top of the view),
            // translate down by half of the bitmap bounds (so center of bitmap would be in the center of the view (bitmap bounds))
            dy = mBitmapDrawBounds.top - mBitmap!!.height * scale / 2f + mBitmapDrawBounds.width() / 2f
        } else {
            // the same concept goes the same here, the difference is we
            // translate (center) horizontal axis instead of vertical/y axis
            scale = mBitmapDrawBounds.height() / mBitmap!!.height.toFloat()
            dx = mBitmapDrawBounds.left - mBitmap!!.width * scale / 2f + mBitmapDrawBounds.width() / 2f
            dy = mBitmapDrawBounds.top
        }

        // apply this transformation into shader matrix -> bitmap shader
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(dx, dy)
        mBitmapShader.setLocalMatrix(mShaderMatrix)
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateCircleDrawBounds(mBitmapDrawBounds)
    }

    private fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth = (width - paddingLeft - paddingRight).toFloat()
        val contentHeight = (height - paddingTop - paddingBottom).toFloat()

        var left = paddingLeft.toFloat()
        var top = paddingTop.toFloat()

        // we'll center bounds by translating left/top
        // so that the rendered circle always in the center of view
        if (contentWidth > contentHeight) {
            left += (contentWidth - contentHeight) / 2f
        } else {
            top += (contentHeight - contentWidth) / 2f
        }

        // we want to make this bounds always square (aspect ratio of 1:1)
        val diameter = min(contentWidth, contentHeight)
        bounds.set(left, top, left + diameter, top + diameter)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBitmap(canvas)
        drawStroke(canvas)
    }

    private fun drawStroke(canvas: Canvas) {
        if (mStrokePaint.strokeWidth > 0f) {
            canvas.drawOval(mStrokeBounds, mStrokePaint)
        }
    }

    private fun drawBitmap(canvas: Canvas) {
        // we draw an oval shape using draw bounds that we have set to always square and it would draw a circle in it
        // also the bitmap paint is set with the bitmap shader so the color
        // of the shape is the bitmap itself
        canvas.drawOval(mBitmapDrawBounds, mBitmapPaint)
    }
}
