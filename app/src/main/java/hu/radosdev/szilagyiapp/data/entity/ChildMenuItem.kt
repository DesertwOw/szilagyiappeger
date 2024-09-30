package hu.radosdev.szilagyiapp.data.entity

data class ChildMenuItem(
    val title: String,
    val url: String,
    val childs: List<ChildMenuItem>?
)