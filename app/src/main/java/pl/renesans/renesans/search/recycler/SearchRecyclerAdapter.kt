package pl.renesans.renesans.search.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import pl.renesans.renesans.R
import pl.renesans.renesans.data.ArticleItem

class SearchRecyclerAdapter(private val presenter: SearchContract.SearchPresenter):
    RecyclerView.Adapter<SearchRowHolder>(), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRowHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_search_row, parent, false)
        return SearchRowHolder(view, parent.context, presenter)
    }

    override fun getItemCount() = presenter.getItemCount()

    override fun onBindViewHolder(holder: SearchRowHolder, position: Int) =
        presenter.onBindViewHolder(holder, position)

    override fun getFilter() = articlesFilter

    private val articlesFilter = object : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val filteredList = mutableListOf<ArticleItem>()
            if (p0 != null && p0.isNotEmpty())  fillFilteredList(p0, filteredList)
            else filteredList.addAll(presenter.getSearchedArticles())
            return getFilterResults(filteredList)
        }

        private fun getFilterResults(filteredList: MutableList<ArticleItem>): FilterResults {
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

    private fun fillFilteredList(p0: CharSequence?, filteredList: MutableList<ArticleItem>) {
        val pattern = p0.toString().toLowerCase().trim()
        presenter.getAllArticles().forEach {
            checkItemMatchesPattern(it, pattern, filteredList)
        }
    }

    private fun checkItemMatchesPattern(item: ArticleItem, pattern: String,
                                        filteredList: MutableList<ArticleItem>) {
        if (itemContainsPattern(item, pattern))
            filteredList.add(item)
    }

    private fun itemContainsPattern(item: ArticleItem, pattern: String) =
        item.title!!.toLowerCase().contains(pattern)
}