package hu.radosdev.szilagyiapp.supporters

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.entity.Supporter

class SupportersActivity : AppCompatActivity() {

    private lateinit var supportersListView: ListView
    private lateinit var supportersAdapter: SupportersAdapter

    // Sample data, replace with your actual data source
    private val supportersList = listOf(
        Supporter("Marshall Ablak Kft.", R.drawable.marshall_logo, "https://www.marshallablak.hu/"),
        Supporter("Agria Informatika Kft.", R.drawable.ai, "https://agriainfo.hu"),
        Supporter("Cserháti Gabriella e.v.", null, null) // No image
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_supporters)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportersListView = findViewById(R.id.supporters_list_view)
        supportersAdapter = SupportersAdapter(this, supportersList)
        supportersListView.adapter = supportersAdapter
    }
}
