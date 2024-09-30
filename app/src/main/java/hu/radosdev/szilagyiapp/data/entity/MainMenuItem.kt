package hu.radosdev.szilagyiapp.data.entity

data class MainMenuItem(
    val title: String,
    val childs: List<ChildMenuItem>?
)