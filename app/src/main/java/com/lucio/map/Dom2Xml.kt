package com.lucio.map

import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.text.TextUtils
import android.util.Xml
import android.view.View
import org.xmlpull.v1.XmlPullParser
import java.io.IOException
import java.io.InputStream
import java.util.*

object Dom2XmlUtils {

    //svg图的实际宽高
    var svgWidth = 0f
    var svgHeight = 0f

    fun dom2xml(context: Context, id: Int): List<MapData> {
        val data: MutableList<MapData> = mutableListOf()
        val rectF = RectF()
        var maxRight = 0f
        var maxBottom = 0f
        var left = 0f
        var top = 0f
        var inputStream: InputStream? = null
        try {
            inputStream = context.resources.openRawResource(id)
            val xmlPullParser = Xml.newPullParser()
            xmlPullParser.setInput(inputStream, "utf-8")
            var eventType = xmlPullParser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val name = xmlPullParser.name
                if (!TextUtils.isEmpty(name)) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if ("path" == name) {
                            val pathData = xmlPullParser.getAttributeValue(null, "pathData")
                            val path = PathParser.createPathFromPathData(pathData)
                            val cityName = xmlPullParser.getAttributeValue(null, "name")
                            val fillColor = Color.parseColor(xmlPullParser.getAttributeValue(null, "fillColor"))
                            val strokeColor = Color.parseColor(xmlPullParser.getAttributeValue(null, "strokeColor"))
                            val strokeWidth = xmlPullParser.getAttributeValue(null, "strokeWidth").toFloat()
                            val city = MapData(cityName, path, fillColor, strokeColor, strokeWidth)
                            path.computeBounds(rectF, true)

                            maxRight = maxRight.coerceAtLeast(rectF.right)
                            maxBottom = maxBottom.coerceAtLeast(rectF.bottom)
                            left = left.coerceAtMost(rectF.left)
                            top = top.coerceAtMost(rectF.top)
                            data.add(city)
                        }
                    }
                }
                eventType = xmlPullParser.next()
            }
            svgWidth = maxRight - left
            svgHeight = maxBottom - top
            return data
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return ArrayList()
    }
}