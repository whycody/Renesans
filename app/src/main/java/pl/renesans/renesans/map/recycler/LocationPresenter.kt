package pl.renesans.renesans.map.recycler

interface LocationPresenter {

    fun onCreate()

    fun itemClicked(pos: Int)

    fun getItemCount(): Int

    fun onBindViewHolder(holder: LocationRowHolder, position: Int)

}