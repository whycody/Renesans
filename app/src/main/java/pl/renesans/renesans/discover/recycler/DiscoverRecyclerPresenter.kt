package pl.renesans.renesans.discover.recycler

interface DiscoverRecyclerPresenter {

    fun onCreate(articleId: String)

    fun itemClicked(pos: Int)

    fun getItemCount(): Int

    fun onBindViewHolder(holder: DiscoverRowHolder, position: Int)
}