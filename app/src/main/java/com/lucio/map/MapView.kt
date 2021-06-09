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
import kotlin.math.atan2
import kotlin.math.sqrt

class MapView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var scale = 0.5f
    private var minWidth = dp2px(getContext(), 400f)
    private var minHeight = dp2px(getContext(), 320f)

    //svg图的实际宽高
    private var svgWidth = 0f
    private var svgHeight = 0f
    private var mapDatas: List<MapData> = mutableListOf()

    companion object {
        //将dp转换为像素单位
        fun dp2px(context: Context, dpValue: Float): Float {
            val scale = context.resources.displayMetrics.density
            return dpValue * scale + 0.5f
        }
    }

    init {
        setData()
    }

    fun setData(id: Int = R.raw.chinahigh) = GlobalScope.launch {
        flow { emit(dom2xml(context, id)) }
                .flowOn(Dispatchers.IO)
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


        //计算需要缩放的比例
        scale = width / svgWidth
        scale = (height / svgHeight).coerceAtMost(scale)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        for (city in mapDatas) {
//            city.path.reset()
            canvas.save()
            canvas.scale(scale, scale)
            city.onDraw(canvas)
            canvas.restore()
        }
    }

    /********************************  滑动,缩放,移动,点击处理  **********************************/

    // 属性变量
    private var transX = 0f
    private var transY = 0f
    private var scaled = 1f // 伸缩比例

    // 移动过程中临时变量
    private var actionX = 0f
    private var actionY = 0f
    private var spacing = 0f
    private var degree = 0f
    private var currentMS = 0L
    private var moveType = 0    // 0=未选择，1=拖动，2=缩放

    //滑动,缩放,移动,点击处理
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                moveType = 1
                actionX = event.rawX
                actionY = event.rawY
                currentMS = System.currentTimeMillis()//long currentMS     获取系统时间
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                moveType = 2
                spacing = getSpacing(event)
                degree = getDegree(event)
            }
            MotionEvent.ACTION_MOVE -> if (moveType == 1) {
                transX = transX + event.rawX - actionX
                transY = transY + event.rawY - actionY
                translationX = transX
                translationY = transY
                actionX = event.rawX
                actionY = event.rawY
            } else if (moveType == 2) {
                scaled = scaled * getSpacing(event) / spacing
                scaleX = scaled
                scaleY = scaled
            }
            MotionEvent.ACTION_UP -> {
                moveType = 0
                //判断是否继续传递信号,移动时间
                if (System.currentTimeMillis() - currentMS > 200) {
                    return true
                } else {
                    handlerTouch(event)
                }
            }
            MotionEvent.ACTION_POINTER_UP -> moveType = 0
        }
        return true
    }

    // 触碰两点间距离
    private fun getSpacing(event: MotionEvent): Float {
        //通过三角函数得到两点间的距离
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }

    // 取旋转角度
    private fun getDegree(event: MotionEvent): Float {
        //得到两个手指间的旋转角度
        val deltaX = (event.getX(0) - event.getX(1)).toDouble()
        val deltaY = (event.getY(0) - event.getY(1)).toDouble()
        val radians = atan2(deltaY, deltaX)
        return Math.toDegrees(radians).toFloat()
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

    private var selectProvince: ((String) -> Unit?)? = null
    private var selectBlank: (() -> Unit?)? = null
    fun setOnProvinceClickLisener(selectProvince: (data: String) -> Unit, selectBlank: () -> Unit) {
        this.selectProvince = selectProvince
        this.selectBlank = selectBlank
    }
}