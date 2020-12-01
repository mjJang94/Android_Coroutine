package com.mj.android_coroutine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {


    private val TAG = this::class.qualifiedName

    //코루틴 예외 처리에 관한 핸들러
    private val handler = CoroutineExceptionHandler{ coroutineContext, exception ->
        Log.e(TAG, "launch() $exception handled!")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //코루틴이 비동기로서 동작하는 방식을 확인하는 메소드
        coroutineAsyncTest()

        //코루틴의 실행 메소드인 launch 와 async 의 차이점을 다루는 메소드
        coroutineLaunchAndAsync()

        //코루틴의 launch 실행문을 통한 예외 처리를 보여주는 메소드
        launchHandleCoroutineException()

        //코루틴의 async 와 withContext 에서의 예외처리를 보여주는 메소드
        asyncOrWithContextHandleCoroutineException()

        btn_next_activity.setOnClickListener {

            val intent = Intent(this, LifeCycleCoroutineActivity::class.java)
            startActivity(intent)
        }
    }



    private fun coroutineAsyncTest(){

        Log.d(TAG,"coroutineAsyncTest before coroutine")

        GlobalScope.launch(Dispatchers.Main){
            //화면이 바꿔어도 코루틴내에 동작은 유지딘다.
            delay(5000L)
            Log.d(TAG,"coroutineAsyncTest in Coroutine wait for 5 sec")
        }

        Log.d(TAG,"coroutineAsyncTest after coroutine")
    }


    /**
     * launch 와 async 는 코루틴 실행 명령어지만 차이점이 존재한다.
     *
     * launch 는 리턴값이 없는 실행 명령어이고
     *
     * async 는 리턴값이 있는 실행 명령어이다.
     *
     */
    private fun coroutineLaunchAndAsync(){

        GlobalScope.launch {
            launch {
                Log.d(TAG,"coroutineLaunchAndAsync launch has no return value")
            }

            val value: Int = async {
                1+2
            }.await()// 작업이 완료되면 미리 지정된 타입의 객체를 리턴한다.

            Log.d(TAG,"coroutineLaunchAndAsync launch has $value value")
        }
    }

    // 실행 명령어 launch 블록 안에서의 예외 처리 예시
    private fun launchHandleCoroutineException(){

        GlobalScope.launch(Dispatchers.IO + handler){
            throw Exception()
        }
    }

    // 실행 명령어 async 나 withContext 를 사용하는 경우의 예외 처리 예시
    private fun asyncOrWithContextHandleCoroutineException(){

        GlobalScope.launch(Dispatchers.IO){
            try {
                val name = withContext(Dispatchers.Main){
                    throw Exception()
                }
            }catch (e: Exception){
                Log.e(TAG, "async() or withContext $e handled!")
            }
        }
    }



}