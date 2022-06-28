package com.mobilekosmos.android.weather.ui

import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.mobilekosmos.android.weather.R
import com.mobilekosmos.android.weather.data.model.WeatherForecastEntity


class DetailsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FORECAST_OBJECT = "EXTRA_FORECAST_OBJECT"
    }

    private lateinit var mURL: String
    private lateinit var mDownloadImageButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val mForecastObject: WeatherForecastEntity = intent.getParcelableExtra(EXTRA_FORECAST_OBJECT)
            ?: throw IllegalArgumentException("You must pass a ForecastEntity to this activity.")

        displayData(mForecastObject)
        findViewById<Button>(R.id.forecastDetailDownloadImage).setOnClickListener { downloadTheImage() }
    }

    private fun displayData(mForecastObject: WeatherForecastEntity) {
        title = getString(R.string.detail_title, mForecastObject.day)

        val forecastDetailDescription = mForecastObject.description
        findViewById<TextView>(R.id.forecastDetailDescription).text =
            getString(R.string.detail_description, forecastDetailDescription)

        // TODO: better time formatting and localized.
        val sunriseTimeFormatted = DateUtils.formatElapsedTime(mForecastObject.sunrise)
        findViewById<TextView>(R.id.forecastDetailSunrise).text =
            getString(R.string.detail_sunrise, sunriseTimeFormatted)

        val sunsetFormatted = DateUtils.formatElapsedTime(mForecastObject.sunset)
        findViewById<TextView>(R.id.forecastDetailSunset).text =
            getString(R.string.detail_sunset, sunsetFormatted)

        val forecastDetailRainProbability = (mForecastObject.chance_rain * 100).toInt()
        findViewById<TextView>(R.id.forecastDetailRainProbability).text =
            getString(R.string.detail_rain, forecastDetailRainProbability)

        val forecastDetailHigh = mForecastObject.high
        findViewById<TextView>(R.id.forecastDetailHigh).text =
            getString(R.string.detail_max, forecastDetailHigh)

        val forecastDetailLow = mForecastObject.low
        findViewById<TextView>(R.id.forecastDetailLow).text =
            getString(R.string.detail_min, forecastDetailLow)

        // Since the api always use the same URL we want to avoid the loading library to cache the result
        // and thus not showing new images for different days. The parameter is expected to be ignored.
        mURL = mForecastObject.image + "?" + mForecastObject.day

        mDownloadImageButton = findViewById(R.id.forecastDetailDownloadImage)
        mDownloadImageButton.visibility = View.VISIBLE
    }

    private fun downloadTheImage() {
        // The API uses a URL from https://picsum.photos/ which redirects to a random image.
        val forecastDetailImage = findViewById<ImageView>(R.id.forecastDetailImageView)
        // TODO: check coil's caching behavior over time, if we better need to free something manually
        //  after some time or there are parameters we can define.
        forecastDetailImage.load(mURL) {
            crossfade(true)
            error(R.drawable.no_image)
            listener(
                onStart = {
                    mDownloadImageButton.visibility = View.GONE
                    // TODO: show some progress view.
                },
                onSuccess = { request: ImageRequest, successResult: SuccessResult ->
                    forecastDetailImage.visibility = View.VISIBLE
                },
                onError = { request: ImageRequest, errorResult: ErrorResult ->
                    mDownloadImageButton.visibility = View.VISIBLE
                    forecastDetailImage.visibility = View.VISIBLE
                    Toast.makeText(
                        this@DetailsActivity,
                        getString(R.string.detail_error_downloading),
                        Toast.LENGTH_SHORT
                    ).show()
                    // TODO: extend error handling, for example when offline.
                }
            )

        }
    }
}
