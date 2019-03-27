package dev.jriley.nyt

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.test.rule.ActivityTestRule

fun <T : Activity?> ActivityTestRule<T>.rotateLandscape()  {
    this.activity?.requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

fun <T : Activity?> ActivityTestRule<T>.rotatePortrait()  {
    this.activity?.requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}