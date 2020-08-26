package id.mobilecomputing.mc_responsi.activity.weather;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.mobilecomputing.mc_responsi.R;
import id.mobilecomputing.mc_responsi.activity.auth.LoginActivity;
import id.mobilecomputing.mc_responsi.adapter.CityWeatherAdapter;
import id.mobilecomputing.mc_responsi.helper.GPSTracker;
import id.mobilecomputing.mc_responsi.helper.ItemTouchHelperCallback;
import id.mobilecomputing.mc_responsi.interfaces.onSwipeListener;
import id.mobilecomputing.mc_responsi.models.CityWeather;
import id.mobilecomputing.mc_responsi.models.WeatherDetails;
import id.mobilecomputing.mc_responsi.network.APIClient;
import id.mobilecomputing.mc_responsi.network.APIInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends AppCompatActivity {

    private List<CityWeather> cities;
    @BindView(R.id.rv_weathercards) RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fab_addCity) FloatingActionButton fab_addCity;
    private APIInterface apiInterface;
    private MaterialTapTargetPrompt mFabPrompt;
    private static final String LAYOUT_MANAGER_STATE = "LAYOUT_MANAGER_STATE";
    private Parcelable mLayoutManagerState;
    private Parcelable recyclerViewState;
    private final String LIST_STATE_KEY = "recycler_state";
    private final String RECVIEW_DATA_ID = "data_id";

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener fireAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        fireAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user1 = firebaseAuth.getCurrentUser();

                if (user1 == null){
                    MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        };


        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        checkLocationPermission();

        if(savedInstanceState != null){
            mLayoutManagerState = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(mLayoutManagerState);
        }
        cities = getCities();
        if(cities.size() == 0){
            showFabPrompt();
        }

        apiInterface = APIClient.getApi().create(APIInterface.class);
        initUI();

    }

    private void initUI() {
        layoutManager = new LinearLayoutManager(this);
        adapter = new CityWeatherAdapter(cities, R.layout.weather_list, this, new CityWeatherAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CityWeather cityWeather, int position, View clickView) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        MainActivity.this,clickView,
                        "weatherCardTransition");

                intent.putExtra("city",  cityWeather);
                startActivity(intent,options.toBundle());
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy >0) {
                    // Scroll Down
                    if (fab_addCity.isShown()) {
                        fab_addCity.hide();
                    }
                }
                else if (dy <0) {
                    // Scroll Up
                    if (!fab_addCity.isShown()) {
                        fab_addCity.show();
                    }
                }
            }
        });

        fab_addCity.setOnClickListener(view -> {
            showAlertAddCity("Tambah kota","Ketik kota yang ingin ditambahkan");
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue, R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
        });
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback((onSwipeListener) adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }


    public boolean checkLocationPermission()
        {
            String permission = "android.permission.ACCESS_FINE_LOCATION";
            int res = this.checkCallingOrSelfPermission(permission);
            return (res == PackageManager.PERMISSION_GRANTED);
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("WORK FFS","Oncreaoptionsmenu called");
        getMenuInflater().inflate(R.menu.account_menu_options,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("WORK FFS","onOptionItem called");
        switch (item.getItemId()){
            case R.id.menu_acc_signout:
                firebaseAuth.signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showFabPrompt()
    {
        if (mFabPrompt != null)
        {
            return;
        }
        mFabPrompt = new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(findViewById(R.id.fab_addCity))
                .setFocalPadding(R.dimen.dp40)
                .setPrimaryText("Tambahkan kota")
                .setSecondaryText("Tap disini untuk menambahkan kota dan mendapatkan update tentang cuaca")
                .setBackButtonDismissEnabled(true)
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_DISMISSING )
                    {
                        mFabPrompt = null;
                    }
                })
                .create();
        mFabPrompt.show();
    }

    private void refreshData() {
        for (int i = 0; i < cities.size(); i++) {
            updateCity(cities.get(i).getCity().getName(), i);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private String cityToAdd ="";

    private void updateCity(String cityName, int index) {
        Call<CityWeather> cityWeather = apiInterface.getWeatherCity(cityName, APIClient.KEY, "metric",6);
        cityWeather.enqueue(new Callback<CityWeather>() {
            @Override
            public void onResponse(Call<CityWeather> call, Response<CityWeather> response) {
                if(response.code()==200){
                    CityWeather cityWeather = response.body();
                    cities.remove(index);
                    cities.add(index,cityWeather);
                    adapter.notifyItemChanged(index);
                }
            }

            @Override
            public void onFailure(Call<CityWeather> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Cek koneksi internet",Toast.LENGTH_LONG).show();
            }
        });
    }



    private void showAlertAddCity(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(title!=null) builder.setTitle(title);
        if(message!=null) builder.setMessage(message);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_city,null);
        builder.setView(view);
        final TextView editTextAddCityName = view.findViewById(R.id.edt_addcity);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        builder.setNeutralButton("Detect Location", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getLocationFromGPS();
            }
        });

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cityToAdd = editTextAddCityName.getText().toString();
                addCity(cityToAdd);
                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS,0);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS,0);
                Toast.makeText(MainActivity.this,"Cancel",Toast.LENGTH_LONG).show();
            }
        });
        builder.create().show();
    }

    private void getLocationFromGPS() {
        GPSTracker gpsTracker = new GPSTracker(this);
        if(gpsTracker.getIsGPSTrackingEnabled())
        {
            String stringLatitude = String.valueOf(gpsTracker.latitude);
            String stringLongitude = String.valueOf(gpsTracker.longitude);
            Call<CityWeather> cityWeather = apiInterface.getWeatherGPS(stringLatitude,
                    stringLongitude, APIClient.KEY,6, "metric");
            cityWeather.enqueue(new Callback<CityWeather>() {
                @Override
                public void onResponse(Call<CityWeather> call, Response<CityWeather> response) {
                    if(response.code() == 200){
                        CityWeather cityWeather = response.body();
                        cities.add(cityWeather);
                        adapter.notifyItemInserted(cities.size()-1);
                        recyclerView.scrollToPosition(cities.size()-1);
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Cek Permission GPS", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<CityWeather> call, Throwable t) {
                    Toast.makeText(MainActivity.this,"Cek koneksi internet/GPS",Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    private void addCity(String cityName) {
        Call<CityWeather> cityWeather = apiInterface.getWeatherCity(cityName, APIClient.KEY, "metric",6);
        cityWeather.enqueue(new Callback<CityWeather>() {
            @Override
            public void onResponse(Call<CityWeather> call, Response<CityWeather> response) {
                if(response.code()==200){
                    CityWeather cityWeather = response.body();
                    cities.add(cityWeather);
                    adapter.notifyItemInserted(cities.size()-1);
                    recyclerView.scrollToPosition(cities.size()-1);

                }else{
                    Toast.makeText(MainActivity.this,"Kota tidak ditemukan",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<CityWeather> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Cek koneksi internet",Toast.LENGTH_LONG).show();
            }
        });
    }


    private List<CityWeather> getCities() {
        return new ArrayList<CityWeather>(){
            {
            }
        };
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable(LAYOUT_MANAGER_STATE, mLayoutManagerState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLayoutManagerState = recyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(fireAuthListener != null){
            firebaseAuth.removeAuthStateListener(fireAuthListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(fireAuthListener);
    }
}
