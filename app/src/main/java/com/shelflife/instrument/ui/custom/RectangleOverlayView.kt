package com.shelflife.instrument.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import androidx.core.content.ContextCompat
import com.shelflife.instrument.R

class RectangleOverlayView(context: Context) : View(context) {
    private var rect: RectF? = null
    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.border_found_scan)
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rect?.let { canvas.drawRect(it, paint) }
    }

    fun updateRectangle(newRect: RectF?) {
        rect = newRect
        invalidate()  // перерисовка
    }
}
