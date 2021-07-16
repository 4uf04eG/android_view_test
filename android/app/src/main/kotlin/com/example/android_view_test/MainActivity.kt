package com.example.android_view_test

import android.content.Intent
import android.os.Bundle
import com.example.android_view_test.scheduleapp.activities.AllGroupsActivity
import com.example.android_view_test.scheduleapp.activities.MainNativeActivity
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant


class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.startActivity/testChannel"

    private lateinit var methodChannel: MethodChannel

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        methodChannel = MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        );

        methodChannel.setMethodCallHandler { call, result ->
            if (call.method == "StartSecondActivity") {
                val intent = Intent(this, MainNativeActivity::class.java)
                startActivity(intent)
                result.success("ActivityStarted")
            } else {
                result.notImplemented()
            }
        }

        flutterEngine
            .platformViewsController
            .registry
            .registerViewFactory("native-view", NativeViewFactory())

    }
}
