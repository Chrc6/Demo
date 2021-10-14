package com.chrc.kotlindemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.chrc.demo.R
import com.google.android.material.textview.MaterialTextView

/**
 *    @author : chrc
 *    date   : 2021/8/10  4:27 PM
 *    desc   :
 */
class CustomLineHeightTextView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0)
    : MaterialTextView(context, attributeSet, defStyleAttr) {

    private var customLineHeight: Int = 0

    init {
        initParams(attributeSet)
    }

    private fun initParams(attributeSet: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.LineHeightTextView, 0, 0)
        customLineHeight = typedArray.getDimensionPixelOffset(R.styleable.LineHeightTextView_customize_line_height, 0)

//        setCustomLineHeight()
    }

    fun setCustomLineHeight() {
        if (customLineHeight > 0) {
            var fontHeight = paint.getFontMetricsInt(null)
            var fontMetrics = paint.fontMetrics//fontMetrics.descent - fontMetrics.ascent
            if (customLineHeight > fontHeight) {
                var params = layoutParams
                if (params == null) {
                    params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                }
//                var topPadding = (customLineHeight - fontHeight) / 2 - (fontMetrics.ascent - fontMetrics.top).toInt()
//                var bottomPadding = (customLineHeight - fontHeight) / 2 - (fontMetrics.bottom - fontMetrics.descent).toInt()
                var topPadding = 0
                var bottomPadding = 0
                setPadding(paddingLeft, topPadding, paddingRight, bottomPadding)
                var toInt = ((fontMetrics.bottom - fontMetrics.descent) + (fontMetrics.ascent - fontMetrics.top)).toInt()
                lineHeight = customLineHeight - toInt
                Log.i("testview===", " lineHeight=$customLineHeight fontHeight=$fontHeight " +
                        "topPadding=$topPadding  bottomPadding=$bottomPadding" +
                        "  textHeight=${fontMetrics.bottom - fontMetrics.top} top=${fontMetrics.top} bottom=${fontMetrics.bottom} " +
                        " descent=${fontMetrics.descent} ascent=${fontMetrics.ascent}" +
                        " dd-ac=${fontMetrics.descent - fontMetrics.ascent}" +
                        " paint.descent=${paint.descent()}")
                layoutParams = params
            }
        }
    }

    fun setCustomLineHeight2(fontHeight: Int, topCoordinate: Int, bottomCoordinate: Int) {
        if (customLineHeight > 0) {
//            var fontHeight = paint.getFontMetricsInt(null)
            var fontMetrics = paint.fontMetrics//fontMetrics.descent - fontMetrics.ascent

            if (customLineHeight > fontHeight) {
                var topPadding = (customLineHeight - fontHeight) / 2 - (topCoordinate - fontMetrics.top).toInt()
                var bottomPadding = (customLineHeight - fontHeight) / 2 - (fontMetrics.bottom - bottomCoordinate).toInt()
//                var topPadding = 0
//                var bottomPadding = 0
                if (topPadding == paddingTop && bottomPadding == paddingBottom) {
                    Log.e("fontMetrics::::", " setCustomLineHeight2 return")
                    return
                }
                Log.e("fontMetrics::::", " setCustomLineHeight2 continue")
                setPadding(paddingLeft, topPadding, paddingRight, bottomPadding)
                var toInt = ((fontMetrics.bottom - fontMetrics.descent) + (fontMetrics.ascent - fontMetrics.top)).toInt()
                lineHeight = customLineHeight - toInt
                Log.i("testview===", " lineHeight=$customLineHeight fontHeight=$fontHeight " +
                        "topPadding=$topPadding  bottomPadding=$bottomPadding" +
                        "  textHeight=${fontMetrics.bottom - fontMetrics.top} top=${fontMetrics.top} bottom=${fontMetrics.bottom} " +
                        " descent=${fontMetrics.descent} ascent=${fontMetrics.ascent}" +
                        " dd-ac=${fontMetrics.descent - fontMetrics.ascent}" +
                        " paint.descent=${paint.descent()}")
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val measuredWidth = measuredWidth
        val paint = paint
        val fontMetrics = paint.fontMetrics
        val top = if (includeFontPadding) fontMetrics.top else fontMetrics.ascent
//        paint.strokeWidth = 6f

//        paint.color = Color.BLACK
//        canvas?.drawLine(0f, 0f, measuredWidth.toFloat(), 0f, paint)
//
//        paint.color = Color.RED
//        canvas?.drawLine(0f, fontMetrics.ascent - fontMetrics.top, measuredWidth.toFloat() - 300, fontMetrics.ascent - fontMetrics.top, paint)
//        paint.color = Color.YELLOW
//        canvas?.drawLine(0f, -fontMetrics.top, measuredWidth.toFloat(), -fontMetrics.top, paint)
//
//        paint.color = Color.GREEN
//        canvas?.drawLine(0f, fontMetrics.descent - fontMetrics.top, measuredWidth.toFloat(), fontMetrics.descent - fontMetrics.top, paint)
//
//        paint.color = Color.BLUE
//        canvas?.drawLine(100f, fontMetrics.bottom - fontMetrics.top, measuredWidth.toFloat() - 300, fontMetrics.bottom - fontMetrics.top, paint)
//
//        Log.e("fontMetrics::::", "top:${fontMetrics.top} ; ascent:${fontMetrics.ascent} ; leading:${fontMetrics.leading} ; descent:${fontMetrics.descent} ; bottom:${fontMetrics.bottom}" +
//                " ; paint.baselineShift=${paint.baselineShift}")
//        Log.e("width: || height: ", "$width  ||  $height")
        val rect = Rect()
//        var str = "MaterialTextView测试"
        if (!TextUtils.isEmpty(text)) {
            paint.getTextBounds(text.toString(), 0, text.length, rect)
            Log.e("fontMetrics::::", " width=${rect.width()} height=${rect.height()} rect.top=${rect.top} rect.bottom=${rect.bottom}")
            setCustomLineHeight2(rect.height(), rect.top, rect.bottom)
        }
    }
}