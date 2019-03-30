package dev.jriley.nyt.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import dev.jriley.nyt.BottomNavActivity
import dev.jriley.nyt.R
import dev.jriley.nyt.ui.finishAndExitWithAnimation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_splash.*
import timber.log.Timber

class SplashActivity : AppCompatActivity() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ViewModelProviders.of(this).get(SplashViewModel::class.java).apply {
            compositeDisposable.add(loadingObservable
                    .doFinally { compositeDisposable.clear() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ startMain() }, { Timber.e("Something has gone seriously wrong") }))
        }
    }

    override fun onResume() {
        super.onResume()
        content.startAnimation(AlphaAnimation(0.0F, 1.0F).apply { duration = 1000 })
    }

    private fun startMain() {
        startActivity(Intent(this, BottomNavActivity::class.java))
        finishAndExitWithAnimation()
    }
}