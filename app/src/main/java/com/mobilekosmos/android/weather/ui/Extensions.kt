package com.mobilekosmos.android.weather.ui

import android.widget.ImageView
import coil.load
import coil.request.CachePolicy
import coil.request.Disposable
import coil.request.ImageRequest
import com.mobilekosmos.android.weather.R

/**
 * Helper extension function for ImageViews to load images using the coil library and only load
 * from the local cache.
 */
fun ImageView.loadFromCache(
    data: Any?
): Disposable {

    val noCacheBuilder : ImageRequest.Builder.() -> Unit = {
        this.networkCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .error(R.drawable.no_image)
    }
    return load(data, builder = noCacheBuilder)
}