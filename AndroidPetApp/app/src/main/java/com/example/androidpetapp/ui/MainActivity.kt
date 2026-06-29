package com.example.androidpetapp.ui

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.androidpetapp.R
import com.example.androidpetapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var activeTag: String = TAG_PETS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.updatePadding(top = bars.top)
            binding.bottomNav.updatePadding(bottom = bars.bottom)
            insets
        }

        setupFragments(savedInstanceState)
        setupBottomNav()
        setupAdminToggle()
    }

    private fun setupFragments(savedInstanceState: Bundle?) {
        val fm = supportFragmentManager
        if (savedInstanceState == null) {
            val pets = PetsFragment()
            val about = AboutFragment()
            val contact = ContactFragment()
            fm.beginTransaction()
                .add(R.id.navHost, pets, TAG_PETS)
                .add(R.id.navHost, about, TAG_ABOUT).hide(about)
                .add(R.id.navHost, contact, TAG_CONTACT).hide(contact)
                .commit()
            activeTag = TAG_PETS
        } else {
            activeTag = savedInstanceState.getString(KEY_ACTIVE_TAG, TAG_PETS)
        }
        binding.toolbar.title = titleFor(activeTag)
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            val tag = when (item.itemId) {
                R.id.nav_pets -> TAG_PETS
                R.id.nav_about -> TAG_ABOUT
                R.id.nav_contact -> TAG_CONTACT
                else -> return@setOnItemSelectedListener false
            }
            switchTo(tag)
            true
        }
    }

    private fun switchTo(tag: String) {
        if (tag == activeTag) return
        val fm = supportFragmentManager
        val tx = fm.beginTransaction()
        for (t in listOf(TAG_PETS, TAG_ABOUT, TAG_CONTACT)) {
            fm.findFragmentByTag(t)?.let { f: Fragment ->
                if (t == tag) tx.show(f) else tx.hide(f)
            }
        }
        tx.commit()
        activeTag = tag
        binding.toolbar.title = titleFor(tag)
    }

    private fun titleFor(tag: String): String = when (tag) {
        TAG_ABOUT -> "About"
        TAG_CONTACT -> "Contact"
        else -> "Pawfound"
    }

    private fun setupAdminToggle() {
        binding.toolbar.inflateMenu(R.menu.menu_main)
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_admin) {
                onAdminIconTapped()
                true
            } else {
                false
            }
        }
        // Reflect the current mode and keep the icon in sync if it changes.
        AdminMode.isAdmin.observe(this) { isAdmin -> updateAdminIcon(isAdmin) }
    }

    private fun onAdminIconTapped() {
        if (AdminMode.isAdminNow) {
            AdminMode.disable()
            Toast.makeText(this, "Public mode", Toast.LENGTH_SHORT).show()
        } else {
            promptForPin()
        }
    }

    private fun promptForPin() {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            hint = "PIN"
        }
        // Pad the dialog's input a little.
        val container = FrameLayout(this).apply {
            val pad = (24 * resources.displayMetrics.density).toInt()
            setPadding(pad, pad / 2, pad, 0)
            addView(input)
        }
        AlertDialog.Builder(this)
            .setTitle("Enter admin PIN")
            .setMessage("Admin mode unlocks add / edit / delete.")
            .setView(container)
            .setPositiveButton("Unlock") { _, _ ->
                if (AdminMode.enableWithPin(input.text?.toString().orEmpty())) {
                    Toast.makeText(this, "Admin mode on", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateAdminIcon(isAdmin: Boolean) {
        binding.toolbar.menu.findItem(R.id.action_admin)?.apply {
            setIcon(if (isAdmin) R.drawable.ic_lock_open else R.drawable.ic_lock)
            title = if (isAdmin) "Exit admin mode" else "Admin mode"
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_ACTIVE_TAG, activeTag)
    }

    companion object {
        private const val TAG_PETS = "pets"
        private const val TAG_ABOUT = "about"
        private const val TAG_CONTACT = "contact"
        private const val KEY_ACTIVE_TAG = "active_tag"
    }
}
