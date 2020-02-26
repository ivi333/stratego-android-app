package de.arvato.stratego

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import java.util.*

class HtmlActivity : Activity() {
    private var _webview: WebView? = null
    private var _lang: String? = null
    private var _TVversionName: TextView? = null
    /** Called when the activity is first created.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.help)
        //this.makeActionOverflowMenuShown()
        // html assets localization
        _lang = if (Locale.getDefault().language == "es") {
            "es"
        } else if (Locale.getDefault().language == "it") {
            "it"
        } else if (Locale.getDefault().language == "pt") {
            "pt"
        } else if (Locale.getDefault().language == "ru") {
            "ru"
        } else if (Locale.getDefault().language == "zh") {
            "zh"
        } else {
            "en"
        }
        _webview = findViewById(R.id.WebViewHelp) as WebView?
        _TVversionName = findViewById(R.id.textVersionName) as TextView?
        _TVversionName?.setText(
            getString(
                R.string.version_number,
                /*TODO BuildConfig.VERSION_NAME*/
            "2"
            )
        ) // "Version:  " + BuildConfig.VERSION_NAME
    }

    override fun onResume() {
        super.onResume()
        val intent: Intent = getIntent()
        // final Uri uri = intent.getData();
        val extras = intent.extras
        if (extras != null) {
            val s = extras.getString(HELP_MODE)
            _TVversionName!!.visibility = if (s == "about") View.VISIBLE else View.GONE
            _webview!!.loadUrl(
                "file:///android_asset/" + s + "-" + _lang
                        + ".html"
            )
        }
    }

    companion object {
        const val TAG = "HtmlActivity"
        var HELP_MODE = "HELP_MODE"
    }
}
