package pl.renesans.renesans.article.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R

class RelatedAdapter(val context: Context, val presenter: RelatedPresenterImpl):
    RecyclerView.Adapter<RelatedRowHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatedRowHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_related_row, parent, false)
        return RelatedRowHolder(view, context, presenter)
    }

    override fun getItemCount(): Int {
        return presenter.getItemCount()
    }

    override fun onBindViewHolder(holder: RelatedRowHolder, position: Int) {
        presenter.onBindViewHolder(holder, position)
    }
}