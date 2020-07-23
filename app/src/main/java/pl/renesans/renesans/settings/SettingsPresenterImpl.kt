package pl.renesans.renesans.settings

import android.Manifest
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import pl.renesans.renesans.BuildConfig
import pl.renesans.renesans.MainActivity
import pl.renesans.renesans.R
import pl.renesans.renesans.data.Setting
import pl.renesans.renesans.data.SettingListItem
import pl.renesans.renesans.settings.dialog.SettingsDialogFragment
import pl.renesans.renesans.settings.dialog.SettingsListContract

class SettingsPresenterImpl(private val activity: MainActivity,
                            private val settingsView: SettingsContract.SettingsView)
    : SettingsContract.SettingsPresenter {

    private val holders: MutableList<SettingsRowHolder> = mutableListOf()
    private val sharedPrefs: SharedPreferences =
        activity.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
    private val editor = sharedPrefs.edit()
    private val settingsList = getSettings()
    private var currentMapMode = sharedPrefs.getInt(MAP_MODE, 0)
    private var permissionGranted = false

    private fun getSettings(): List<Setting>{
        val settingsList = mutableListOf<Setting>()
        settingsList.add(Setting(ERA, activity.getString(R.string.era),
            activity.getString(R.string.renesans), false))
        settingsList.add(Setting(DOWNLOAD_PHOTOS, activity.getString(R.string.download_photos),
            activity.getString(R.string.download_photos_desc), true,
            getValueOfDownloadingPhotos()))
        settingsList.add(Setting(MAP_MODE, activity.getString(R.string.map_mode),
            getMapModeDescription(), booleanValue = false, defaultValue = false,
            listOfOptions = listOf(SettingListItem(ALL_BUILDINGS,
                activity.getString(R.string.all_buildings), getMapModeDescription(ALL_BUILDINGS)),
                SettingListItem(ALL_TO_CHOOSED_ERA, activity.getString(R.string.all_to_choosed_era),
                    getMapModeDescription(ALL_TO_CHOOSED_ERA)),
                SettingListItem(ERA_BUILDINGS, activity.getString(R.string.era_buildings),
                    getMapModeDescription(ERA_BUILDINGS))),
            defaultSettingsItemPos = sharedPrefs.getInt(MAP_MODE, 0)))
        settingsList.add(Setting(MAP_FUNCTIONALITIES, activity.getString(R.string.map_functionalities),
            activity.getString(R.string.map_functionalities_desc),
            true, sharedPrefs.getBoolean(MAP_FUNCTIONALITIES, !freeRamMemoryIsEnough())))
        settingsList.add(Setting(MAP_ANIMATIONS, activity.getString(R.string.map_animations),
            activity.getString(R.string.map_animations_desc),
            true, sharedPrefs.getBoolean(MAP_ANIMATIONS, false)))
        settingsList.add(Setting(MAP_OPACITY, activity.getString(R.string.tour_view),
            activity.getString(R.string.tour_view_desc),
            true, sharedPrefs.getBoolean(MAP_OPACITY, true)))
        settingsList.add(Setting(APP_VERSION,
            activity.getString(R.string.app_version), BuildConfig.VERSION_NAME, false))
        return settingsList
    }

    private fun getValueOfDownloadingPhotos(): Boolean {
        permissionGranted = (ContextCompat.checkSelfPermission(activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        return if(!permissionGranted) false
        else sharedPrefs.getBoolean(DOWNLOAD_PHOTOS, permissionGranted)
    }

    private fun getMapModeDescription(index: Int = sharedPrefs.getInt(MAP_MODE, 0)): String{
        return when (index) {
            ALL_BUILDINGS -> activity.getString(R.string.all_buildings_desc)
            ALL_TO_CHOOSED_ERA -> activity.getString(R.string.all_to_choosed_era_desc)
            else -> activity.getString(R.string.era_buildings_desc)
        }
    }

    private fun freeRamMemoryIsEnough(): Boolean {
        val mi = ActivityManager.MemoryInfo()
        val activityManager = activity.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        return (mi.availMem / 1048576L) >= 400
    }

    override fun itemClicked(pos: Int, checkBoxValue: Boolean) {
        if(settingsList[pos].booleanValue && settingsList[pos].settingId != DOWNLOAD_PHOTOS){
            editor.putBoolean(settingsList[pos].settingId!!, checkBoxValue)
            editor.apply()
        }else if(settingsList[pos].listOfOptions != null) showMapModeDialog(settingsList[pos], pos)
        else if(settingsList[pos].settingId == DOWNLOAD_PHOTOS) checkPermissionGranted(pos, checkBoxValue)
        if(settingsList[pos].settingId == MAP_MODE) settingsView.refreshMapFragment()
        if(settingsList[pos].settingId == MAP_FUNCTIONALITIES) settingsView.changedOptionOfMapLimit()
    }

    private fun checkPermissionGranted(pos: Int, checkBoxValue: Boolean){
        permissionGranted = (ContextCompat.checkSelfPermission(activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        if(!permissionGranted && checkBoxValue){
            settingsList[pos].defaultValue = false
            settingsView.notifyItemChangedAtPosition(pos)
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }else{
            editor.putBoolean(settingsList[pos].settingId!!, checkBoxValue)
            editor.apply()
        }
    }

    private fun showMapModeDialog(setting: Setting, settingPos: Int){
        currentMapMode = setting.defaultSettingsItemPos
        val dialog = SettingsDialogFragment(object : SettingsListContract.SettingsListView{
            override fun getSettingItemsList(): List<SettingListItem> {
                return setting.listOfOptions!!
            }

            override fun getDefaultSettingsItemPos(): Int {
                return setting.defaultSettingsItemPos
            }

            override fun radioBtnChoosed(mapModePos: Int) {
                refreshMapModeSetting(mapModePos, settingPos)
                if(mapModePos == ERA_BUILDINGS) showWarningDialog(settingPos)
            }
        })
        dialog.show(activity.supportFragmentManager, "MapMode")
    }

    private fun showWarningDialog(settingsPos: Int){
        val dialog = AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.warning))
            .setMessage(activity.getString(R.string.warning_desc))
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                refreshMapModeSetting(currentMapMode, settingsPos)
            }.create()

        dialog.setOnShowListener{
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
        }

        dialog.show()
    }

    private fun refreshMapModeSetting(mapModePos: Int, settingsPos: Int){
        val setting = settingsList[settingsPos]
        editor.putInt(MAP_MODE, mapModePos)
        editor.apply()
        setting.settingDescription = getMapModeDescription(mapModePos)
        setting.defaultSettingsItemPos = mapModePos
        settingsView.notifyItemChangedAtPosition(settingsPos)
    }

    override fun getItemCount(): Int {
        return settingsList.size
    }

    override fun onBindViewHolder(holder: SettingsRowHolder, position: Int) {
        resetVariables(holder)
        refreshHoldersList(holder, position)
        holder.setSettingTitle(settingsList[position].settingTitle!!)
        if(settingsList[position].settingDescription!=null)
            holder.setSettingDescribe(settingsList[position].settingDescription!!)
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
        const val ERA = "era"
        const val DOWNLOAD_PHOTOS = "download photos"
        const val MAP_FUNCTIONALITIES = "map functionalities"
        const val MAP_MODE = "map mode"
        const val MAP_ANIMATIONS = "map animations"
        const val MAP_OPACITY = "map opacity"
        const val APP_VERSION = "app version"

        const val ALL_BUILDINGS = 0
        const val ALL_TO_CHOOSED_ERA = 1
        const val ERA_BUILDINGS = 2
    }
}