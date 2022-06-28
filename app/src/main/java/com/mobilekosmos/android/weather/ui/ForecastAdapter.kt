package com.mobilekosmos.android.weather.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobilekosmos.android.weather.R
import com.mobilekosmos.android.weather.data.model.WeatherForecastEntity
import com.mobilekosmos.android.weather.ui.ForecastAdapter.DayViewHolder

class ForecastAdapter(mode: Mode, clickListener: OnForecastClickListener?) :
    RecyclerView.Adapter<DayViewHolder>() {

    // We don't use lateinit because we cannot guaranty it's not accessed before initialization (getItemCount() is being called before).
    private var mDataset: List<WeatherForecastEntity>? = null

    private var mMode: Mode
    private val mOnClickListener: OnForecastClickListener

    init {
        requireNotNull(clickListener) { "You must pass a clickListener." }
        mMode = mode
        mOnClickListener = clickListener
    }

    interface OnForecastClickListener {
        fun onForecastClick(forecastObject: WeatherForecastEntity)
    }

    enum class Mode {
        DEFAULT, HOTTEST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast, parent, false)
        val viewHolder = DayViewHolder(view)
        viewHolder.itemView.setOnClickListener { v: View ->
            mDataset?.let {
                val pos = v.tag as Int
                val forecastObject = it[pos]
                mOnClickListener.onForecastClick(forecastObject)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        mDataset?.let {
            val day = it[position].day
            val forecastDescription = it[position].description
            val descriptionText = "Day $day: $forecastDescription"
            holder.forecastTitleView.text = descriptionText
            holder.itemView.tag = position

            // When we download the images in the detail view we add the day as parameter to the
            // request so coil uses it as the cache-key.
            val url = it[position].image + "?" + day
            holder.forecastImageView.loadFromCache(url)
        }
    }

    override fun getItemCount(): Int {
        return mDataset?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(weatherForecasts: List<WeatherForecastEntity>) {
        mDataset = when (mMode) {
            Mode.DEFAULT -> weatherForecasts
            Mode.HOTTEST -> getFilteredData(weatherForecasts)
        }
        notifyDataSetChanged()
    }

    /**
     * Only save days with chance_rain < 0.5 and sort fom hottest to coldest days.
     *
     * @param weatherForecasts The list to be filtered and sorted.
     * @return the filtered and sorted list.
     */
    private fun getFilteredData(weatherForecasts: List<WeatherForecastEntity>): List<WeatherForecastEntity> {
        val newFilteredSortedlist: MutableList<WeatherForecastEntity> = ArrayList()
        for (i in weatherForecasts.indices) {
            val forecastEntity = weatherForecasts[i]
            if (forecastEntity.chance_rain < 0.5) {
                newFilteredSortedlist.add(forecastEntity)
            }
        }
        newFilteredSortedlist.sortWith(compareBy<WeatherForecastEntity> { it.high }.thenBy { it.low })
        newFilteredSortedlist.reverse()
        return newFilteredSortedlist
    }

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val forecastTitleView: TextView = itemView.findViewById(R.id.forecastDetailTitle)
        val forecastImageView: ImageView = itemView.findViewById(R.id.forecastImage)
    }
}