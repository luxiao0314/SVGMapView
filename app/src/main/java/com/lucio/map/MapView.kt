package com.lucio.map

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.lucio.map.Dom2XmlUtils.dom2xml
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class MapView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var scale = 0.5f
    private var minWidth = 200f
    private var minHeight = 200f

    //svg图的实际宽高
    private var svgWidth = 0f
    private var svgHeight = 0f
    private var mapDatas: List<MapData> = mutableListOf()

    private fun initMapData() = GlobalScope.launch {
        flow { emit(dom2xml(context, R.raw.neimeng)) }
                .onStart { }
                .flowOn(Dispatchers.IO)
                .onCompletion { }
                .catch {}
                .collect {
                    withContext(Dispatchers.Main) {
                        mapDatas = it
                        svgWidth = Dom2XmlUtils.svgWidth
                        svgHeight = Dom2XmlUtils.svgHeight
                        measure(measuredWidth, measuredHeight)
                        postInvalidate()
                    }
                }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        when (widthMode) {
            MeasureSpec.EXACTLY -> {
                Log.d("MapView", "widthMode : MeasureSpec.EXACTLY")
                width = width.coerceAtLeast(minWidth.toInt())
            }
            MeasureSpec.AT_MOST -> {
                Log.d("MapView", "widthMode : MeasureSpec.AT_MOST")
                width = minWidth.toInt()
            }
            MeasureSpec.UNSPECIFIED -> {
            }
            else -> {
            }
        }
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                Log.d("MapView", "heightMode : MeasureSpec.EXACTLY")
                height.coerceAtLeast(minHeight.toInt())
            }
            MeasureSpec.AT_MOST -> {
                Log.d("MapView", "heightMode : MeasureSpec.AT_MOST")
                minHeight.toInt()
            }
            else -> minHeight.toInt()
        }


//        计算需要缩放的比例
        scale = width / svgWidth
        scale = (height / svgHeight).coerceAtMost(scale)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        for (city in mapDatas) {
            canvas.save()
            canvas.scale(scale, scale)
            city.onDraw(canvas)
            canvas.restore()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> handlerTouch(event)
        }
        return super.onTouchEvent(event)
    }

    private fun handlerTouch(event: MotionEvent) {

        for (city in mapDatas) {
            var x = event.x
            var y = event.y
            x /= scale
            y /= scale
            val isContain = city.isContainXY(x, y)
            city.isSelected = isContain

            if (isContain) {
                selectProvince?.invoke(city.name)
            }
        }

        invalidate()

        mapDatas.none { it.isSelected }.let {
            if (it) selectBlank?.invoke()
        }
    }

    companion object {
        //将dp转换为像素单位
        fun dp2px(context: Context, dpValue: Float): Float {
            val scale = context.resources.displayMetrics.density
            return dpValue * scale + 0.5f
        }
    }

    init {
        minWidth = dp2px(getContext(), 400f)
        minHeight = dp2px(getContext(), 500f)
        initMapData()
    }

    private var selectProvince: ((String) -> Unit?)? = null
    private var selectBlank: (() -> Unit?)? = null
    fun setOnProvinceClickLisener(selectProvince: (data: String) -> Unit, selectBlank: () -> Unit) {
        this.selectProvince = selectProvince
        this.selectBlank = selectBlank
    }
}