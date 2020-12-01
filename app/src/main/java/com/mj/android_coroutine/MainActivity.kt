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

    /**
     * 코루틴은 스레드가 아니지만, 비동기로 실행되고 기본적으로 Activity 가 종료되어도 계속해서 동작한다.
     *
     * 별도의 처리가 필요하다.
     */

    private fun coroutineAsyncTest(){

        Log.d(TAG,"coroutineAsyncTest before coroutine")

        GlobalScope.launch(Dispatchers.Main){
            //화면이 바꿔어도 코루틴내에 동작은 유지딘다.
            delay(5000L)
            Log.d(TAG,"coroutineAsyncTest in Coroutine wait for 5 sec")

            makeLog()
        }

        Log.d(TAG,"coroutineAsyncTest after coroutine")


    }

    /**
     * 코루틴 안에서 일반적인 메소드는 호출이 불가하다.
     *
     * 그 이유는 코루틴이 스레드를 바꿔가며 작업할 수도 있기 때문에 실행을 멈추거나 다시 실행될 수 있기 때문이다.
     *
     * 코루틴 전용 메소드를 만들기 위해서는 suspend 키워드를 붙여주면 된다.
     *
     * 또한 suspend 함수 내에서는 새로운 코루틴을 만들수도 있다.
     */
    private suspend fun makeLog(){

        GlobalScope.launch {
            Log.d(TAG, "make debug log with suspend function")
        }

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

    /**
     * 실행 명령어 launch 블록 안에서의 예외 처리 예시
     */
    private fun launchHandleCoroutineException(){

        GlobalScope.launch(Dispatchers.IO + handler){
            throw Exception()
        }
    }

    /**
     * 실행 명령어 async 나 withContext 를 사용하는 경우의 예외 처리 예시
     *
     * async 와 withContext 는 동일하게 결과를 리턴하는 키워드이다.
     *
     * 차이점은 async 에서는 await() 에소드를 호출해야하지만, withContext 는 생략할 수 있다.
     */
    //
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