package hu.radosdev.szilagyiapp.util

class Constants {
    companion object{

        // Constants All Around The App

        const val URL = "url"
        const val CANCEL = "Mégse"
        const val PACKAGE = "package"
        const val APP_PREFS = "app_prefs"
        const val ERROR_ARG = "error_message"
        const val CONTAINS_URL_STRING = "szilagyi"
        const val MENU_FILE_PATH = "menuItems.json"
        const val GO_TO_SETTINGS = "Beállítások"
        const val FIRST_LAUNCH_FLAG = "is_first_launch"
        const val PERMISSION_TITLE = "Értesítések engedélyezése"
        const val PREFERENCES_NAME = "szilagyi_preferences"
        const val BASE_URL =  "https://www.szilagyi-eger.hu/"
        const val DEFAULT_ERROR_MESSAGE = "Hoppá, valami hiba történt."
        const val NEWS_URL = "https://www.szilagyi-eger.hu/hu/aktualis/hirek"
        const val CHAT_URL = "https://www.szilagyi-eger.hu/hu/aktualis/uzenetek"
        const val URL_JSON_LOAD_ERROR = "Hiba a menü betöltése közben, nézz vissza később."
        const val NO_INTERNET_CONNECTION_DEFAULT_STRING = "Ellenőrizd az internetkapcsolatod."
        const val UPDATE_MESSAGE = "Ahhoz, hogy értesülj a legújjabb cikkekről, engedélyezd az értesítéseket."

        // FCM Related Constants

        const val PREFS_NAME = "FCMTokenPrefs"
        const val PREFS_KEY_TOKEN = "fcm_token"
        const val NEWS_CHANNEL_NAME = "Szilagyi_news"
        const val CHAT_CHANNEL_NAME = "Szilagyi_chat"
        const val NEWS_CHANNEL_DESCRIPTION = "News"
        const val CHAT_CHANNEL_DESCRIPTION = "Chat"
        const val BASE_CHANNEL_ID = "szilagyi_base_channel"
        const val NEWS_CHANNEL_ID = "Szilagyi_news_channel"
        const val CHAT_CHANNEL_ID = "Szilagyi_chat_channel"


        // Number Constants

        const val SPLASH_DELAY = 2000L
        const val ANIMATE_ANGLE = 180f
        const val ANIMATE_DURATION = 300L
        const val SUBMENU_RECYCLER_VIEW_ALPHA = 0f

    }
}