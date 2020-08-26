package id.mobilecomputing.mc_responsi.activity.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.mobilecomputing.mc_responsi.R;
import id.mobilecomputing.mc_responsi.helper.IconProvider;
import id.mobilecomputing.mc_responsi.models.CityWeather;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.tv_mintemp) TextView tv_minTemp;
    @BindView(R.id.tv_maxtemp) TextView tv_maxTemp;
    @BindView(R.id.tv_currentTemp) TextView tv_currentTemp;
    @BindView(R.id.tv_weatherdesc) TextView tv_weatherDesc;
    @BindView(R.id.tv_cityName) TextView tv_cityName;
    @BindView(R.id.tv_cloudiness) TextView tv_cloudiness;
    @BindView(R.id.tv_humidity) TextView tv_humidity;
    @BindView(R.id.tv_wind) TextView tv_wind;
    @BindView(R.id.tv_pressure) TextView tv_pressure;
    @BindView(R.id.tv_weatherday1) TextView tv_weatherDay1;
    @BindView(R.id.tv_weatherday2) TextView tv_weatherDay2;
    @BindView(R.id.tv_weatherday3) TextView tv_weatherDay3;
    @BindView(R.id.tv_weatherday4) TextView tv_weatherDay4;
    @BindView(R.id.tv_weatherday5) TextView tv_weatherDay5;
    @BindView(R.id.tv_maxtempday1) TextView tv_maxTempday1;
    @BindView(R.id.tv_maxtempday2) TextView tv_maxTempday2;
    @BindView(R.id.tv_maxtempday3) TextView tv_maxTempday3;
    @BindView(R.id.tv_maxtempday4) TextView tv_maxTempday4;
    @BindView(R.id.tv_maxtempday5) TextView tv_maxTempday5;
    @BindView(R.id.tv_mintempday1) TextView tv_minTempday1;
    @BindView(R.id.tv_mintempday2) TextView tv_minTempday2;
    @BindView(R.id.tv_mintempday3) TextView tv_minTempday3;
    @BindView(R.id.tv_mintempday4) TextView tv_minTempday4;
    @BindView(R.id.tv_mintempday5) TextView tv_minTempday5;
    @BindView(R.id.iv_weatherday1) ImageView iv_weatherDay1;
    @BindView(R.id.iv_weatherday2) ImageView iv_weatherDay2;
    @BindView(R.id.iv_weatherday3) ImageView iv_weatherDay3;
    @BindView(R.id.iv_weatherday4) ImageView iv_weatherDay4;
    @BindView(R.id.iv_weatherday5) ImageView iv_weatherDay5;
    @BindView(R.id.iv_cardweathericon) ImageView iv_cardWeatherIcon;

    private CityWeather cityWeather;
    String[] days={
            "SAT","SUN","MON", "TUE", "WED", "THU", "FRI",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if(!bundle.isEmpty()){
            cityWeather = (CityWeather) bundle.getSerializable("city");
        }
        setCardData();
    }

    private void setCardData() {
        tv_cityName.setText(cityWeather.getCity().getName()+", "+cityWeather.getCity().getCountry());
        tv_weatherDesc.setText(cityWeather.getWeeklyWeather().get(0).getWeatherDetails().get(0).getLongDescription());
        tv_currentTemp.setText((int)cityWeather.getWeeklyWeather().get(0).getTemp().getDay()+"°");
        tv_maxTemp.setText((int)cityWeather.getWeeklyWeather().get(0).getTemp().getMax()+"°");
        tv_minTemp.setText((int)cityWeather.getWeeklyWeather().get(0).getTemp().getMin()+"°");
        tv_humidity.setText((int)cityWeather.getWeeklyWeather().get(0).getHumidity()+"%");
        tv_wind.setText((int)cityWeather.getWeeklyWeather().get(0).getSpeed()+" m/s");
        tv_cloudiness.setText((int)cityWeather.getWeeklyWeather().get(0).getClouds()+"%");
        tv_pressure.setText((int)cityWeather.getWeeklyWeather().get(0).getPressure()+" hpa");

        String weatherDesc = cityWeather.getWeeklyWeather().get(0).getWeatherDetails().get(0).getShortDescription();
        Picasso.get().load(IconProvider.getImageIcon(weatherDesc)).into(iv_cardWeatherIcon);
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        for(int i = 1; i < cityWeather.getWeeklyWeather().size(); i++){
            calendar.setTime(date);
            String day = days[(calendar.get(Calendar.DAY_OF_WEEK)+i)%7];
            String weatherWeekDesc;

            switch (i){
                case 0:
                    break;

                case 1:
                    tv_weatherDay1.setText(day);
                    weatherWeekDesc = cityWeather.getWeeklyWeather().get(i).getWeatherDetails().get(0).getShortDescription();
                    Picasso.get().load(IconProvider.getImageIcon(weatherWeekDesc)).into(iv_weatherDay1);
                    tv_maxTempday1.setText((int)cityWeather.getWeeklyWeather().get(i).getTemp().getMax()+" °C");
                    tv_minTempday1.setText((int)cityWeather.getWeeklyWeather().get(i).getTemp().getMin()+" °C");
                    break;
                case 2:
                    tv_weatherDay2.setText(day);
                    weatherWeekDesc = cityWeather.getWeeklyWeather().get(i).getWeatherDetails().get(0).getShortDescription();
                    Picasso.get().load(IconProvider.getImageIcon(weatherWeekDesc)).into(iv_weatherDay1);
                    tv_maxTempday2.setText((int)cityWeather.getWeeklyWeather().get(i).getTemp().getMax()+" °C");
                    tv_minTempday2.setText((int)cityWeather.getWeeklyWeather().get(i).getTemp().getMin()+" °C");
                    break;
                case 3:
                    tv_weatherDay3.setText(day);
                    weatherWeekDesc = cityWeather.getWeeklyWeather().get(i).getWeatherDetails().get(0).getShortDescription();
                    Picasso.get().load(IconProvider.getImageIcon(weatherWeekDesc)).into(iv_weatherDay1);
                    tv_maxTempday3.setText((int)cityWeather.getWeeklyWeather().get(i).getTemp().getMax()+" °C");
                    tv_minTempday3.setText((int)cityWeather.getWeeklyWeather().get(i).getTemp().getMin()+" °C");
                    break;
                case 4:
                    tv_weatherDay4.setText(day);
                    weatherWeekDesc = cityWeather.getWeeklyWeather().get(i).getWeatherDetails().get(0).getShortDescription();
                    Picasso.get().load(IconProvider.getImageIcon(weatherWeekDesc)).into(iv_weatherDay1);
                    tv_maxTempday4.setText((int)cityWeather.getWeeklyWeather().get(i).getTemp().getMax()+" °C");
                    tv_minTempday4.setText((int)cityWeather.getWeeklyWeather().get(i).getTemp().getMin()+" °C");
                    break;
                case 5:
                    tv_weatherDay5.setText(day);
                    weatherWeekDesc = cityWeather.getWeeklyWeather().get(i).getWeatherDetails().get(0).getShortDescription();
                    Picasso.get().load(IconProvider.getImageIcon(weatherWeekDesc)).into(iv_weatherDay1);
                    tv_maxTempday5.setText((int)cityWeather.getWeeklyWeather().get(i).getTemp().getMax()+" °C");
                    tv_minTempday5.setText((int)cityWeather.getWeeklyWeather().get(i).getTemp().getMin()+" °C");
                    break;
                default:
                    break;
            }
        }
    }

}