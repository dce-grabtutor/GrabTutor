package com.dce.grabtutor;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.dce.grabtutor.Service.Model.URI;

public class TutorAptitudeTestExamActivity extends AppCompatActivity {

    WebView wvAptitudeTestExam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_aptitude_test_exam);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Aptitude Exam");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wvAptitudeTestExam = (WebView) findViewById(R.id.wvAptitudeTestExam);
        wvAptitudeTestExam.getSettings().setJavaScriptEnabled(true);
        wvAptitudeTestExam.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wvAptitudeTestExam.addJavascriptInterface(new WebAppInterface(this), "Android");
        wvAptitudeTestExam.loadUrl(URI.APTITUDE_EXAM + "?acc_id=5&qna_id=1");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    public class WebAppInterface {
        Context context;

        WebAppInterface(Context context){
            this.context = context;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
