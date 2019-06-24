package com.upco.androidesportes.ui.news

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upco.androidesportes.R
import com.upco.androidesportes.model.NetworkState
import com.upco.androidesportes.model.News

/**
 * Adapter para a lista de notícias.
 *
 * @param activity      Activity a qual esse adapter pertence.
 * @param retryCallback Callback que será invocado ao clicar na view, em caso de erro.
 */
class NewsAdapter(
    private val activity: AppCompatActivity,
    private val retryCallback: () -> Unit
): PagedListAdapter<News, RecyclerView.ViewHolder>(NEWS_COMPARATOR) {

    /* Mantém o estado da requisição atual */
    private var networkState: NetworkState? = null

    /**
     * Chamado quando o RecyclerView necessita de um novo [RecyclerView.ViewHolder]
     * de um dado tipo para representar um item.
     *
     * @param parent   O ViewGroup ao qual a nova View será adicionada.
     * @param viewType O tipo da nova View.
     * @throws IllegalArgumentException
     */
    @Throws(IllegalArgumentException::class)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        /* Cria o ViewHolder de acordo com o tipo da view */
        return when (viewType) {
            R.layout.list_item_news -> NewsViewHolder.create(activity, parent)
            R.layout.list_item_network_state -> {
                NetworkStateViewHolder.create(parent, retryCallback)
            }
            else -> throw IllegalArgumentException("View do tipo $viewType desconhecida")
        }
    }

    /**
     * Chamado pelo RecyclerView para exibir os dados na posição específica.
     *
     * @param holder   O ViewHolder que deve ser atualizado para representar o
     *                 conteúdo do item na dada posição.
     * @param position A posição do item.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        /* Faz binding com o ViewHolder de acordo com o tipo da view */
        when (getItemViewType(position)) {
            R.layout.list_item_news -> getItem(position)?.let {
                (holder as NewsViewHolder).bind(it)
            }
            R.layout.list_item_network_state -> networkState?.let {
                (holder as NetworkStateViewHolder).bind(it)
            }
        }
    }

    /**
     * Retorna o tipo da view para o item na posição [position].
     *
     * @param position Posição do item.
     * @return [Int] identificando o tipo da view necessária para representar
     * o item na posição [position].
     */
    override fun getItemViewType(position: Int): Int {
        /* Verifica se o adapter deve inflar a view extra ou não */
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.list_item_network_state
        } else {
            R.layout.list_item_news
        }
    }

    /**
     * Indica ao adapter qual o estado da requisição, assim ele pode exibir a view de acordo.
     *
     * @param newNetworkState Estado da requisição.
     */
    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = networkState
        val hadExtraRow = hasExtraRow()

        networkState = newNetworkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(itemCount)
            } else {
                notifyItemInserted(itemCount)
            }
        } else if (hasExtraRow && previousState != networkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    /**
     * Indica se o adapter deve inflar uma view extra, para exibição da ProgressBar ou da
     * view de erro.
     *
     * @return true se o estado da requisição atual for diferente de [NetworkState.LOADED],
     * caso contrário, false.
     */
    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    companion object {
        private val NEWS_COMPARATOR = object: DiffUtil.ItemCallback<News>() {

            override fun areItemsTheSame(oldItem: News, newItem: News) =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: News, newItem: News) =
                    oldItem == newItem
        }
    }
}