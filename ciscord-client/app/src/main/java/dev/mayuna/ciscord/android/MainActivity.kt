package dev.mayuna.ciscord.android

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import dev.mayuna.ciscord.android.backend.Ciscord
import dev.mayuna.ciscord.android.backend.networking.tcp.tasks.ChannelTasks
import dev.mayuna.ciscord.android.databinding.ActivityMainBinding
import dev.mayuna.ciscord.commons.objects.CiscordChannel
import dev.mayuna.ciscord.commons.objects.CiscordChatMessage


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        /*
        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Starting Ciscord Client", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()

            Thread {
                Ciscord.client = CiscordClient(ClientConfig())
                Ciscord.client.setup()
                Ciscord.client.start()
                Ciscord.client.connect("10.0.2.2", 40088)
                NetworkTask.ExchangeProtocolVersion().run(69)
                    .whenCompleteAsync { result, throwable ->
                        if (throwable != null) {
                            Snackbar.make(
                                view,
                                "Exception: " + throwable.message,
                                Snackbar.LENGTH_LONG
                            )
                                .setAction("Action", null).show()
                        } else {
                            Snackbar.make(
                                view,
                                "Result protocol: " + result.protocolVersion,
                                Snackbar.LENGTH_LONG
                            )
                                .setAction("Action", null).show()
                        }
                    }
            }.start()
        }*/

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_chat)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_chat
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.set_channel) {
            val alert: AlertDialog.Builder = AlertDialog.Builder(this)
            alert.setTitle("Set Channel")
            alert.setMessage("Please, specify the channel ID:")

            val input = EditText(this)
            alert.setView(input)

            alert.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, whichButton ->
                val value = input.text.toString().toLong()

                ChannelTasks.fetch(value).whenCompleteAsync { result, throwable ->
                    if (throwable != null) {
                        return@whenCompleteAsync
                    }

                    if (result.result == null) {
                        runOnUiThread {
                            MaterialAlertDialogBuilder(this)
                                .setTitle("Error")
                                .setMessage("Channel with ID $value not found. Create it?")
                                .setPositiveButton("OK") { dialog, which ->
                                    run {
                                        ChannelTasks.create(
                                            Ciscord.user.username + "'s channel",
                                        ).whenCompleteAsync { result, throwable ->
                                            if (throwable != null) {
                                                return@whenCompleteAsync
                                            }

                                            if (result == null) {
                                                runOnUiThread {
                                                    MaterialAlertDialogBuilder(this)
                                                        .setTitle("Error")
                                                        .setMessage("Failed to create channel")
                                                        .setPositiveButton("OK", null)
                                                        .show()
                                                }
                                                return@whenCompleteAsync
                                            }

                                            runOnUiThread {
                                                MaterialAlertDialogBuilder(this)
                                                    .setTitle("Success")
                                                    .setMessage("Created the channel")
                                                    .setPositiveButton("OK", null)
                                                    .show()
                                            }

                                            Ciscord.currentChannel = result.result;
                                            Ciscord.resetMessages = true;
                                        }
                                    }
                                }
                                .setNegativeButton("Cancel", null)
                                .show()
                        }
                        return@whenCompleteAsync
                    }

                    Ciscord.currentChannel = result.result;
                    Ciscord.resetMessages = true;
                }

                return@OnClickListener
            })

            alert.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which ->
                    return@OnClickListener
                })

            alert.show()

            return true // Consume the event
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_chat)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}