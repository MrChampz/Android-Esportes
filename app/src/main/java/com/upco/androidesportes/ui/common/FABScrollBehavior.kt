package com.upco.androidesportes.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Behavior associado ao FAB da NewsActivity e WebViewActivity.
 * Faz com que o FAB seja oculto ao rolar o feed para baixo, e seja re-exibido ao rolar para cima.
 */
class FABScrollBehavior(context: Context, attrs: AttributeSet):
        FloatingActionButton.Behavior(context, attrs) {

    /**
     * Chamado quando uma view descendente do [CoordinatorLayout] tenta iniciar uma rolagem.
     *
     * Qualquer Behavior associado a qualquer filho direto do [CoordinatorLayout] pode
     * responder a esse evento e retornar true, indicando que o [CoordinatorLayout] deve
     * agir como um parente nessa rolagem. Apenas Behaviors que retornem true, a partir
     * desse método, receberão os eventos de rolagem subsequentes.
     */
    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        /*
         * Verifica se a rolagem é vertical e caso seja, retorna true indicando que a rolagem
         * será tratada por esse Behavior. Caso contrário, deixa a classe pai trata-la.
         */
        return if (axes == ViewCompat.SCROLL_AXIS_VERTICAL) {
            true
        } else {
            super.onStartNestedScroll(
                coordinatorLayout,
                child,
                directTargetChild,
                target,
                axes,
                type
            )
        }
    }

    /**
     * Chamado toda vez que uma rolagem, em progresso, é atualizada.
     */
    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type
        )

        /*
         * Se a rolagem do RecyclerView consumir mais do que 0 pixels verticais,
         * indica que é uma rolagem no sentido para baixo. Se consumir menos do
         * que 0 pixels, é uma rolagem no sentido para cima.
         * Caso a rolagem seja para baixo, o FAB é ocultado e volta a ser
         * exibido quando a rolagem for para cima.
         */
        if (dyConsumed > 0 && child.visibility == View.VISIBLE) {
            /* Oculta o FAB, utilizando sua animação */
            child.hide(object: FloatingActionButton.OnVisibilityChangedListener() {

                @SuppressLint("RestrictedApi")
                override fun onHidden(fab: FloatingActionButton) {
                    super.onHidden(fab)
                    fab.visibility = View.INVISIBLE
                }
            })
        } else if (dyConsumed < 0 && child.visibility != View.VISIBLE) {
            /* Exibe o FAB, utilizando sua animação */
            child.show()
        }
    }
}