package com.callor.lession.todolist;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.callor.lession.todolist.naver.AudioWriterPCM;
import com.callor.lession.todolist.naver.Naver_Secret;

import java.lang.ref.WeakReference;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class NaverClova extends AppCompatActivity {


    // naver clova 설정
    private final String CLIENT_ID = Naver_Secret.CLIENT_ID;

    // Naver와 hand Shake 할 클래스 선언
    private RecogHandle handler ; // 네이버로 부터 메시지를 수신하는 클래스
    private NaverRecognizer naverRecognizer; // 네이버와 교신하는 클래스
    private String strResult; // 음성인식된 text를 임시로 보관할 변수
    private AudioWriterPCM writer; // 네이버에 음성을 보내는 클래스

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();

    private TextView mContentView;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_naver_clova);

        // 핸들러 초기화
        handler = new RecogHandle(this);
        naverRecognizer = new NaverRecognizer(this,handler,CLIENT_ID);

        mVisible = true;

        mContentView = findViewById(R.id.fullscreen_content);
        mContentView.setText("나를 터치하세요");

        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                naverRecognizer.recognize();
            }

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        // 음성인식을 열어주세요
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 음성인식을 닫아 주세요
        naverRecognizer.getSpeechRecognizer().release();
    }

    // 화면이 그려지고 난 후 실행되는 자동메서드
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    private void handleMessage(Message msg){

        // naver가 보낸 메시지를 분석
        switch(msg.what) { // command
            case R.id.clientReady :// 음성인식을 할 준비가 되었다라는 command
                mContentView.setText("말을 시작하세요");
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath()+"/naverSpech"
                );
                writer.open("naver_speech");
                break;

            case R.id.audioRecording :
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult: // 음성인식중
                strResult = (String)msg.obj;
                mContentView.setText(strResult);
                break;

            case R.id.finalResult: // 음성인식 완료
                break;

            case R.id.recognitionError: // 인식과정에서 오류가 발생
                break;

            case R.id.clientInactive: // 네이버 음성인식기에서 접속 거부
                break;
        }
    }

    // Naver로 부터 데이터(Text)를 받을 클래스
    class RecogHandle extends Handler {

        // 현재 Activity 내에서 백그라운드로 실행 하도록 하는 절차
        private final WeakReference<NaverClova> thisActivity;
        RecogHandle(NaverClova activity) {
            thisActivity = new WeakReference<NaverClova>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // inner class에서 outter class의 method를 사용하는 방법
             NaverClova activity = thisActivity.get();
            if(activity != null) {
                activity.handleMessage(msg);
            }

        }
    }

}
