package hu.radosdev.szilagyiapp.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.radosdev.szilagyiapp.data.entity.MainMenuItem
import hu.radosdev.szilagyiapp.data.repositories.MenuRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: MenuRepository
) : ViewModel() {

    private val _menuItems = MutableLiveData<List<MainMenuItem>>()
    val menuItems: LiveData<List<MainMenuItem>> get() = _menuItems

    fun loadMenuItems(){
        viewModelScope.launch {
            val menu = repository.fetchMenu()
            _menuItems.value = menu?.mainMenu
        }
    }
}
