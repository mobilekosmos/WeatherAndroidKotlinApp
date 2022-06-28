package com.mobilekosmos.android.weather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobilekosmos.android.weather.R


class HottestFragment : UpcomingFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_hottest, container, false)

        mListAdapter = ForecastAdapter(ForecastAdapter.Mode.HOTTEST, this)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = mListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        return rootView
    }
}