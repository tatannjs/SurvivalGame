package fr.core.projet.utils

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.WindowManager

object DialogUtils {
    fun getDialogWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        return if (isTablet(context)) {
            (screenWidth * 0.6).toInt()
        } else {
            (screenWidth * 0.85).toInt()
        }
    }

    fun isTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE
    }
}