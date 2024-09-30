package hu.radosdev.szilagyiapp.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.radosdev.szilagyiapp.data.repositories.MenuRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import hu.radosdev.szilagyiapp.data.entity.Menu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : ViewModel() {

    private val _menu = MutableStateFlow<Menu?>(null)
    val menu: StateFlow<Menu?> get() = _menu

    fun fetchMenu() {
        viewModelScope.launch {
            val menuData = menuRepository.fetchMenu()
            _menu.value = menuData
        }
    }
}
