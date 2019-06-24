package com.upco.androidesportes.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.upco.androidesportes.R
import com.upco.androidesportes.model.NetworkState
import com.upco.androidesportes.model.Status
import kotlinx.android.synthetic.main.list_item_network_state.view.*

/**
 * ViewHolder para a classe [NetworkState].
 *
 * @param view
 * @param retryCallback Callback que será invocado ao clicar na view, em caso de erro.
 */
class NetworkStateViewHolder(view: View, retryCallback: () -> Unit):
        RecyclerView.ViewHolder(view) {

    /* ProgressBar que será exibido enquanto houver alguma requisição sendo feita */
    private val pbProgress: ProgressBar = view.pb_progress

    /*
     * Layout com o aviso de que ocorreu algum erro, é por meio dele que podemos
     * também clicar para tentar novamente
     */
    private val clNetworkState: ConstraintLayout = view.cl_network_state

    /* Executado na inicialização da classe */
    init {
        /*
         * Cria um drawable que será utilizado no ProgressBar, dessa forma conseguimos
         * personalizar a largura do anel (strokeWidth), o raio do centro (centerRadius), etc.
         */
        val progressDrawable = CircularProgressDrawable(view.context)
        progressDrawable.apply {
            val resources = view.resources
            setColorSchemeColors(ContextCompat.getColor(view.context, R.color.colorPrimary))
            strokeWidth = resources.getDimension(R.dimen.news_progress_stroke_width)
            centerRadius = resources.getDimension(R.dimen.news_progress_center_radius)
            start()
        }

        /* Define o drawable criado acima como o drawable do ProgressBar */
        pbProgress.indeterminateDrawable = progressDrawable

        /* Define o que deve ser feito ao clicar na view, nesse caso, invoca o callback */
        clNetworkState.setOnClickListener {
            retryCallback()
        }
    }

    /**
     * Faz binding do estado [networkState] com o layout.
     *
     * @param networkState Estado da requisição atual.
     */
    fun bind(networkState: NetworkState) {
        /* Define o que deve estar visivel ou não de acordo com o estado */
        pbProgress.visibility = toVisibility(networkState.status == Status.RUNNING)
        clNetworkState.visibility = toVisibility(networkState.status == Status.FAILED)
    }

    /**
     * Retorna um valor de visibilidade de acordo com o parâmetro [constraint].
     *
     * @param constraint Indica se a View deve estar visivel ou não.
     * @return Se [constraint] for true, retorna [View.VISIBLE]. Caso constrário,
     * retorna [View.GONE].
     */
    private fun toVisibility(constraint: Boolean) =
            if (constraint) View.VISIBLE else View.GONE

    companion object {
        /**
         * Cria e retorna uma instância de [NetworkStateViewHolder].
         *
         * @param parent        ViewGroup utilizado para inflar a view.
         * @param retryCallback Callback que será invocado ao clicar na view, em caso de erro.
         * @return Instância de [NetworkStateViewHolder]
         */
        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.list_item_network_state, parent, false)
            return NetworkStateViewHolder(view, retryCallback)
        }
    }
}