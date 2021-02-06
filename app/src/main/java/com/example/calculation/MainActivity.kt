package com.example.calculation

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.example.calculation.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mNumberStack: Stack<Int>
    private lateinit var mOperatorStack: Stack<String>
    private val bean = Bean()
    private val random: Random = Random()
    private var mRealResult = 0
    private lateinit var mContentBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContentBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        ) as ActivityMainBinding

        mContentBinding.data = bean
        mContentBinding.events = this
        mContentBinding.lifecycleOwner = this

        mNumberStack = Stack()
        mOperatorStack = Stack()
        updateSubject()
    }

    private fun updateSubject() {
        val generateData = generateData(3)
        bean.content.set(generateData)
    }

    fun submit(view: View) {
        val editResult = mContentBinding.etResult.text.toString().trim()
        if (TextUtils.isEmpty(editResult)) {
            tip("小朋友, 还没有输入数据哦!")
            return
        }
        if (editResult.toInt() != mRealResult) {
            tip("不对哦,再想想吧！")
            mContentBinding.etResult.setText("")
            return
        }

        tip("恭喜你, 答对了!")
        updateSubject()
    }

    private fun tip(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun generateData(size: Int): String {
        val builder = StringBuilder()
        for (i in 0 until size) {
            val number = getNumber(30)
            val operator = getOperator()
            builder.append("$number ")
            if (i != size - 1) {
                builder.append("$operator ")
            }
        }
        updateRealResult(builder.toString())
        if (mRealResult < 0) {
            generateData(size)
        }
        builder.append("= ")
        return builder.toString()
    }

    private fun updateRealResult(subject: String) {
        val array = subject.trim().split(" ")
        Log.d(TAG, "updateRealResult: $array")
        for (item in array) {
            Log.d(TAG, "updateRealResult: $item")
            if (OPERATOR_ARRAY.contains(item)) {
                mOperatorStack.push(item)
                Log.d(Companion.TAG, "updateRealResult: $mOperatorStack")
            } else {
                var number = item.toInt()
                if (!mOperatorStack.empty()) {
                    val operator = mOperatorStack.peek()
                    if (operator == OPERATOR_ARRAY[2]) {
                        mOperatorStack.pop()
                        number = mNumberStack.pop() * item.toInt()
                    }
                }
                mNumberStack.push(number)
                Log.d(TAG, "updateRealResult: $mNumberStack")
            }
        }
        if (mOperatorStack.isEmpty()) {
            mRealResult = mNumberStack.pop()
        }
        for (i in 0 until mOperatorStack.size) {
            val pop = mOperatorStack.pop()
            if (pop == OPERATOR_ARRAY[0]) {
                mRealResult -= mNumberStack.pop()
            } else {
                mRealResult += mNumberStack.pop()
            }
        }

    }

    private fun getOperator(): String {
        when (getNumber(3)) {
            0 -> return "+"
            1 -> return "-"
            2 -> return "x"
        }
        return "+"
    }

    private fun getNumber(bound: Int): Int = random.nextInt(bound)

    class Bean {
        public var content: ObservableField<String> = ObservableField()
    }

    companion object {
        private const val TAG = "MainActivity"
        private val OPERATOR_ARRAY = arrayOf("+", "-", "x")
    }

}

