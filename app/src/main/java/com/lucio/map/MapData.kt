package com.lucio.map

import android.graphics.*
import androidx.annotation.ColorInt

class MapData(var name: String, private var path: Path, @param:ColorInt private var fillColor: Int, @param:ColorInt private val strokeColor: Int, private val strokeWidth: Float) {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var isSelected = false

    fun onDraw(canvas: Canvas) {
        //背景
        if (isSelected) {
            paint.color = Color.RED
        } else {
            paint.color = fillColor
        }
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 1f
        canvas.drawPath(path, paint)

        //边线
        paint.strokeWidth = strokeWidth
        paint.color = strokeColor
        paint.style = Paint.Style.STROKE
        canvas.drawPath(path, paint)

        //文字
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 0.1f
        paint.color = Color.BLACK
        paint.textSize = 15f

        val rectF = RectF()
        path.computeBounds(rectF, true)
        canvas.drawText((if (name.isEmpty()) "" else name), rectF.centerX() - paint.measureText(name) / 2, rectF.centerY(), paint)
    }

    fun isContainXY(x: Float, y: Float): Boolean {
        val rectF = RectF()
        path.computeBounds(rectF, true)
        val region = Region()
        region.setPath(path, Region(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt()))
        return region.contains(x.toInt(), y.toInt())
    }
}