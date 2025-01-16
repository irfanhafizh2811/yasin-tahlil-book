package com.quran.almulk.extension.glide

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.RequestOptions
import com.quran.almulk.extension.common.extension

/**
 * Created by irfan on 9/7/17.
 */

fun ImageView.loadFromUri(imageUri: Uri?) {
    val options = RequestOptions()
        .dontAnimate()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
    Glide.with(this.context)
        .load(imageUri ?: "")
        .apply(options)
        .into(this)
}

fun ImageView.loadFromUriWithRounded(
    imageUri: Uri?,
    radius: Int,
    @DrawableRes placeholder: Int,
    @DrawableRes errorPlaceholder: Int,
    onSuccessLoad: () -> Unit = {},
    onErrorLoad: () -> Unit = {}
) {
    val options = RequestOptions()
        .dontAnimate()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .priority(Priority.IMMEDIATE)
        .transform(CenterCrop(), RoundedCorners(radius))
    Glide.with(this.context)
        .load(imageUri ?: "")
        .apply(options)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                onErrorLoad.invoke()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                onSuccessLoad.invoke()
                return false
            }

        })
        .placeholder(placeholder)
        .error(errorPlaceholder)
        .into(this)
}

fun ImageView.loadFromUrl(imageUrl: String?) {
    val options = RequestOptions()
        .dontAnimate()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .priority(Priority.IMMEDIATE)
    val isGif = imageUrl?.extension?.contains("GIF", true) ?: false
    when {
        isGif -> {
            Glide.with(this.context)
                .asGif()
                .load(imageUrl ?: "")
                .apply(options)
                .into(this)
        }
        else -> {
            Glide.with(this.context)
                .load(imageUrl ?: "")
                .apply(options)
                .into(this)
        }
    }
}

fun ImageView.loadFromUrlListener(
    imageUrl: String?,
    onSuccessLoad: () -> Unit = {},
    onErrorLoad: () -> Unit = {}
) {
    val options = RequestOptions()
        .dontAnimate()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .priority(Priority.IMMEDIATE)
    val isGif = imageUrl?.extension?.contains("GIF", true) ?: false
    when {
        isGif -> {
            Glide.with(this.context)
                .asGif()
                .load(imageUrl ?: "")
                .apply(options)
                .listener(object : RequestListener<GifDrawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<GifDrawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        onErrorLoad.invoke()
                        return false
                    }

                    override fun onResourceReady(
                        resource: GifDrawable?,
                        model: Any?,
                        target: Target<GifDrawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        onSuccessLoad.invoke()
                        return false
                    }

                })
                .into(this)
        }
        else -> {
            Glide.with(this.context)
                .load(imageUrl ?: "")
                .apply(options)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        onErrorLoad.invoke()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        onSuccessLoad.invoke()
                        return false
                    }

                })
                .into(this)
        }
    }
}

fun ImageView.loadFromResource(@DrawableRes imageRes: Int) {
    val options = RequestOptions()
        .dontAnimate()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .priority(Priority.IMMEDIATE)
    Glide.with(this.context)
        .load(imageRes)
        .apply(options)
        .into(this)
}

fun ImageView.loadFromUrlWithPlaceholder(
    imageUrl: String?,
    @DrawableRes placeholder: Int,
    @DrawableRes errorPlaceholder: Int,
    onSuccessLoad: () -> Unit = {},
    onErrorLoad: () -> Unit = {}
) {
    val options = RequestOptions()
        .dontAnimate()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .priority(Priority.IMMEDIATE)
    val isGif = imageUrl?.extension?.contains("GIF", true) ?: false
    when {
        isGif -> {
            Glide.with(this.context)
                .asGif()
                .load(imageUrl ?: "")
                .apply(options)
                .listener(object : RequestListener<GifDrawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<GifDrawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        onErrorLoad.invoke()
                        return false
                    }

                    override fun onResourceReady(
                        resource: GifDrawable?,
                        model: Any?,
                        target: Target<GifDrawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        onSuccessLoad.invoke()
                        return false
                    }

                })
                .placeholder(placeholder)
                .error(errorPlaceholder)
                .into(this)
        }
        else -> {
            Glide.with(this.context)
                .load(imageUrl ?: "")
                .apply(options)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        onErrorLoad.invoke()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        onSuccessLoad.invoke()
                        return false
                    }

                })
                .placeholder(placeholder)
                .error(errorPlaceholder)
                .into(this)
        }
    }
}

fun ImageView.loadFromUrlWithPlaceholder(
    imageUrl: String?,
    placeholder: Drawable,
    errorPlaceholder: Drawable,
    onSuccessLoad: () -> Unit = {},
    onErrorLoad: () -> Unit = {}
) {
    val options = RequestOptions()
        .dontAnimate()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .priority(Priority.IMMEDIATE)
    val isGif = imageUrl?.extension?.contains("GIF", true) ?: false
    when {
        isGif -> Glide.with(this.context)
            .asGif()
            .load(imageUrl ?: "")
            .apply(options)
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onErrorLoad.invoke()
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onSuccessLoad.invoke()
                    return false
                }

            })
            .placeholder(placeholder)
            .error(errorPlaceholder)
            .into(this)
        else -> Glide.with(this.context)
            .load(imageUrl ?: "")
            .apply(options)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onErrorLoad.invoke()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onSuccessLoad.invoke()
                    return false
                }

            })
            .placeholder(placeholder)
            .error(errorPlaceholder)
            .into(this)
    }
}

fun ImageView.loadFromUrlWithRounded(
    imageUrl: String?,
    radius: Int,
    @DrawableRes placeholder: Int,
    @DrawableRes errorPlaceholder: Int,
    onSuccessLoad: () -> Unit = {},
    onErrorLoad: () -> Unit = {}
) {
    val options = RequestOptions()
        .dontAnimate()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .priority(Priority.IMMEDIATE)
        .transform(CenterCrop(), RoundedCorners(radius))
    val isGif = imageUrl?.extension?.contains("GIF", true) ?: false
    when {
        isGif -> Glide.with(this.context)
            .asGif()
            .load(imageUrl ?: "")
            .apply(options)
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onErrorLoad.invoke()
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onSuccessLoad.invoke()
                    return false
                }

            })
            .placeholder(placeholder)
            .error(errorPlaceholder)
            .into(this)
        else -> Glide.with(this.context)
            .load(imageUrl ?: "")
            .apply(options)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onErrorLoad.invoke()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onSuccessLoad.invoke()
                    return false
                }

            })
            .placeholder(placeholder)
            .error(errorPlaceholder)
            .into(this)
    }
}
