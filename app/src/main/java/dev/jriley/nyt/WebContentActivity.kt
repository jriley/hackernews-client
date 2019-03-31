package dev.jriley.nyt

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import androidx.appcompat.app.AppCompatActivity
import dev.jriley.nyt.ui.enterLeftExitRight
import kotlinx.android.synthetic.main.web_view.*
import timber.log.Timber

class WebContentActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_view)

        intent.getStringExtra(URL_TAG)?.let { url ->
            Timber.tag(URL_TAG).i( "onCreate: ${intent.getLongExtra(ID_TAG, -1)} $url")
            webView.webChromeClient = WebChromeClient()
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(url)

            openInBrowser.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) })
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        enterLeftExitRight()
    }

    companion object {
        const val URL_TAG = "URL_TAG"
        const val ID_TAG = "ID_TAG"
    }
}
