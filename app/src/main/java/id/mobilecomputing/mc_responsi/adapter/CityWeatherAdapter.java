package id.mobilecomputing.mc_responsi.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.mobilecomputing.mc_responsi.R;
import id.mobilecomputing.mc_responsi.helper.IconProvider;
import id.mobilecomputing.mc_responsi.interfaces.onSwipeListener;
import id.mobilecomputing.mc_responsi.models.CityWeather;

public class CityWeatherAdapter extends RecyclerView.Adapter<CityWeatherAdapter.ViewHolder> implements onSwipeListener {
    private List<CityWeather> cities;
    private int layoutReference;
    private OnItemClickListener onItemClickListener;
    private Activity activity;
    private View parentView;
    public CityWeatherAdapter(List<CityWeather> cities, int layoutReference,  Activity activity,OnItemClickListener onItemClickListener) {
        this.cities = cities;
        this.layoutReference = layoutReference;
        this.activity = activity;
        this.onItemClickListener = onItemClickListener;

    }

    @Override
    public CityWeatherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentView = parent;
        View view = LayoutInflater.from(activity).inflate(layoutReference,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CityWeatherAdapter.ViewHolder holder, int position) {
        holder.bind(cities.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    @Override
    public void onItemDelete(final int position) {
        CityWeather tempCity = cities.get(position);
        cities.remove(position);
        notifyItemRemoved(position);

        Snackbar.make(parentView, "Removed", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> {
                    addItem(position, tempCity);
                }).show();

    }
    public void addItem(int position, CityWeather city) {
        cities.add(position, city);
        notifyItemInserted(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_cityName)
        TextView textViewCityName;
        @BindView(R.id.tv_weatherdesc) TextView textViewWeatherDescription;
        @BindView(R.id.tv_currentTemp) TextView textViewCurrentTemp;
        @BindView(R.id.tv_maxtemp) TextView textViewMaxTemp;
        @BindView(R.id.tv_mintemp) TextView  textViewMinTemp;
        @BindView(R.id.iv_cardweathericon)
        ImageView imageViewWeatherIcon;
        @BindView(R.id.cv_weathercardlist)
        CardView cardViewWeather;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public void bind(final CityWeather cityWeather, final OnItemClickListener onItemClickListener){
            textViewCityName.setText(cityWeather.getCity().getName()+", "+cityWeather.getCity().getCountry());
            textViewWeatherDescription.setText(cityWeather.getWeeklyWeather().get(0).getWeatherDetails().get(0).getLongDescription());
            textViewCurrentTemp.setText((int) cityWeather.getWeeklyWeather().get(0).getTemp().getDay()+"°");
            textViewMaxTemp.setText((int) cityWeather.getWeeklyWeather().get(0).getTemp().getMax()+"°");
            textViewMinTemp.setText((int) cityWeather.getWeeklyWeather().get(0).getTemp().getMin()+"°");
            String weatherDescription = cityWeather.getWeeklyWeather().get(0).getWeatherDetails().get(0).getShortDescription();
            Picasso.get().load(IconProvider.getImageIcon(weatherDescription)).into(imageViewWeatherIcon);

            cardViewWeather.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(cityWeather,getAdapterPosition(), cardViewWeather);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(CityWeather cityWeather , int position, View view);
    }

}