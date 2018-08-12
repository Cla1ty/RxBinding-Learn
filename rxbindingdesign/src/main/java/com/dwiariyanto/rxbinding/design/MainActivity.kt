package com.dwiariyanto.rxbinding.design

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.Toast
import com.jakewharton.rxbinding2.support.design.widget.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*

class MainActivity : AppCompatActivity()
{
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        setupAppBarLayout()
        setupBottomNavigationMenu()
        setupSnackBar()
        setupTabLayout()
        setupNavigationView()
        setupBottomSheet()
    }

    private fun setupNavigationView()
    {
        val toggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.itemSelections()
                .map { it.title }
                .subscribe {
                    drawerLayout.closeDrawer(Gravity.START)
                    toast(it)
                }
    }

    private fun setupAppBarLayout()
    {
        appBarLayout.offsetChanges()
                .map { it == 0 }
                .subscribe(fab.visibility())
        //                .subscribe { toast("Appbar offset $it") }
    }

    private fun setupBottomNavigationMenu()
    {
        bottomNavigationView.itemSelections()
                .map { it.title }
                .subscribe { toast(it) }
    }

    private fun setupSnackBar()
    {
        btnSnackbar.setOnClickListener { snackBar("This is Snackbar") }
    }

    private fun setupTabLayout()
    {
        tabLayout.apply {
            selections().map { it.text }
            //                    .subscribe { toast(it) }

            selectionEvents().filter { it is TabLayoutSelectionSelectedEvent }
                    .map { it.tab().text }
            //                    .subscribe { toast("select $it") }

            selectionEvents().filter { it is TabLayoutSelectionUnselectedEvent }
                    .map { it.tab().text }
                    .subscribe { toast("unselect $it") }

            selectionEvents().filter { it is TabLayoutSelectionReselectedEvent }
                    .map { it.tab().text }
                    .subscribe { toast("reselects $it") }
        }
    }

    private fun setupBottomSheet()
    {
        val bsBehaviour = BottomSheetBehavior.from(bottom_sheet)
        bottomSheet.setOnClickListener {
            when (bsBehaviour.state)
            {
                BottomSheetBehavior.STATE_HIDDEN ->
                {
                    bsBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
                }
                BottomSheetBehavior.STATE_EXPANDED ->
                {
                    bsBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                BottomSheetBehavior.STATE_COLLAPSED ->
                {
                    bsBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }

    }

    private fun snackBar(message: CharSequence)
    {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).apply {
            dismisses().subscribe { toast("Dismiss") }
        }.show()
    }

    private fun toast(message: CharSequence?)
    {
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_LONG).also { it.show() }

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
