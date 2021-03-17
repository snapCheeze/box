package rashed.hasan.schoolae;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // variables para manejar la subida de archivos
    private final static int FILECHOOSER_RESULTCODE = 1;
    // variable para manejar el navegador empotrado
    WebView mainWebView;
    private ValueCallback<Uri[]> mUploadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // instanciamos el webview
        mainWebView = findViewById(R.id.webview);

        // establecemos el cliente interno para que la navegacion no se salga de la aplicacion
        mainWebView.setWebViewClient(new MyWebViewClient());

        // establecemos el cliente chrome para seleccionar archivos
        mainWebView.setWebChromeClient(new MyWebChromeClient());

        // configuracion del webview
        mainWebView.getSettings().setJavaScriptEnabled(true);

        // cargamos la pagina
        mainWebView.loadUrl("https://schoolae.com/");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {


        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FILECHOOSER_RESULTCODE) {

            if (null == mUploadMessage || intent == null || resultCode != RESULT_OK) {
                return;
            }

            Uri[] result = null;
            String dataString = intent.getDataString();

            if (dataString != null) {
                result = new Uri[]{Uri.parse(dataString)};
            }

            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }


    // ====================
    // Web clients classes
    // ====================

    /**
     * Clase para configurar el webview
     */
    private class MyWebViewClient extends WebViewClient {

        // permite la navegacion dentro del webview
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


    /**
     * Clase para configurar el chrome client para que nos permita seleccionar archivos
     */
    private class MyWebChromeClient extends WebChromeClient {

        // maneja la accion de seleccionar archivos
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

            // asegurar que no existan callbacks
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }

            mUploadMessage = filePathCallback;

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*"); // set MIME type to filter

            MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.FILECHOOSER_RESULTCODE);

            return true;
        }
    }
    @Override
    public void onBackPressed() {
        if(mainWebView!= null && mainWebView.canGoBack())
            mainWebView.goBack();// if there is previous page open it
        else
            super.onBackPressed();//if there is no previous page, close app
    }

}

