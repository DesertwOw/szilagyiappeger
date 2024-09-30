package hu.radosdev.szilagyiapp.data.repositories

import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import hu.radosdev.szilagyiapp.data.entity.Menu
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MenuRepository  @Inject constructor(
    private val firebaseStorage: FirebaseStorage, private val gson: Gson
) {
    suspend fun fetchMenu(): Menu? {

        return try{
            val storageRef = firebaseStorage.getReferenceFromUrl("xd")
            val data = storageRef.getBytes(Long.MAX_VALUE).await()
            val json = String(data)
            gson.fromJson(json, Menu::class.java)
        } catch (e: Exception) {
            null // Handle errors
        }
    }
}