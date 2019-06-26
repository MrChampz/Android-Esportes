package com.upco.androidesportes.ui.common

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewTreeObserver
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Exibe o FAB, com a animação padrão.
 * Essa função é usada para contornar um bug em que a animação do FAB não funciona
 * de primeira, quando a activity é criada.
 * A função espera certo tempo ([delay]) antes de exibir o FAB, desta forma é
 * possível que ele seja inserido no layout antes da animação. Isso faz com que
 * o efeito de "pop" que ocorre (bug) seja evitado.
 * <p/>
 * Estende a classe [FloatingActionButton].
 *
 * @param delay Tempo a ser esperado antes de exibir o FAB. O padrão é 200ms.
 */
@SuppressLint("RestrictedApi")
fun FloatingActionButton.showWithAnimation(delay: Long = 200) {
    /* Define o estado inicial para a animação */
    visibility = View.INVISIBLE
    scaleX = 0f
    scaleY = 0f
    alpha = 0f

    /*
     * Define um callback que será chamado sempre que a view tree estiver prestes a ser renderizada
     */
    viewTreeObserver.addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener {

        /**
         * Método callback chamado quando a view tree está prestes a ser renderizada.
         * Nesse ponto, todas as views na view tree já foram medidas e receberam um frame.
         * Os clientes podem usar esse método para ajustar seus limites de rolagem ou até
         * mesmo para solicitar um novo layout antes que a renderização aconteça.
         *
         * @return Retorne true para proceder com a renderização, ou false para cancelar.
         */
        override fun onPreDraw(): Boolean {
            /* Assim que esse método for invocado o listener deverá ser removido da view tree */
            viewTreeObserver.removeOnPreDrawListener(this)

            /* Espera o tempo definido em delay, e então exibe o FAB */
            postDelayed({
                show()
            }, delay)

            /* Retorna true para que a renderização continue */
            return true
        }
    })
}