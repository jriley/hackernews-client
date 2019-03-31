package dev.jriley.nyt.ui

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.jriley.nyt.R

fun Activity.enterLeftExitRight() = this.overridePendingTransition(R.anim.enter_left, R.anim.exit_right)

fun Activity.enterRightExitLeft() = this.overridePendingTransition(R.anim.enter_right, R.anim.exit_left)

fun Activity.finishAndExitWithAnimation() {
    this.finish()
    enterRightExitLeft()
}

fun ViewGroup.inflate(layoutId: Int) : View = LayoutInflater.from(this.context).inflate(layoutId, this, false)