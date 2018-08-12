package com.dwiariyanto.rxbinding.supportv4

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SlidingPaneLayout
import android.support.v7.widget.Toolbar
import android.util.Log
import com.jakewharton.rxbinding2.support.v4.widget.*
import com.jakewharton.rxbinding2.support.v7.widget.navigationClicks
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

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        setupSlidingPane()
        setupSwipeToRefresh()
        setupNestedScrollView()
    }

    private fun setupSlidingPane()
    {
        findViewById<Toolbar>(R.id.action_bar)
                .navigationClicks()
                .map { !slidingPane.isOpen }
                .subscribe(slidingPane.open())

        slidingPane.panelOpens()
                .subscribe { Log.v("aa", "open $it") }

        slidingPane.panelSlides()
                .subscribe { Log.v("aa", "slide $it") }
    }

    private fun setupSwipeToRefresh(){
        swipeRefresh.refreshes()
                .delay(1, TimeUnit.SECONDS)
                .toUi()
                .subscribe { swipeRefresh.isRefreshing = false }
    }

    private fun setupNestedScrollView(){
        nestedScrollView.scrollChangeEvents()
                .subscribe { Log.v("aa", "slide ${it.scrollY()}") }
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
