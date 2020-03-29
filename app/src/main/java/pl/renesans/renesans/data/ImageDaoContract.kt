package pl.renesans.renesans.data

import pl.renesans.renesans.discover.recycler.DiscoverRowHolder

interface ImageDaoContract {

    interface ImageDao {

        fun loadPhotoToHolder(holder: DiscoverRowHolder?, id: String, highQuality: Boolean = true)
    }
}