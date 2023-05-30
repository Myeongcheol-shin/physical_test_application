package com.shino72.physical.ui.main

import android.os.Looper
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.logging.Handler


class MainViewModel : ViewModel() {

    private val _status = MutableLiveData<Status>()
    val status :  LiveData<Status> get() = _status

    var msg = ObservableField<String>()

    private var startTime = 0L
    private var endTime = 0L

    private val handler = android.os.Handler(Looper.getMainLooper())

    init {
        _status.value = Status.MAIN
        msg.set("화면을 클릭하면 게임이 시작합니다.\n 녹색이 보이면 빠르게 누르세요.")
    }
    fun gameBtnClick() {
        when(status.value)
        {
            Status.MAIN -> {
                gameStart()
            }
            Status.START -> {
                endTime = System.nanoTime()
                msg.set("당신의 반응속도는 ${(((endTime - startTime)/1_000_000_000.0).toDouble())}초 입니다.")
                _status.value = Status.END
            }
            Status.PAUSE -> {
                _status.value = Status.FAST
                _status.value = Status.END
                msg.set("너무 빨리 눌렀어요")
                handler.removeCallbacksAndMessages(null)
            }

            Status.END -> {
                _status.value = Status.MAIN
                msg.set("화면을 클릭하면 게임이 시작합니다.\n 녹색이 보이면 빠르게 누르세요.")
            }
            else -> {

            }
        }
    }

    private fun gameStart()
    {
        _status.value = Status.PAUSE

        val range = (10 until 30)
        val randN = range.random() * 100

        handler.postDelayed(Runnable {
            _status.postValue(Status.START)
            startTime = System.nanoTime()
            msg.set("클릭!")
        }, randN.toLong())
    }
}