package net.cattaka.android.learnscopedstorage.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

object ImageLoader {
    @JvmStatic
    @BindingAdapter("loadImage")
    fun ImageView.loadImage(uri: String?) {
        Picasso.get()
                .load(uri)
                .into(this)
    }
}
