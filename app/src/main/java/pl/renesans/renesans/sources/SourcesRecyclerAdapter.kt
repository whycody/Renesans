package pl.renesans.renesans.sources

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R

class SourcesRecyclerAdapter(val context: Context, val presenter: SourcesContract.SourcesPresenter):
    RecyclerView.Adapter<SourcesRowHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourcesRowHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_sources_row, parent, false)
        return SourcesRowHolder(view, context, presenter)
    }

    override fun getItemCount() = presenter.getItemCount()

    override fun onBindViewHolder(holder: SourcesRowHolder, position: Int) =
        presenter.onBindViewHolder(holder, position)

}