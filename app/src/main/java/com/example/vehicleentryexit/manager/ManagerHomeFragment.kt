package com.example.vehicleentryexit.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.vehicleentryexit.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ManagerHomeFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1) // Extract param1 using the key
            param2 = it.getString(ARG_PARAM2) // Extract param2 using the key
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manager_home, container, false)
        webView = view.findViewById(R.id.managerWebView)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl("http://20.30.76.17:3000/") // Replace with your desired URL

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ManagerHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1) // Store param1 using the key
                    putString(ARG_PARAM2, param2) // Store param2 using the key
                }
            }
    }
}