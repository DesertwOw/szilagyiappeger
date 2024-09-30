package hu.radosdev.szilagyiapp.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.radosdev.szilagyiapp.data.entity.MenuItem
import hu.radosdev.szilagyiapp.data.repositories.MenuRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : ViewModel() {

    // Use MutableLiveData to hold the menu items
    private val _menuItems = MutableLiveData<List<MenuItem>>() // LiveData to observe
    val menuItems: LiveData<List<MenuItem>> get() = _menuItems // Expose LiveData

    fun loadMenuItems() {
        viewModelScope.launch {
            try {
                val jsonData = menuRepository.fetchMenuItems()
                val type = object : TypeToken<List<MenuItem>>() {}.type
                val items: List<MenuItem> = Gson().fromJson(jsonData, type)
                _menuItems.postValue(items) // Update LiveData with the new items
            } catch (e: Exception) {
                // Handle error, optionally post an error state to LiveData
            }
        }
    }
}
