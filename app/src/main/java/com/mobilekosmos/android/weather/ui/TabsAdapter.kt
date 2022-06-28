package com.mobilekosmos.android.weather.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabsAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> UpcomingFragment()
            1 -> HottestFragment()
            else -> throw IllegalStateException("Invalid adapter position")
        }

}