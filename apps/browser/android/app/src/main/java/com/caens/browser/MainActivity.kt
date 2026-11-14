package com.caens.browser

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.SafeBrowsingResponse
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

/**
 * CAENS Browser 0.1 — Android WebView shell.
 *
 * Internet Mode is a convenience policy inside this client. It is NOT a
 * security boundary. CAENS services must independently enforce server-side
 * authentication, device authorization and network policy.
 */
class MainActivity : Activity() {
    private lateinit var webView: WebView
    private lateinit var addressBar: EditText
    private lateinit var modeLabel: TextView
    private lateinit var modeButton: Button
    private var internetMode = true

    // Replace with the real CAENS gateway hostname after it is provisioned.
    // Until then, CAENS-only mode intentionally has no reachable private site.
    private val caensHostSuffixes = listOf("caens.local", "caens.internal")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(17, 19, 21)
        window.navigationBarColor = Color.rgb(17, 19, 21)
        buildInterface()
        configureWebView()
        updateModeUi()
        if (savedInstanceState == null) {
            showHomeMessage()
        } else {
            webView.restoreState(savedInstanceState)
        }
    }

    private fun buildInterface() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(17, 19, 21))
            setPadding(dp(12), dp(12), dp(12), dp(8))
        }

        val header = TextView(this).apply {
            text = "CAENS  /  BROWSER"
            textSize = 14f
            setTextColor(Color.rgb(207, 255, 220))
            letterSpacing = 0.12f
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(4), dp(4), dp(4), dp(12))
        }
        root.addView(header, matchWrap())

        val modeRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(4), dp(4), dp(4), dp(10))
        }
        modeLabel = TextView(this).apply {
            textSize = 13f
            setTextColor(Color.LTGRAY)
        }
        modeRow.addView(modeLabel, LinearLayout.LayoutParams(0, dp(44), 1f))
        modeButton = Button(this).apply {
            textSize = 12f
            setOnClickListener {
                internetMode = !internetMode
                updateModeUi()
                if (!internetMode) {
                    val current = webView.url
                    if (current != null && !isCaensHost(Uri.parse(current).host)) {
                        showHomeMessage()
                    }
                }
            }
        }
        modeRow.addView(modeButton, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, dp(44)
        ))
        root.addView(modeRow, matchWrap())

        val addressRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        addressBar = EditText(this).apply {
            setSingleLine(true)
            textSize = 14f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            hint = "Enter website or search"
            setPadding(dp(12), 0, dp(12), 0)
            setBackgroundColor(Color.rgb(34, 38, 42))
            imeOptions = EditorInfo.IME_ACTION_GO
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    navigateFromAddress()
                    true
                } else false
            }
        }
        addressRow.addView(addressBar, LinearLayout.LayoutParams(0, dp(48), 1f))
        val goButton = Button(this).apply {
            text = "GO"
            setOnClickListener { navigateFromAddress() }
        }
        addressRow.addView(goButton, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, dp(48)
        ))
        root.addView(addressRow, matchWrap())

        val controls = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        listOf("BACK", "FORWARD", "RELOAD", "HOME").forEach { label ->
            controls.addView(Button(this).apply {
                text = label
                textSize = 10f
                setOnClickListener {
                    when (label) {
                        "BACK" -> if (webView.canGoBack()) webView.goBack()
                        "FORWARD" -> if (webView.canGoForward()) webView.goForward()
                        "RELOAD" -> webView.reload()
                        "HOME" -> showHomeMessage()
                    }
                }
            }, LinearLayout.LayoutParams(0, dp(44), 1f))
        }
        root.addView(controls, matchWrap())

        webView = WebView(this)
        root.addView(webView, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        setContentView(root)
    }

    private fun configureWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadsImagesAutomatically = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            setSupportMultipleWindows(false)
            javaScriptCanOpenWindowsAutomatically = false
            allowFileAccess = false
            allowContentAccess = false
            safeBrowsingEnabled = true
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean = !isAllowedDestination(request.url)

            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (url != null) addressBar.setText(url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url != null) addressBar.setText(url)
            }
        }
    }

    private fun navigateFromAddress() {
        val raw = addressBar.text.toString().trim()
        if (raw.isEmpty()) return

        val destination = when {
            raw.startsWith("https://", true) || raw.startsWith("http://", true) -> Uri.parse(raw)
            raw.contains(".") && !raw.contains(" ") -> Uri.parse("https://$raw")
            else -> Uri.parse("https://www.google.com/search").buildUpon()
                .appendQueryParameter("q", raw).build()
        }

        if (!isAllowedDestination(destination)) {
            Toast.makeText(this, "Internet Mode is OFF. Public websites are blocked.", Toast.LENGTH_LONG).show()
            return
        }
        webView.loadUrl(destination.toString())
    }

    private fun isAllowedDestination(uri: Uri): Boolean {
        val scheme = uri.scheme?.lowercase() ?: return false
        if (scheme != "https" && scheme != "http") return false
        val host = uri.host ?: return false
        if (internetMode) return true
        return isCaensHost(host)
    }

    private fun isCaensHost(host: String?): Boolean {
        val normalized = host?.lowercase()?.trimEnd('.') ?: return false
        return caensHostSuffixes.any { normalized == it || normalized.endsWith(".$it") }
    }

    private fun updateModeUi() {
        modeLabel.text = if (internetMode) {
            "Internet Mode: ON"
        } else {
            "Internet Mode: OFF · CAENS hosts only"
        }
        modeButton.text = if (internetMode) "TURN OFF" else "TURN ON"
    }

    private fun showHomeMessage() {
        val html = """
            <html><head><meta name="viewport" content="width=device-width, initial-scale=1">
            <style>
            body{background:#111315;color:#f3f5f6;font:16px system-ui;padding:28px;line-height:1.6}
            h1{color:#cfffdc}p{color:#aab3ba}.panel{background:#1b1f22;border:1px solid #30363b;border-radius:16px;padding:18px}
            </style></head><body><h1>CAENS Browser</h1>
            <div class="panel"><b>Browser prototype is ready.</b>
            <p>Internet Mode ON allows public websites.</p>
            <p>Internet Mode OFF blocks public websites and reserves navigation for CAENS gateway hosts.</p>
            <p>The private CAENS gateway is not configured yet.</p></div></body></html>
        """.trimIndent()
        webView.loadDataWithBaseURL("https://start.caens.local/", html, "text/html", "UTF-8", null)
        addressBar.setText("")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    @Deprecated("Deprecated in Android 13; retained for broad device compatibility")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.stopLoading()
            webView.destroy()
        }
        super.onDestroy()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun matchWrap() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
    )
}
