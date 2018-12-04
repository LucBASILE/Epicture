package basile.luc.epicture

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val clientId = "9e775f25b104d4d"
    val clientSecret = "7b8834b4c004cd6ccdaa44276666df82575d5441"
    val Imgur_Authe = Imgur_Auth()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        replaceFragment(HomeFragment())
    }

    override fun onResume() {
        super.onResume()
        when (intent.getAction()) {
            Intent.ACTION_VIEW -> actionView(intent)
        }
    }
    override fun onNewIntent(intent: Intent) {
        when (intent.getAction()) {
            Intent.ACTION_VIEW -> actionView(intent)
        }
    }

    fun actionView(intent: Intent) {
        val uri = intent.getData()
        Imgur_Authe.authenticateCallback(uri.toString())
        Toast.makeText(this@MainActivity, "Successfully Logged in", Toast.LENGTH_LONG).show()
    }

    //Handle Connection END

    //Handle Recycler View END

    //Handle button in toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_login -> {
            Imgur_Authe.authenticate(this@MainActivity)
            Toast.makeText(this, "Connection button", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.action_search -> {
            // do stuff
            Toast.makeText(this, "Search button", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.action_upload -> {
            // do stuff
            Toast.makeText(this, "Upload button", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.action_setting -> {
            // do stuff
            Toast.makeText(this, "Settings button", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.action_about -> {
            // do stuff
            Toast.makeText(this, "About button", Toast.LENGTH_SHORT).show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    //Handle selection event on navigation items
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.home -> {
                replaceFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.favorite -> {
                replaceFragment(FavoriteFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.profile -> {
                println(Imgur_Authe.accountUsername)
                val fra = ProfileFragment.newInstance(Imgur_Authe.token, Imgur_Authe.accountUsername)
                replaceFragment(fra)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    //Import login_button xml and handle search bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.login_button, menu)
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as android.support.v7.widget.SearchView
            searchView.setOnQueryTextListener(object : android.support.v7.widget.SearchView.OnQueryTextListener{
                override fun onQueryTextChange(query : String?): Boolean {
                    //Text has changed, apply filter
                    Toast.makeText(this@MainActivity, "onQueryTextChange", Toast.LENGTH_SHORT).show()
                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    //Perform the final search
                    if (query != null) {
                        val fragment = SearchFragment.newInstance(query)
                        replaceFragment(fragment)
                    }
                    return true
                }
            })
        }
        return true
    }

    //Function to switch between fragment
    private fun replaceFragment(fragment: Fragment){
        //edit operations on the Fragments associated with this FragmentManager
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }
}

