package com.example.android_view_test

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class HybridViewFactory : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<String?, Any?>?
        return HybridView(context, viewId, creationParams)
    }
}

internal class HybridView(context: Context, id: Int,
                          creationParams: Map<String?, Any?>?) : PlatformView {

    private val nativeView: View

    override fun getView(): View {
        return nativeView
    }

    override fun dispose() {}

    init {
        nativeView = LayoutInflater.from(context).inflate(R.layout.layout_native_view, null)
        setupViews(nativeView)
    }

    private fun setupViews(nativeView: View?) {

    }
}