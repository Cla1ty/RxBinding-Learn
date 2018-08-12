package com.dwiariyanto.rxbinding.appcompatv7

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.jakewharton.rxbinding2.support.v7.widget.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionMenuView()
        setupPopupMenu()
        setupSearchView()
        setupToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        return true
    }

    private fun setupActionMenuView()
    {
        actionMenuView.apply {
            menuInflater.inflate(R.menu.edit, menu)
            itemClicks().map { it.title }
                    .subscribe { toast(it) }
        }
    }

    private fun setupPopupMenu()
    {
        val popupMenu = PopupMenu(this, btnPopupMenu, Gravity.RIGHT).apply {
            inflate(R.menu.edit)
            itemClicks().map { it.title }
                    .subscribe { toast(it) }
            dismisses().delay(1, TimeUnit.SECONDS)
                    .toUi()
                    .subscribe { toast("Dismiss") }
        }

        btnPopupMenu.setOnClickListener {
            popupMenu.show()
        }
    }

    private fun setupSearchView()
    {
        searchView.apply {
            queryTextChangeEvents().subscribe { toast("${it.isSubmitted} ${it.queryText()}") }
            queryTextChanges().subscribe { toast(it) }
        }
    }

    private fun setupToolbar()
    {
        findViewById<Toolbar>(R.id.action_bar).apply {
            //inflateMenu(R.menu.edit)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
            }

            itemClicks().map { it.title }
                    .subscribe { toast(it) }
            navigationClicks().subscribe { toast("Navigation") }
        }
    }

    private fun toast(message: CharSequence?)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun <T> Observable<T>.onUi(): Observable<T>
    {
        return subscribeOn(AndroidSchedulers.mainThread())
    }

    private fun <T> Observable<T>.toUi(): Observable<T>
    {
        return observeOn(AndroidSchedulers.mainThread())
    }

    private fun <T> Observable<T>.toIo(): Observable<T>
    {
        return observeOn(Schedulers.io())
    }

    private fun <T> Observable<T>.onIoToUi(): Observable<T>
    {
        return subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
