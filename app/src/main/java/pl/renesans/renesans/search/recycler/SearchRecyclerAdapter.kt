package pl.renesans.renesans.search.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R
import pl.renesans.renesans.data.ArticleItem

class SearchRecyclerAdapter(val context: Context, val presenter: SearchContract.SearchPresenter):
    RecyclerView.Adapter<SearchRowHolder>(), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRowHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_search_row, parent, false)
        return SearchRowHolder(view, context, presenter)
    }

    override fun getItemCount(): Int {
        return presenter.getItemCount()
    }

    override fun onBindViewHolder(holder: SearchRowHolder, position: Int) {
        presenter.onBindViewHolder(holder, position)
    }

    override fun getFilter(): Filter {
        return articlesFilter
    }

    private val articlesFilter = object : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val filteredList = mutableListOf<ArticleItem>()
            if (p0 != null && p0.isNotEmpty()) {
                val pattern = p0.toString().toLowerCase().trim()
                for (item in presenter.getAllArticles())
                    if (item.title!!.toLowerCase().contains(pattern))
                        filteredList.add(item)
            } else filteredList.addAll(presenter.getSearchedArticles())
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            if(p1!!.values != presenter.getCurrentArticlesList() && p1.values!=null) {
                presenter.setCurrentArticlesList(p1.values as List<ArticleItem>)
                notifyDataSetChanged()
            }
        }
    }
}