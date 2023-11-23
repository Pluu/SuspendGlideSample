package com.pluu.suspendglidesample.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.pluu.suspendglidesample.R
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun ImageView.awaitLoad(url: String) = suspendCoroutine { cont ->
    Glide.with(this)
        .load(url)
        .circleCrop()
        .placeholder(R.drawable.placeholder)
        .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                cont.resume(Unit)
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                cont.resume(Unit)
                return false
            }
        })
        .into(this)
}
