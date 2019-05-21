package com.example.artur.drawingapp

import android.content.Context
import android.view.View
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.graphics.Bitmap
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


val pathsToPaints: MutableList<Pair<Path, Paint>> = mutableListOf()
var path = Path()

class Canvas(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var paint = Paint()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        invalidate()

        for (p: Pair<Path, Paint> in pathsToPaints) {
            canvas?.drawPath(p.first, p.second)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event != null) {
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    path = Path()
                    paint = Paint()
                    preparePaint()

                    if (rubberActive) {
                        paint.color = Color.WHITE
                        paint.strokeWidth = 100f
                    }

                    if (boldActive) {
                        paint.strokeWidth = 50f
                    }

                    path.moveTo(event.x, event.y)
                    pathsToPaints.add(Pair(path, paint))
                }

                MotionEvent.ACTION_MOVE -> {
                    path.lineTo(event.x, event.y)
                }

                MotionEvent.ACTION_UP -> {
                }
            }
        }
        invalidate()
        return true
    }

    fun preparePaint() {
        paint.color = color
        paint.isAntiAlias = true
        paint.strokeJoin = Paint.Join.ROUND
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
    }

    fun saveCanvas() {
        val DIRECTORY = Environment.getExternalStorageDirectory().path + "/PaintImages/"
        val pictureName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val StoredPath = "$DIRECTORY$pictureName.png"

        val file = File(DIRECTORY)
        if(!file.exists())
            file.mkdir()

        val bitmap = Bitmap.createBitmap(1000,1450, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.WHITE)
        val canvas = Canvas(bitmap)

        val mFileOutputStream = FileOutputStream(StoredPath)
        this.draw(canvas)

        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, mFileOutputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mFileOutputStream.flush()
        mFileOutputStream.close()
    }
}

fun clearCanvas() {
    for (p: Pair<Path, Paint> in pathsToPaints) {
        p.first.reset()
    }
}