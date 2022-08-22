package com.jhomlala.better_player

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import android.util.LongSparseArray
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import io.flutter.util.ViewUtils

internal class BetterViewFactory(private val videoPlayers: LongSparseArray<BetterPlayer>) :
    PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<String?, Any?>?
        val textureId = (creationParams!!["textureId"] as Int).toLong()
        val player = videoPlayers[textureId]

        val surfaceView = player.getSurfaceView

        if (surfaceView.parent != null) {
            (surfaceView.parent as ViewGroup).removeView(surfaceView)
        }

        val frameLayout: FrameLayout =
            ViewUtils.getActivity(context)!!
                .findViewById<FrameLayout>(FlutterActivity.FLUTTER_VIEW_ID)

        frameLayout.addView(surfaceView, 0)

        val viewController = ViewController(context, surfaceView);

        return BetterPlatformView(viewController)
    }
}

internal class BetterPlatformView(private val view: ViewController) :
    PlatformView {
    override fun getView(): View {
        return view
    }

    override fun dispose() {
    }
}

internal class ViewController(context: Context?, private val view: SurfaceView) : View(context) {
    companion object {
        private const val TAG = "ViewController"
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        Log.d(TAG, "onSizeChanged $w $h");
        val layoutParams = FrameLayout.LayoutParams(w, h)

        view.layoutParams = layoutParams

        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        val par = this.parent as FrameLayout

//        Log.d(TAG, "parent x ${par.x}");
//        Log.d(TAG, "parent y ${par.y}");

        view.x = par.x
        view.y = par.y

        super.onDraw(canvas)
    }
}