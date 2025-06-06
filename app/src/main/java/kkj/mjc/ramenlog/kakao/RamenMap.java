package kkj.mjc.ramenlog.kakao;

import android.app.Application;
import android.util.Log;

import com.kakao.vectormap.KakaoMapSdk;

public class RamenMap extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KakaoMapSdk.init(this, "b4c7768420bb1c9e89820c47bb176832");
        Log.d("KakaoMap", "Application started");
    }
}
