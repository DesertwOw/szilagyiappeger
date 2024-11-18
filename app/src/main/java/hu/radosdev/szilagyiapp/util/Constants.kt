package hu.radosdev.szilagyiapp.util

class Constants {
    companion object{

        // Constants All Around The App

        const val URL = "url"
        const val TITLE = "title"
        const val CANCEL = "Mégse"
        const val PACKAGE = "package"
        const val MAIN_PAGE = "FŐOLDAL"
        const val APP_PREFS = "app_prefs"
        const val SUPPORTERS = "TÁMOGATÓINK"
        const val ERROR_ARG = "error_message"
        const val FACEBOOK_URL = "facebook.com"
        const val CONTAINS_URL_STRING = "szilagyi"
        const val MENU_FILE_PATH = "menuItems.json"
        const val GO_TO_SETTINGS = "Beállítások"
        const val FIRST_LAUNCH_FLAG = "is_first_launch"
        const val FACEBOOK_PACKAGE_NAME = "com.facebook.katana"
        const val INVALID_URL = "Nem létező vagy hibás webcím."
        const val PERMISSION_TITLE = "Értesítések engedélyezése"
        const val PREFERENCES_NAME = "szilagyi_preferences"
        const val BASE_URL =  "https://www.szilagyi-eger.hu/"
        const val DEFAULT_ERROR_MESSAGE = "Hoppá, valami hiba történt."
        const val WEB_VIEW_LOAD_FAILED = "A weboldal betöltése sikertelen!"
        const val CHAT_URL = "https://www.szilagyi-eger.hu/hu/aktualis/uzenetek"
        const val URL_JSON_LOAD_ERROR = "Hiba a menü betöltése közben, nézz vissza később."
        const val NO_INTERNET_CONNECTION_DEFAULT_STRING = "Ellenőrizd az internetkapcsolatod."
        const val UPDATE_MESSAGE = "Ahhoz, hogy értesülj a legújjabb cikkekről, engedélyezd az értesítéseket."

        // FCM Related Constants

        const val CHAT_CHANNEL_NAME = "Szilagyi_chat"
        const val CHAT_CHANNEL_DESCRIPTION = "Chat"
        const val CHAT_CHANNEL_ID = "Szilagyi_chat_channel"


        // Number Constants

        const val SPLASH_DELAY = 2000L
        const val ANIMATE_ANGLE = 180f
        const val ANIMATE_DURATION = 300L
        const val SUBMENU_RECYCLER_VIEW_ALPHA = 0f

        // Supporters Constants

        const val MARSHALL = "Marshall Ablak Kft."
        const val MARSHALL_WEB = "https://www.marshallablak.hu/"
        const val AGRIA = "Agria Informatika Kft."
        const val AGRIA_WEB = "https://agriainfo.hu"
        const val EV = "Cserháti Gabriella e.v."

        // Animation Constants
        const val SCALE_X = "scaleX"
        const val SCALE_Y = "scaleY"
        const val VALUE_ONETWO = 1.2f
        const val VALUE_ONE = 1f
        const val DURATION = 300L
    }
}