package pl.renesans.renesans.settings

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import pl.renesans.renesans.BuildConfig
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Setting

class SettingsPresenterImpl(val context: Context, val settingsView: SettingsContract.SettingsView)
    : SettingsContract.SettingsPresenter {

    private val holders: MutableList<SettingsRowHolder> = mutableListOf()
    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
    private val editor = sharedPrefs.edit()
    private val settingsList = getSettings()

    private fun getSettings(): List<Setting>{
        val settingsList = mutableListOf<Setting>()
        settingsList.add(Setting(ERA, context.getString(R.string.era),
            context.getString(R.string.renesans), false))
        settingsList.add(Setting(DOWNLOAD_PHOTOS, context.getString(R.string.download_photos),
            context.getString(R.string.download_photos_desc), true,
            sharedPrefs.getBoolean(DOWNLOAD_PHOTOS, true)))
        settingsList.add(Setting(MAP_MODE, context.getString(R.string.map_mode),
            context.getString(R.string.map_mode_desc), true,
            sharedPrefs.getBoolean(MAP_MODE, false)))
        settingsList.add(Setting(MAP_ANIMATIONS, context.getString(R.string.map_animations), 
            context.getString(R.string.map_animations_desc),
            true, sharedPrefs.getBoolean(MAP_ANIMATIONS, false)))
        settingsList.add(Setting(MAP_OPACITY, context.getString(R.string.tour_view),
            context.getString(R.string.tour_view_desc),
            true, sharedPrefs.getBoolean(MAP_OPACITY, true)))
        settingsList.add(Setting(APP_VERSION,
            context.getString(R.string.app_version), BuildConfig.VERSION_NAME, false))
        return settingsList
    }

    override fun itemClicked(pos: Int, checkBoxValue: Boolean) {
        if(settingsList[pos].booleanValue){
            editor.putBoolean(settingsList[pos].settingId!!, checkBoxValue)
            editor.apply()
            if(settingsList[pos].settingId == MAP_MODE)
                settingsView.refreshMapFragment()
        }
    }

    override fun getItemCount(): Int {
        return settingsList.size
    }

    override fun onBindViewHolder(holder: SettingsRowHolder, position: Int) {
        resetVariables(holder)
        refreshHoldersList(holder, position)
        holder.setSettingTitle(settingsList[position].settingTitle!!)
        if(settingsList[position].settingDescribe!=null)
            holder.setSettingDescribe(settingsList[position].settingDescribe!!)
        if(settingsList[position].booleanValue)
            holder.setCheckBoxChecked(settingsList[position].defaultValue)
        else holder.setCheckBoxVisibility(View.INVISIBLE)
        holder.setOnRowClickListener(position)
        if(position == settingsList.size-1) holder.setUnderlineVisibility(View.INVISIBLE)
        else holder.setUnderlineVisibility(View.VISIBLE)
    }

    private fun refreshHoldersList(holder: SettingsRowHolder, position: Int){
        if(holders.size-1<position || holders.isEmpty()) holders.add(position, holder)
        else holders[position] = holder
    }

    private fun resetVariables(holder: SettingsRowHolder){
        holder.setSettingTitle(" ")
        holder.setSettingDescribe(" ")
        holder.setOnRowClickListener(0)
    }

    companion object{
        val ERA = "era"
        val DOWNLOAD_PHOTOS = "download photos"
        val MAP_MODE = "map mode"
        val MAP_ANIMATIONS = "map animations"
        val MAP_OPACITY = "map opacity"
        val APP_VERSION = "app version"
    }
}