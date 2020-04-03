package pl.renesans.renesans.discover.recycler.fragment

import pl.renesans.renesans.discover.recycler.DiscoverRowHolder


interface DiscoverRecyclerView {

    fun getDiscoverHolderAtPosition(pos: Int): DiscoverRowHolder?
}