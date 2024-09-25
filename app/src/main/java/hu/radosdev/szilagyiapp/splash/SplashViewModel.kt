package hu.radosdev.szilagyiapp.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.radosdev.szilagyiapp.data.repositories.MenuRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(private val menuRepository: MenuRepository) : ViewModel() {

    private val _isDataLoaded = MutableLiveData<Boolean>()
    val isDataLoaded : LiveData<Boolean> get()= _isDataLoaded

    init {
       // loadInitialData()
    }

    private fun loadInitialData(){
        viewModelScope.launch {
            delay(1000)
            // TODO: Ide jöhet majd amit be kell várnunk a főoldalon (menü itemek letöltése, főoldali cikekk letöltése stb)
            _isDataLoaded.postValue(true)
        }
    }
}