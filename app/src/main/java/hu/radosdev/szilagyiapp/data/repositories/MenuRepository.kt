package hu.radosdev.szilagyiapp.data.repositories

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MenuRepository  @Inject constructor(
    private val firebaseStorage: FirebaseStorage
) {
    suspend fun fetchMenuItems(): String {
        val storageRef = firebaseStorage.getReference("menuItems.json")
        return storageRef.getBytes(Long.MAX_VALUE).await().toString(Charsets.UTF_8)
    }
}