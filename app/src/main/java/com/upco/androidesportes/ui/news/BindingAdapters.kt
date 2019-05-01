package com.upco.androidesportes.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.squareup.picasso.Picasso
import com.upco.androidesportes.R

/**
 * BindingAdapters são funções que extendem a classe a qual se referem, de modo
 * que seja possível passar valores, no código estático, por meio do DataBinding
 * para a classe referida.
 * <p>
 * Por exemplo, definimos um BindingAdapter que possibilita que qualquer
 * ImageView possa ser configurada e tenha sua imagem carrega por meio da url
 * (passada através do atributo app:imageUrl).
 */

/**
 * BindingAdapter que configura um ImageView e carrega sua imagem a partir da url,
 * passada através do atributo app:imageUrl.
 */
@BindingAdapter("bind:imageUrl")
fun ImageView.setImageUrl(url: String) {
    //
    val progress = CircularProgressDrawable(context)
    progress.apply {
        setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }

    Picasso.get()
           .load(url)
           .fit()
           .noFade()
           .centerCrop()
           .placeholder(progress)
           .error(R.drawable.iv_error_placeholder)
           .into(this)
}