package com.jhomlala.better_player

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.util.LongSparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
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

        val viewGroup = LinearLayout(context)

        surfaceView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )

        viewGroup.addView(surfaceView)

        val viewController = ViewController(context, viewGroup);
        viewController.initial()

        return BetterPlatformView(viewController)
    }
}

internal class BetterPlatformView(private val view: ViewController) :
    PlatformView {
    override fun getView(): View {
        return view
    }

    override fun dispose() {
        view.dispose()
    }
}

internal class ViewController(context: Context?, private val viewGroup: ViewGroup) : View(context) {
    companion object {
        private const val TAG = "ViewController"
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        Log.d(TAG, "onSizeChanged $w $h");
        super.onSizeChanged(w, h, oldw, oldh)

        val layoutParams = FrameLayout.LayoutParams(w, h)

        viewGroup.layoutParams = layoutParams
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val par = this.parent as FrameLayout

//        Log.d(TAG, "parent x ${par.x}");
//        Log.d(TAG, "parent y ${par.y}");

        viewGroup.x = par.x
        viewGroup.y = par.y

    }

    fun initial() {
        val frameLayout = getFlutterFrameLayout()

        frameLayout.addView(viewGroup, 0)
    }

    fun dispose() {
        val frameLayout = getFlutterFrameLayout()

        viewGroup.removeAllViews()
        frameLayout.removeView(viewGroup)
    }

    private fun getFlutterFrameLayout(): FrameLayout {
        return ViewUtils.getActivity(context)!!
            .findViewById<FrameLayout>(FlutterActivity.FLUTTER_VIEW_ID)
    }
}