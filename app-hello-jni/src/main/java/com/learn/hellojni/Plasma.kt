package com.learn.hellojni

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class Plasma : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dspl = display // windowManager.defaultDisplay
        val displaySize = Point()
        dspl?.getSize(displaySize)
        setContentView(PlasmaView(this, displaySize.x, displaySize.y))
    }
}

// Custom view for rendering plasma.
//
// Note: suppressing lint wrarning for ViewConstructor since it is
//       manually set from the activity and not used in any layout.
@SuppressLint("ViewConstructor")
class PlasmaView(context: Context, width: Int, height: Int) : View(context) {

    private external fun renderPlasma(bitmap: Bitmap, time_ms: Long)

    private var mBitmap: Bitmap

    private var mStartTime: Long = 0

    init {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        mStartTime = System.currentTimeMillis()
    }

    override fun onDraw(canvas: Canvas?) {
        renderPlasma(mBitmap, System.currentTimeMillis() - mStartTime)
        canvas?.drawBitmap(mBitmap, 0f, 0f, null)

        // force a redraw, with a different time-based pattern.
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
    }
}