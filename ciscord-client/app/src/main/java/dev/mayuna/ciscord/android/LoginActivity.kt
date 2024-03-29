package dev.mayuna.ciscord.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dev.mayuna.ciscord.android.backend.Ciscord
import dev.mayuna.ciscord.android.backend.networking.tcp.tasks.LoginTask
import dev.mayuna.ciscord.android.backend.util.Utils
import java.net.InetAddress

class LoginActivity : AppCompatActivity() {

    private lateinit var constraintLayout: ConstraintLayout;
    private lateinit var txtinputServerAddress: TextInputEditText;
    private lateinit var txtinputUsername: TextInputEditText;
    private lateinit var txtinputPassword: TextInputEditText;
    private lateinit var btnLogin: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        constraintLayout = findViewById(R.id.activity_login);
        txtinputServerAddress = findViewById(R.id.txtinput_server_address);
        txtinputUsername = findViewById(R.id.txtinput_username);
        txtinputPassword = findViewById(R.id.txtinput_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener {
            onLoginButtonClick(it);
        }

        loadPreferences()
    }

    fun loadPreferences() {
        val sharedPref =
            this.getSharedPreferences(Constants.PREFERENCES_STORAGE_ID, Context.MODE_PRIVATE)

        val serverAddress = sharedPref.getString(Constants.PREFERENCES_SERVER_ADDRESS_ID, null)
        val username = sharedPref.getString(Constants.PREFERENCES_USERNAME_ID, null)

        if (!serverAddress.isNullOrEmpty()) {
            txtinputServerAddress.setText(serverAddress)
        }

        if (!username.isNullOrEmpty()) {
            txtinputUsername.setText(username)
        }

        if (!serverAddress.isNullOrEmpty() && !username.isNullOrEmpty()) {
            txtinputPassword.requestFocus()
        }
    }

    fun savePreferences() {
        val sharedPref =
            this.getSharedPreferences(Constants.PREFERENCES_STORAGE_ID, Context.MODE_PRIVATE)

        with(sharedPref.edit()) {
            putString(
                Constants.PREFERENCES_SERVER_ADDRESS_ID,
                txtinputServerAddress.text.toString()
            )
            putString(Constants.PREFERENCES_USERNAME_ID, txtinputUsername.text.toString())
            commit()
        }
    }

    fun validateInput(): Boolean {
        if (txtinputServerAddress.text.isNullOrEmpty()) {
            Snackbar.make(constraintLayout, "Server address is empty", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
            return false
        } else {
            var serverAddress = txtinputServerAddress.text.toString()

            if (serverAddress.contains(":")) {
                val serverAddressSplit = serverAddress.split(":")

                if (serverAddressSplit.size != 2) {
                    Snackbar.make(
                        constraintLayout,
                        "Server address is invalid",
                        Snackbar.LENGTH_SHORT
                    )
                        .setAction("Action", null).show()
                    return false
                } else {
                    val serverAddressPort = serverAddressSplit[1].toIntOrNull()
                    if (serverAddressPort == null) {
                        Snackbar.make(
                            constraintLayout,
                            "Server address (port) is invalid",
                            Snackbar.LENGTH_SHORT
                        )
                            .setAction("Action", null).show()
                        return false
                    }

                    serverAddress = serverAddressSplit[0]
                }
            }

            // check for valid inet address
            try {
                if (InetAddress.getByName(serverAddress).isAnyLocalAddress) {
                    Snackbar.make(
                        constraintLayout,
                        "Server address is invalid",
                        Snackbar.LENGTH_SHORT
                    )
                        .setAction("Action", null).show()
                    return false
                }
            } catch (ignored: Exception) {
                Snackbar.make(constraintLayout, "Server address is invalid", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
                return false
            }
        }

        if (txtinputUsername.text.isNullOrEmpty()) {
            Snackbar.make(constraintLayout, "Username is empty", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
            return false
        }

        if (txtinputPassword.text.isNullOrEmpty()) {
            Snackbar.make(constraintLayout, "Password is empty", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
            return false
        }

        return true
    }

    fun splitAddressPort(address: String): Pair<String, Int> {
        val addressSplit = address.split(":")

        if (addressSplit.size == 1) {
            return Pair(addressSplit[0], 40088)
        }

        return Pair(addressSplit[0], addressSplit[1].toInt())
    }

    fun onLoginButtonClick(view: View) {
        if (!validateInput()) {
            return
        }

        savePreferences()

        val address = splitAddressPort(txtinputServerAddress.text.toString())

        Utils.runAsyncSafe({
            try {
                Ciscord.createClientAndConnect(address.first, address.second)
            } catch (e: Exception) {
                this.runOnUiThread {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage("Failed to connect to server\n\n${e.message}")
                        .setPositiveButton("OK", null)
                        .show()
                }

                return@runAsyncSafe
            }

            val taskResult =
                LoginTask.login(txtinputUsername.text.toString(), txtinputPassword.text.toString())
                    .join();

            if (taskResult == null) {
                this.runOnUiThread {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage("Failed to login (result is null)")
                        .setPositiveButton("OK", null)
                        .show()
                }

                return@runAsyncSafe
            }

            if (taskResult.isSuccess) {
                startActivity(Intent(this, MainActivity::class.java));
            } else {
                this.runOnUiThread {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage(taskResult.errorMessage)
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }, {
            MaterialAlertDialogBuilder(this)
                .setTitle("Error")
                .setMessage("Failed to login (exception)\n\n${it.message}")
                .setPositiveButton("OK", null)
                .show()
        })
    }
}