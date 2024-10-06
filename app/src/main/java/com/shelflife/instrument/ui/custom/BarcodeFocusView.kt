package com.shelflife.instrument.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class BarcodeFocusView(context: Context, attrs: AttributeSet) : View(context, attrs){

    //private var rect = RectF(0f,299f, ScreenUtils.getWidthScreen(context).toFloat(),900f)
    private var rect = RectF(0f,0f,0f,0f)

    val radiusRect = 0f

    private var colorBorder = Color.TRANSPARENT

    fun setRedBorder(){
        this.colorBorder = Color.RED
        repaint()
    }

    fun setHolePosition(left: Float, top: Float, right: Float, bottom: Float){
        rect = RectF(left, top, right, bottom)
        repaint()
    }

    fun setWhiteBorder(){
        this.colorBorder = Color.WHITE
        repaint()
    }
    fun setGreenBorder(){
        this.colorBorder = Color.GREEN
        repaint()
    }

    fun repaint(){
        this.post {
            invalidate()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val mPath = Path()
        mPath.fillType = Path.FillType.WINDING
        mPath.addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
        mPath.addRoundRect(rect, radiusRect, radiusRect, Path.Direction.CCW)

        val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.parseColor("#C2000000")
        canvas.drawPath(mPath, mPaint)

        //Накладываем рамку на дыру
        canvas.drawRoundRect(rect, radiusRect, radiusRect, Paint().apply {
            color = this@BarcodeFocusView.colorBorder
            strokeWidth = 4f
            style = Paint.Style.STROKE
        })
    }
}