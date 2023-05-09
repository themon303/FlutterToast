package io.github.ponnamkarthik.toast.fluttertoast

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import kotlin.Exception

internal class MethodCallHandlerImpl(var context: Context) : MethodCallHandler {

    private var mToast: Toast? = null

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "showToast" -> {
                val mMessage = call.argument<Any>("msg").toString()
                val length = call.argument<Any>("length").toString()
                val gravity = call.argument<Any>("gravity").toString()
                val textcolor = call.argument<Number>("textcolor")
                val textSize = call.argument<Number>("fontSize")

                val mGravity: Int = when (gravity) {
                    "top" -> Gravity.TOP
                    "center" -> Gravity.CENTER
                    else -> Gravity.BOTTOM
                }

                val mDuration: Int = if (length == "long") {
                    Toast.LENGTH_LONG
                } else {
                    Toast.LENGTH_SHORT
                }

                mToast = Toast.makeText(context, mMessage, mDuration)
                if (Build.VERSION.SDK_INT <= 31) {
                    try {
                        val textView: TextView = mToast?.view!!.findViewById(android.R.id.message)
                        if (textSize != null) {
                            textView.textSize = textSize.toFloat()
                        }
                        if (textcolor != null) {
                            textView.setTextColor(textcolor.toInt())
                        }
                    } catch (e: Exception) {

                    }
                }

                if(Build.VERSION.SDK_INT <= 31) {
                    when (mGravity) {
                        Gravity.CENTER -> {
                            mToast?.setGravity(mGravity, 0, 0)
                        }
                        Gravity.TOP -> {
                            mToast?.setGravity(mGravity, 0, 100)
                        }
                        else -> {
                            mToast?.setGravity(mGravity, 0, 100)
                        }
                    }
                }
                
                if (context is Activity) {
                    (context as Activity).runOnUiThread { mToast?.show() }
                } else {
                    mToast?.show()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    mToast?.addCallback(object : Toast.Callback() {
                        override fun onToastHidden() {
                            super.onToastHidden()
                            mToast = null
                        }
                    })
                }
                result.success(true)
            }
            "cancel" -> {
                if (mToast != null) {
                    mToast?.cancel()
                    mToast = null
                }
                result.success(true)
            }
            else -> result.notImplemented()
        }
    }
}
