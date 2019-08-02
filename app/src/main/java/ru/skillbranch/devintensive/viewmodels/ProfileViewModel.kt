package ru.skillbranch.devintensive.viewmodels

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.repositories.PreferencesRepository

class ProfileViewModel : ViewModel() {
    private val repository: PreferencesRepository = PreferencesRepository
    private val profileData = MutableLiveData<Profile>()
    private val appTheme = MutableLiveData<Int>()

    val repositoryValid: LiveData<Boolean> get() = _repositoryValid
    private val _repositoryValid = MutableLiveData<Boolean>()

    init {
        profileData.value = repository.getProfile()
        appTheme.value = repository.getAppTheme()
    }

    fun getProfileData(): LiveData<Profile> = profileData

    fun saveProfileData(profile: Profile) {
        repository.saveProfile(profile)
        profileData.value = profile
    }

    fun getTheme(): LiveData<Int> = appTheme

    fun switchTheme() {
        if (appTheme.value == AppCompatDelegate.MODE_NIGHT_YES) {
            appTheme.value = AppCompatDelegate.MODE_NIGHT_NO
        } else {
            appTheme.value = AppCompatDelegate.MODE_NIGHT_YES
        }

        repository.saveAppTheme(appTheme.value!!)
    }

    fun validateRepository(repo: String) {
        val exceptionsArray = arrayOf(
            "enterprise", "features", "topics", "collections", "trending", "events", "marketplace", "pricing",
            "nonprofit", "customer-stories", "security", "login", "join"
        )
        val exceptions = exceptionsArray.joinToString("|")
        val regexStr = "^(https:\\/\\/)?(www\\.)?(github\\.com\\/)(?!($exceptions)(?=\\/|\$))[a-zA-Z\\d](?:[a-zA-Z\\d]|-(?=[a-zA-Z\\d])){0,38}(\\/)?$"
        val regex = Regex(regexStr)

        _repositoryValid.value = !(repo.isNotEmpty() && !regex.matches(repo))
    }
}