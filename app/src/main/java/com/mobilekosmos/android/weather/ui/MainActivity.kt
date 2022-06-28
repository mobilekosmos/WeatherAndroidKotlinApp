package com.mobilekosmos.android.weather.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mobilekosmos.android.weather.R

class MainActivity : AppCompatActivity() {

    // TODO: alternatively use Jetpack Compose instead.
    // TODO: implement MVVM and share dataset between fragments.
    // TODO: handle internet connectivity.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setTitle(R.string.main_title)

        initTabs()
    }

    private fun initTabs() {
        val viewPager = findViewById<ViewPager2>(R.id.pager)
        viewPager.adapter = TabsAdapter(this)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val tabTitles = arrayOf(
            resources.getString(R.string.weather_upcoming),
            resources.getString(R.string.weather_hottest)
        )
        TabLayoutMediator(tabLayout, viewPager) { myTabLayout: TabLayout.Tab, position: Int ->
            myTabLayout.text = tabTitles[position]
        }.attach()
    }
}
