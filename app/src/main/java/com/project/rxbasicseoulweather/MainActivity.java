package com.project.rxbasicseoulweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.project.rxbasicseoulweather.domain.Row;
import com.project.rxbasicseoulweather.domain.Weather;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 1. 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IWeather.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        // 2. 서비스 만들기 <인터페이스로부터
        IWeather service = retrofit.create(IWeather.class);

        // 3. 옵저버블 생성
        Observable<Weather> observable = service.getData(IWeather.SERVER_KEY, 1, 10, "동작");

        // 4. 발행
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Weather>() {
            @Override
            public void accept(Weather weather) throws Exception {
                String result = "";
                for(Row row : weather.getRealtimeWeatherStation().getRow()){
                    result += "지역명 : " + row.getNAME() + "\n"
                            + "온도 : " + row.getSAWS_TA_AVG() + "\n"
                            + "습도 : " + row.getSAWS_HD() + "\n\n";
                }
                ((TextView) findViewById(R.id.textView)).setText(result);
            }
        });

    }
}

// 0. 레트로핏 인터페이스 생성
interface IWeather{

    String SERVER_URL = "http://openapi.seoul.go.kr:8088/";
    String SERVER_KEY = "686a766b466a697334385a717a6e51";

    @GET("{key}/json/RealtimeWeatherStation/{skip}/{count}/{gu}")
    Observable<Weather> getData(@Path("key") String serverKey, @Path("skip") int skip, @Path("count") int count, @Path("gu") String gu);
}
