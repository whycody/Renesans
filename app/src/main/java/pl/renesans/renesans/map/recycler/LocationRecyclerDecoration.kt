package pl.renesans.renesans.map.recycler

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LocationRecyclerDecoration(val context: Context): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        val params = view.layoutParams as RecyclerView.LayoutParams
        if (parent.getChildAdapterPosition(view) == parent.adapter?.itemCount?.minus(1))
            params.marginEnd = getMarginInDp(15f)
        else params.marginEnd = 0
        super.getItemOffsets(outRect, view, parent, state)
    }

    private fun getMarginInDp(dp: Float): Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
            context.resources.displayMetrics).toInt()
    }
}