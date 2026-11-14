package com.caens.browser

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

/** CAENS Browser prototype. World mode is client-side only; server security is still required. */
class MainActivity : Activity() {
    private lateinit var webView: WebView
    private lateinit var addressBar: EditText
    private lateinit var worldButton: Button
    private lateinit var header: LinearLayout
    private var worldMode = true
    private var selectedTheme = 0

    private data class Theme(val name: String, val start: Int, val end: Int, val accent: Int)
    private val themes = listOf(
        Theme("Ocean Blue", Color.rgb(133,190,245), Color.rgb(205,230,255), Color.rgb(48,126,205)),
        Theme("Coral Red", Color.rgb(244,151,151), Color.rgb(255,220,210), Color.rgb(204,75,75)),
        Theme("Lavender", Color.rgb(177,160,239), Color.rgb(231,220,255), Color.rgb(117,91,190)),
        Theme("Mint Green", Color.rgb(133,207,174), Color.rgb(218,244,226), Color.rgb(54,139,100)),
        Theme("Warm Amber", Color.rgb(244,190,117), Color.rgb(255,235,198), Color.rgb(181,119,42))
    )
    private val caensHosts = listOf("caens.local", "caens.internal")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(247,248,250)
        window.navigationBarColor = Color.rgb(250,250,248)
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        buildInterface()
        configureWebView()
        updateWorldButton()
        if (savedInstanceState == null) showHome() else webView.restoreState(savedInstanceState)
    }

    private fun buildInterface() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(250,250,248))
            setPadding(dp(12), dp(8), dp(12), dp(6))
        }
        header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(14))
            background = gradient(themes[selectedTheme])
        }
        val topLine = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val brand = TextView(this).apply {
            text = "caens"; textSize = 25f; setTextColor(Color.rgb(24,35,48))
            setTypeface(null, android.graphics.Typeface.BOLD); letterSpacing = -0.03f
        }
        topLine.addView(brand, LinearLayout.LayoutParams(0, dp(44), 1f))
        topLine.addView(Button(this).apply {
            text = "◉ Theme"; textSize = 11f; setTextColor(Color.rgb(40,48,58))
            background = rounded(Color.WHITE, 0x55FFFFFF, 22)
            setOnClickListener { showThemePicker() }
        }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(40)))
        header.addView(topLine, matchWrap())

        val navLine = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(10), 0, 0)
        }
        listOf("Home", "Bookmarks", "History").forEachIndexed { index, label ->
            navLine.addView(TextView(this).apply {
                text = label; textSize = 13f; gravity = Gravity.CENTER
                setTextColor(if (index == 0) Color.WHITE else Color.rgb(55,67,82))
                background = if (index == 0) rounded(Color.rgb(28,38,52), 0, 24) else null
                setPadding(dp(15), dp(9), dp(15), dp(9))
                setOnClickListener {
                    if (index != 0) Toast.makeText(this@MainActivity, "$label will be added in a later update.", Toast.LENGTH_SHORT).show()
                    else showHome()
                }
            })
        }
        navLine.addView(TextView(this).apply { text = "" }, LinearLayout.LayoutParams(0, 1, 1f))
        worldButton = Button(this).apply {
            textSize = 12f; setPadding(dp(12), 0, dp(12), 0)
            setOnClickListener {
                worldMode = !worldMode
                updateWorldButton()
                if (!worldMode) {
                    val host = webView.url?.let { Uri.parse(it).host }
                    if (host != null && !isCaensHost(host)) showHome()
                }
            }
        }
        navLine.addView(worldButton, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(40)))
        header.addView(navLine, matchWrap())
        root.addView(header, matchWrap())

        val addressRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(12), 0, dp(8))
        }
        addressBar = EditText(this).apply {
            setSingleLine(true); textSize = 14f
            setTextColor(Color.rgb(35,42,50)); setHintTextColor(Color.rgb(135,141,148))
            hint = "Search or enter website address"; setPadding(dp(14), 0, dp(14), 0)
            background = rounded(Color.WHITE, 0xFFE5E7EB.toInt(), 18)
            imeOptions = EditorInfo.IME_ACTION_GO
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) { navigate(); true } else false
            }
        }
        addressRow.addView(addressBar, LinearLayout.LayoutParams(0, dp(48), 1f))
        addressRow.addView(Button(this).apply {
            text = "Go"; textSize = 13f; setTextColor(Color.WHITE)
            background = rounded(themes[selectedTheme].accent, 0, 16)
            setOnClickListener { navigate() }
        }, LinearLayout.LayoutParams(dp(58), dp(48)).apply { leftMargin = dp(6) })
        root.addView(addressRow, matchWrap())

        val controls = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER }
        listOf("‹", "›", "↻", "⌂").forEachIndexed { index, symbol ->
            controls.addView(Button(this).apply {
                text = symbol; textSize = 21f; setTextColor(Color.rgb(70,78,88))
                background = rounded(Color.WHITE, 0xFFE8E9EC.toInt(), 14)
                setOnClickListener {
                    when (index) {
                        0 -> if (webView.canGoBack()) webView.goBack()
                        1 -> if (webView.canGoForward()) webView.goForward()
                        2 -> webView.reload()
                        3 -> showHome()
                    }
                }
            }, LinearLayout.LayoutParams(0, dp(42), 1f).apply { leftMargin = dp(3); rightMargin = dp(3) })
        }
        root.addView(controls, matchWrap())
        webView = WebView(this).apply { setBackgroundColor(Color.rgb(250,250,248)) }
        root.addView(webView, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        setContentView(root)
    }

    private fun configureWebView() {
        webView.settings.apply {
            javaScriptEnabled = true; domStorageEnabled = true; loadsImagesAutomatically = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            setSupportMultipleWindows(false); javaScriptCanOpenWindowsAutomatically = false
            allowFileAccess = false; allowContentAccess = false; safeBrowsingEnabled = true
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = !isAllowed(request.url)
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (url != null) addressBar.setText(url)
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url != null && !url.startsWith("https://start.caens.local")) addressBar.setText(url)
            }
        }
    }

    private fun navigate() {
        val raw = addressBar.text.toString().trim()
        if (raw.isEmpty()) return
        val uri = when {
            raw.startsWith("https://", true) || raw.startsWith("http://", true) -> Uri.parse(raw)
            raw.contains(".") && !raw.contains(" ") -> Uri.parse("https://$raw")
            else -> Uri.parse("https://www.google.com/search").buildUpon().appendQueryParameter("q", raw).build()
        }
        if (!isAllowed(uri)) {
            Toast.makeText(this, "World is OFF. Public websites are blocked.", Toast.LENGTH_LONG).show()
            return
        }
        webView.loadUrl(uri.toString())
    }

    private fun isAllowed(uri: Uri): Boolean {
        val scheme = uri.scheme?.lowercase() ?: return false
        val host = uri.host ?: return false
        if (scheme != "https" && scheme != "http") return false
        return worldMode || isCaensHost(host)
    }

    private fun isCaensHost(host: String): Boolean {
        val h = host.lowercase().trimEnd('.')
        return caensHosts.any { h == it || h.endsWith(".$it") }
    }

    private fun updateWorldButton() {
        worldButton.text = if (worldMode) "🌐 World ON" else "🌐 World OFF"
        worldButton.setTextColor(if (worldMode) Color.WHITE else Color.rgb(45,53,62))
        worldButton.background = rounded(if (worldMode) Color.rgb(28,38,52) else Color.WHITE, if (worldMode) 0 else 0xFFE5E7EB.toInt(), 22)
    }

    private fun showThemePicker() {
        AlertDialog.Builder(this).setTitle("Choose browser colour")
            .setSingleChoiceItems(themes.map { it.name }.toTypedArray(), selectedTheme) { dialog, which ->
                selectedTheme = which
                header.background = gradient(themes[selectedTheme])
                dialog.dismiss()
                showHome()
            }.setNegativeButton("Cancel", null).show()
    }

    private fun showHome() {
        val accent = String.format("#%06X", 0xFFFFFF and themes[selectedTheme].accent)
        val worldText = if (worldMode) "allowed" else "blocked"
        val html = """
            <!doctype html><html><head><meta name="viewport" content="width=device-width, initial-scale=1">
            <style>
            *{box-sizing:border-box}body{margin:0;padding:20px 10px 32px;background:#fafaf8;color:#20242a;font:15px system-ui,-apple-system,sans-serif}
            .welcome{font-size:23px;font-weight:650;margin:4px 0 5px}.sub{color:#737982;margin:0 0 22px}
            .section{font-size:19px;font-weight:650;margin:22px 0 5px}.muted{color:#777e87;font-size:13px;margin:0 0 14px}
            .tiles{display:flex;gap:12px;overflow-x:auto;padding:2px 0 8px}
            .tile{flex:0 0 76%;background:#fff;border:1px solid #eceef0;border-radius:19px;padding:18px;box-shadow:0 5px 18px #27384a0b;min-height:145px}
            .folder{font-size:38px;margin-bottom:12px}.tile b{font-size:17px}.tile small{display:block;color:#858b92;margin-top:5px}
            .chips{display:flex;gap:8px;overflow-x:auto;margin:14px 0 12px}
            .chip{white-space:nowrap;border:1px solid #e7e8ea;background:#fff;border-radius:24px;padding:10px 15px;color:#555d66}
            .chip.active{border-color:$accent;color:$accent;background:#fff}
            .item{display:flex;align-items:center;gap:12px;background:#fff;border:1px solid #eceef0;border-radius:15px;padding:12px;margin:9px 0}
            .ico{width:42px;height:42px;border-radius:12px;background:#f6f7f7;display:grid;place-items:center;font-size:20px}
            .item b{font-size:14px;font-weight:600}.item small{display:block;color:#8a9097;margin-top:4px}
            .tag{font-size:11px;color:$accent;background:#f1f5fb;border-radius:8px;padding:4px 7px;margin-left:5px}
            </style></head><body>
            <div class="welcome">Welcome to CAENS</div><p class="sub">A calmer way to explore your digital world.</p>
            <div class="section">Quick access</div><p class="muted">Your browser shortcuts, all in one place.</p>
            <div class="tiles"><div class="tile"><div class="folder">🔖</div><b>Bookmarks</b><small>Your saved websites</small></div>
            <div class="tile"><div class="folder">🕘</div><b>Recent history</b><small>Continue where you left off</small></div></div>
            <div class="section">Explore</div><p class="muted">Search the web or open a destination.</p>
            <div class="chips"><span class="chip active">✦ Discover</span><span class="chip">News</span><span class="chip">Technology</span><span class="chip">Learning</span></div>
            <div class="item"><div class="ico">🌐</div><div><b>World internet <span class="tag">Available</span></b><small>Public websites are currently $worldText</small></div></div>
            <div class="item"><div class="ico">◈</div><div><b>CAENS network</b><small>Private gateway will be connected later</small></div></div>
            </body></html>
        """.trimIndent()
        webView.loadDataWithBaseURL("https://start.caens.local/", html, "text/html", "UTF-8", null)
        addressBar.setText("")
    }

    private fun gradient(theme: Theme) = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(theme.start, theme.end)
    ).apply { cornerRadius = dp(22).toFloat() }

    private fun rounded(color: Int, stroke: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        if (stroke != 0) setStroke(dp(1), stroke)
        cornerRadius = dp(radius).toFloat()
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    private fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    @Deprecated("Deprecated in Android 13; retained for compatibility")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        if (::webView.isInitialized) { webView.stopLoading(); webView.destroy() }
        super.onDestroy()
    }
}
