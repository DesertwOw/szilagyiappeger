package hu.radosdev.szilagyiapp.supporters

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import hu.radosdev.szilagyiapp.MainActivity
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.entity.ChildMenuItem
import hu.radosdev.szilagyiapp.data.entity.MainMenuItem
import hu.radosdev.szilagyiapp.data.entity.Supporter
import hu.radosdev.szilagyiapp.menu.MenuAdapter
import hu.radosdev.szilagyiapp.menu.MenuViewModel
import hu.radosdev.szilagyiapp.util.Constants

@AndroidEntryPoint
class SupportersActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private val menuViewModel: MenuViewModel by viewModels()
    private lateinit var menuAdapter: MenuAdapter
    private  lateinit var toolbarTitle: TextView
    private lateinit var supportersListView: ListView
    private lateinit var supportersAdapter: SupportersAdapter

    private val supportersList = listOf(
        Supporter(Constants.MARSHALL, R.drawable.marshall_logo, Constants.MARSHALL_WEB),
        Supporter(Constants.AGRIA, R.drawable.ai, Constants.AGRIA_WEB),
        Supporter(Constants.EV, null, null)
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supporters)

        drawerLayout = findViewById(R.id.drawer_layout)
        val menuIcon = findViewById<ImageView>(R.id.menu_icon)
        val menuLogo = findViewById<ImageView>(R.id.menu_logo)
        toolbarTitle = findViewById(R.id.toolbar_title)

        menuIcon.setOnClickListener { toggleDrawer() }

        menuLogo.setOnClickListener {
            animateLogoAndLoadUrl(menuLogo)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(Constants.URL, Constants.BASE_URL)
            intent.putExtra(Constants.TITLE,Constants.MAIN_PAGE)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.menu_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        menuAdapter = MenuAdapter(mutableListOf(), ::handleChildMenuItemClick, this)
        recyclerView.adapter = menuAdapter

        menuViewModel.loadMenuItems()
        menuViewModel.menuItems.observe(this){
            items: List<MainMenuItem> ->
            val updatedItems = items.toMutableList()
            updatedItems.add(MainMenuItem(title = Constants.SUPPORTERS, childs = null))
            menuAdapter.updateMenu(updatedItems)
        }

        supportersListView = findViewById(R.id.supporters_list_view)
        supportersAdapter = SupportersAdapter(this, supportersList)
        supportersListView.adapter = supportersAdapter

        toolbarTitle.text = Constants.SUPPORTERS


    }

    private fun toggleDrawer(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START,true)
        }else{
            drawerLayout.openDrawer(GravityCompat.START,true)
        }
    }

    private fun handleChildMenuItemClick(childMenuItem: ChildMenuItem) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.URL, childMenuItem.url)
        intent.putExtra(Constants.TITLE,childMenuItem.title)
        startActivity(intent)
        finish()
    }

    private fun animateLogoAndLoadUrl(menuLogo: ImageView) {
        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            menuLogo,
            PropertyValuesHolder.ofFloat(Constants.SCALE_X, Constants.VALUE_ONETWO),
            PropertyValuesHolder.ofFloat(Constants.SCALE_Y, Constants.VALUE_ONETWO)
        ).setDuration(Constants.DURATION)

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            menuLogo,
            PropertyValuesHolder.ofFloat(Constants.SCALE_X, Constants.VALUE_ONE),
            PropertyValuesHolder.ofFloat(Constants.SCALE_Y, Constants.VALUE_ONE)
        ).setDuration(Constants.DURATION)

        val animationSet = AnimatorSet()
        animationSet.play(scaleUp).before(scaleDown)



        animationSet.start()
    }
}


