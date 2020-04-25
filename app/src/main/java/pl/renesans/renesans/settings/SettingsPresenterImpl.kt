package pl.renesans.renesans.settings

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import pl.renesans.renesans.data.Setting

class SettingsPresenterImpl(val context: Context): SettingsContract.SettingsPresenter {

    private val holders: MutableList<SettingsRowHolder> = mutableListOf()
    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
    private val editor = sharedPrefs.edit()
    private val settingsList = getSettings()

    private fun getSettings(): List<Setting>{
        val settingsList = mutableListOf<Setting>()
        settingsList.add(Setting(ERA, "Epoka", "Renesans", false))
        settingsList.add(Setting(DOWNLOADING_PHOTOS, "Pobieranie zdjęć", "Pobieraj zdjęcia niskiej jakości aby później móc korzystać z " +
                "aplikacji w trybie offline", true, sharedPrefs.getBoolean(DOWNLOADING_PHOTOS, true)))
        settingsList.add(Setting(MAP_MODE, "Tryb mapy", "Pokazuj tylko te budowle, które zostały zbudowane w wybranej " +
                "epoce", true, sharedPrefs.getBoolean(MAP_MODE, false)))
        settingsList.add(Setting(MAP_ANIMATIONS, "Animacje mapy", "Włącz animacje podczas wybierania miejsc na mapie",
                true, sharedPrefs.getBoolean(MAP_ANIMATIONS, false)))
        settingsList.add(Setting(APP_VERSION, "Wersja aplikacji", "1.0", false))
        return settingsList
    }

    override fun itemClicked(pos: Int, checkBoxValue: Boolean) {
        if(settingsList[pos].booleanValue){
            editor.putBoolean(settingsList[pos].settingId!!, checkBoxValue)
            editor.apply()
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
        val DOWNLOADING_PHOTOS = "downloading photos"
        val MAP_MODE = "map mode"
        val MAP_ANIMATIONS = "map animations"
        val APP_VERSION = "app version"
    }
}