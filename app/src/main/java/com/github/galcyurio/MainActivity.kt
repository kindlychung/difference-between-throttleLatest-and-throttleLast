package com.github.galcyurio

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val disposable = CompositeDisposable()
    private var observable = Flowable.range(1, 10000).delay{t: Int ->
        if(t % 5 == 0) Flowable.just(1).delay(1100, TimeUnit.MILLISECONDS) else Flowable.just(1).delay(100, TimeUnit.MILLISECONDS)
    }
    private lateinit var stringListAdapter4First: StringListAdapter
    private lateinit var stringListAdapter4Last: StringListAdapter
    private lateinit var stringListAdapter4Latest: StringListAdapter

    private val strings4First = mutableListOf<String>()
    private val strings4Last = mutableListOf<String>()
    private val strings4Latest = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        stringListAdapter4First = recyclerView4First.initStringListAdapter()
        stringListAdapter4Last = recyclerView4Last.initStringListAdapter()
        stringListAdapter4Latest = recyclerView4Latest.initStringListAdapter()

        subscribeThrottleFirst()
        subscribeThrottleLast()
        subscribeThrottleLatest()

        button.setOnTouchListener { _, _ ->
            disposable.clear()
            observable = Flowable.range(0, 10000).delay{t: Int ->
                if(t % 5 == 0) Flowable.just(1).delay(2, TimeUnit.SECONDS) else Flowable.just(1).delay(10, TimeUnit.MILLISECONDS)
            }
            subscribeThrottleFirst()
            subscribeThrottleLast()
            subscribeThrottleLatest()
            return@setOnTouchListener false
        }
    }

    private fun subscribeThrottleFirst() {
        strings4First.clear()
        stringListAdapter4First.submitList(strings4First)
        observable
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { Log.e("ThrottleFirst", "onError", it) },
                onNext = {
                    strings4First.add(0, it.toString())
                    stringListAdapter4First.submitList(strings4First.toList())
                },
                onComplete = { Log.i("ThrottleFirst", "onComplete") }
            )
            .addTo(disposable)
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
        strings4Last.clear()
        stringListAdapter4Last.submitList(strings4Last)
        observable
            .throttleLast(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { Log.e("ThrottleLast", "onError", it) },
                onNext = {
                    strings4Last.add(0, it.toString())
                    stringListAdapter4Last.submitList(strings4Last.toList())
                },
                onComplete = { Log.i("ThrottleLast", "onComplete") }
            )
            .addTo(disposable)
    }

    private fun subscribeThrottleLatest() {
        strings4Latest.clear()
        stringListAdapter4Latest.submitList(strings4Latest)
        observable
            .throttleLatest(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { Log.e("ThrottleLatest", "onError", it) },
                onNext = {
                    strings4Latest.add(0, it.toString())
                    stringListAdapter4Latest.submitList(strings4Latest.toList())
                },
                onComplete = { Log.i("ThrottleLatest", "onComplete") }
            )
            .addTo(disposable)
    }
}