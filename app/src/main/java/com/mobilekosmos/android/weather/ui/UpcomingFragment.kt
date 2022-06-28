package com.mobilekosmos.android.weather.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobilekosmos.android.weather.R
import com.mobilekosmos.android.weather.data.model.WeatherForecastEntity
import com.mobilekosmos.android.weather.data.network.WeatherForecastApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException


open class UpcomingFragment : Fragment(), ForecastAdapter.OnForecastClickListener {

    lateinit var mListAdapter: ForecastAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_upcoming, container, false)

        mListAdapter = ForecastAdapter(ForecastAdapter.Mode.DEFAULT, this)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = mListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        return rootView
    }

    override fun onResume() {
        super.onResume()
        // Since the MainActivity doesn't recreate because of "singleTask" in the manifest we load here so the list
        // gets reloaded when coming back from the detail activity.
        fetchData()
    }

    /**
     * Fetches data using Retrofit which is configured to cache the response for a short period of time.
     * (Good solution for simple requests and responses, infrequent network calls, or small datasets.)
     */
    private fun fetchData() {
        // Any coroutine launched in this scope is canceled when the Lifecycle is destroyed.
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val weatherForecasts = withContext(Dispatchers.IO) {
                    WeatherForecastApi.RETROFIT_SERVICE.getWeatherForecasts()
                }
                // lifecycleScope uses the MainThread Dispatcher by default so we don't need to use "withContext(Dispatchers.Main)"
                // to access the UI.
                mListAdapter.setData(weatherForecasts)
            } catch (ex: HttpException) {
                Toast.makeText(
                    context?.applicationContext,
                    getString(R.string.error_downloading),
                    Toast.LENGTH_LONG
                ).show()
                // TODO: extend/improve error handling: check internet connection, show UI element
                //  to retry loading or hear for connection state changes.
            }
        }
    }

    override fun onForecastClick(forecastObject: WeatherForecastEntity) {
        val intent = Intent(context, DetailsActivity::class.java)
        intent.putExtra(DetailsActivity.EXTRA_FORECAST_OBJECT, forecastObject)
        startActivity(intent)
    }
}