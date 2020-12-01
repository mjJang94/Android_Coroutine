package com.mj.android_coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

// 1. CoroutineScope 를 상속받는다.
class LifeCycleCoroutineActivity : AppCompatActivity(), CoroutineScope {


    /**
     * Activity 에서 코루틴을 GlobalScope 영역에서 실행시킴으로서 Activity 가 종료되어도 코루틴은 계속해서 자신이 할 일을 해나가는데, 사실 데이터를 다운받는다거나 하는 와중에 Activity 가 종료 되었음에도 불구하고 계속해서 다운받으면 리소스가 낭비되고 있는 것이다.
     *
     * 해결 방법으로는 Activity 의 LifeCycle 과 일치하도록 Scope 에 코루틴을 실행시키면 이러한 현상을 보완할 수 있다.
     *
     * 기본적으로 Activity scope 는 따로 존재 하지 않아서 Activity 의 Lifecycle 과 일치하는 Scope 를 직접 명세해줘야 한다.
     */


    private val TAG = this::class.qualifiedName

    //2. job 객체를 선언한다.
    private lateinit var job: Job

    //3. CoroutineScope 의 CoroutineContext 변수를 오버라이드 한다. 그리고 위에서 생성한 Job 객체를 이어 붙여준다.
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_life_cycle_coroutine)

        //4. Activity LifeCycle 에 맞게끔 onCreate()에서 job 의 객체를 만들어준다.
        job = Job()


        //========================================================================================================================
        //6. Activity 단에서 이미 CoroutineScope 를 구현했기 때문에 아래처럼 오버라이드한 coroutineContext 를 참조하거나 launch 키워드로 실행시킬 수 있다.
        CoroutineScope(coroutineContext).launch {
            delay(5000L)
            Log.d(TAG, "LifeCycleCoroutineActivity in Coroutine wait for 5 sec")
        }

        launch {
            delay(5000L)
            Log.d(TAG, "LifeCycleCoroutineActivity in Coroutine wait for 5 sec")
        }
        //========================================================================================================================
    }

    override fun onDestroy() {
        super.onDestroy()
        //5. Activity 가 종료될 때 함께 종료되게끔 onDestroy() 에서 job 을 취소한다.
        job.cancel()
    }
}