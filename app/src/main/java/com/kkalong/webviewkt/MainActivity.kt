package com.kkalong.webviewkt

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.*
import android.webkit.WebChromeClient.FileChooserParams
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.kkalong.webviewkt.databinding.ActivityMainBinding

interface IImageHandler {
    fun takePicture(callBack: ValueCallback<Array<Uri>>?)

    fun uploadImageOnPage(resultCode: Int, intent: Intent?)
}


class MainActivity : AppCompatActivity(), IImageHandler {
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!
    var filePathCallbackLollipop: ValueCallback<Array<Uri>>? = null
    private val CAPTURE_CAMERA_RESULT = 3089

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 동작되는 로직을 변환해주는 코드
        binding.webView.settings.apply{
            javaScriptEnabled = true
            domStorageEnabled = true
            setSupportMultipleWindows(true)
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams
        ): Boolean {
            // Callback 초기화 (중요!)
            if (filePathCallbackLollipop != null) {
                filePathCallbackLollipop?.onReceiveValue(null)
                filePathCallbackLollipop = null
            }
            filePathCallbackLollipop = filePathCallback
            val isCapture = fileChooserParams.isCaptureEnabled

            if (activity is IImageHandler) {
                activity.takePicture(filePathCallbackLollipop)
            }


            filePathCallbackLollipop = null
            return true
        }

        binding.webView.apply{
            // 새창이 뜨지 않도록 방지하는 구문
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            // 로드할 링크 주소
            loadUrl("http://k7b302.p.ssafy.io/")
        }
    }

    override fun onBackPressed() {
        // website에서 뒤로 갈 페이지가 존재 한다면...
        if (binding.webView.canGoBack()) {
            // website에서 뒤로가기
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

}

