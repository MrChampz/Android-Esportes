package com.upco.androidesportes.ui.news

import android.os.Build
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
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
 * (passada através do atributo imageUrl).
 */

/**
 * BindingAdapter que configura um ImageView e carrega sua imagem a partir da url,
 * passada através do atributo imageUrl.
 */
@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(url: String?) {
    /*
     * Cria um drawable que será utilizado como placeholder na ImageView, enquanto a
     * imagem estiver sendo carregada, pelo Picasso, o drawable ficará visível na View
     */
    val progressDrawable = CircularProgressDrawable(context)
    progressDrawable.apply {
        setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }

    /*
     * Armazena o id do drawable de erro numa variável,
     * desta forma a manutenção do código se mantém simples.
     */
    val errorDrawable = R.drawable.iv_error_placeholder

    /*
     * Carrega a imagem no ImageView, a partir da url.
     * O progressDrawable criado é utilizado como placeholder, enquanto a imagem é carregada.
     * Em caso de erro, errorDrawable é utilizado como drawable na View.
     */
    if (url != null) {
        Picasso.get()
                .load(url)
                .fit()
                .noFade()
                .centerCrop()
                .placeholder(progressDrawable)
                .error(errorDrawable)
                .into(this)
    }
}