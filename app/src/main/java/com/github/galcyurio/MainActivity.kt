package com.github.galcyurio

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val disposable = CompositeDisposable()
    private val observable = BehaviorSubject.createDefault("default")
    private lateinit var stringListAdapter4Last: StringListAdapter
    private lateinit var stringListAdapter4Latest: StringListAdapter

    private val strings4Last = mutableListOf<String>()
    private val strings4Latest = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        subscribeThrottleLast()
        subscribeThrottleLatest()

        stringListAdapter4Last = recyclerView4Last.initStringListAdapter()
        stringListAdapter4Latest = recyclerView4Latest.initStringListAdapter()

        button.setOnTouchListener { _, _ ->
            observable.onNext(System.currentTimeMillis().toString())
            return@setOnTouchListener false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun RecyclerView.initStringListAdapter(): StringListAdapter {
        val stringListAdapter = StringListAdapter()
        stringListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                scrollToPosition(0)
            }
        })
        adapter = stringListAdapter
        return stringListAdapter
    }

    private fun subscribeThrottleLast() {
        observable
            .throttleLast(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { Log.e("ThrottleLast", "onError", it) },
                onNext = {
                    strings4Last.add(0, it)
                    stringListAdapter4Last.submitList(strings4Last.toList())
                },
                onComplete = { Log.i("ThrottleLast", "onComplete") }
            )
            .addTo(disposable)
    }

    private fun subscribeThrottleLatest() {
        observable
            .throttleLatest(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { Log.e("ThrottleLatest", "onError", it) },
                onNext = {
                    strings4Latest.add(0, it)
                    stringListAdapter4Latest.submitList(strings4Latest.toList())
                },
                onComplete = { Log.i("ThrottleLatest", "onComplete") }
            )
            .addTo(disposable)
    }
}