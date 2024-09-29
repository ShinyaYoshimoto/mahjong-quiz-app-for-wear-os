/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.mahjongquizapp.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.mahjongquizapp.presentation.theme.MahjongQuizAppTheme
import org.json.JSONObject
import com.example.mahjongquizapp.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel : ViewModel() {
    private val _apiResponse = MutableLiveData<Boolean?>()
    val apiResponse: LiveData<Boolean?> = _apiResponse

    private val _isParent = MutableLiveData<Boolean>()
    val isParent: LiveData<Boolean> = _isParent

    private val _symbolCount = MutableLiveData<Int>()
    val symbolCount: LiveData<Int> = _symbolCount

    private val _fanCount = MutableLiveData<Int>()
    val fanCount: LiveData<Int> = _fanCount

    private val _isDraw = MutableLiveData<Boolean>()
    val isDraw: LiveData<Boolean> = _isDraw

    private val _hasAnswer = MutableLiveData<Boolean>()
    val hasAnswer: LiveData<Boolean> = _hasAnswer

    init {
        generateNewQuiz()
    }

    private fun generateNewQuiz() {
        _isParent.value = (0..1).random() == 1
        _symbolCount.value = (2..11).random() * 10
        _fanCount.value = generateRandomFanCount()
        _isDraw.value = (0..1).random() == 1
        _hasAnswer.value = false
    }

    private fun generateRandomFanCount(): Int {
        val lotteryNumber = (1..100).random()
        return when (lotteryNumber) {
            in 100..100 -> 13 // 1%
            in 99..99 -> 12 // 1%
            in 98..98 -> 11 // 1%
            in 97..97 -> 10 // 1%
            in 96..96 -> 9 // 1%
            in 95..95 -> 8 // 1%
            in 94..94 -> 7 // 1%
            in 93..93 -> 6 // 1%
            in 90..92 -> 5 // 2%
            in 86..90 -> 4 // 5%
            in 70..85 -> 3 // 15%
            in 40..69 -> 2 // 30%
            else -> 1 // 40%
        }
    }

    fun callAnswerApi(
        context: Context,
        payForStartPlayer: Int,
        payForOther: Int
    ) {
        _hasAnswer.value = true
        val url = BuildConfig.API_ROOT + "/scores/answer"
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)

        val json = JSONObject()
        val question = JSONObject()
        question.put("isStartPlayer", isParent.value)
        question.put("isDraw", isDraw.value)
        question.put("symbolCount", symbolCount.value)
        question.put("fanCount", fanCount.value)
        json.put("question", question)

        val answer = JSONObject()
        val score = JSONObject()
        score.put("startPlayer", payForStartPlayer)
        score.put("other", payForOther)
        answer.put("score", score)
        json.put("answer", answer)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, json,
            { response ->
                _apiResponse.value = response.getBoolean("isCorrect")
                viewModelScope.launch {
                    delay(3000)
                    generateNewQuiz()
                    _apiResponse.value = null
                }
            },
            { error ->
                Log.e("API_CALL", error.toString())
                _apiResponse.value = false
                viewModelScope.launch {
                    delay(3000)
                    generateNewQuiz()
                    _apiResponse.value = null
                }
            }
        )

        requestQueue.add(jsonObjectRequest)
    }
}

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var speechRecognizerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        speechRecognizerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val spokenText = results?.get(0) ?: "認識できませんでした"
                val (startPlayerScore, otherPlayerScore) = parseScore(spokenText)
                // 以下のルールに則り、変数startPlayerScore, otherPlayerScoreを定義する
                // 1. 「xxxオール」は、Intキャストしたxxxの値
                // 2. 「300500」（6桁以上の数値）は、半分に切った、前半と後半をそれぞれIntキャスト、
                // 3. 「1000」は、Intキャストしたその値
                // 4. それ以外の場合は、入力値が不正なので再度入力を促す
                if (startPlayerScore != null && otherPlayerScore != null) {
                    if (mainViewModel.isParent.value == true) {
                        if (mainViewModel.isDraw.value == true) {
                            mainViewModel.callAnswerApi(
                                context = this,
                                payForStartPlayer = 0,
                                payForOther = otherPlayerScore
                            )
                        } else {
                            mainViewModel.callAnswerApi(
                                context = this,
                                payForStartPlayer = 0,
                                payForOther = otherPlayerScore
                            )
                        }
                    } else {
                        if (mainViewModel.isDraw.value == true) {
                            mainViewModel.callAnswerApi(
                                context = this,
                                payForStartPlayer = startPlayerScore,
                                payForOther = otherPlayerScore
                            )
                        } else {
                            mainViewModel.callAnswerApi(
                                context = this,
                                payForStartPlayer = startPlayerScore,
                                payForOther = otherPlayerScore
                            )
                        }
                    }

                } else {
                    Log.e("Bad Request", "入力値が不正です。再度入力してください。")
                }
            } else {
                Log.e("Internal Error", "音声認識に失敗しました")
            }
        }

        setContent {
            WearApp(mainViewModel, speechRecognizerLauncher)
        }
    }
}

fun displaySpeechRecognizer(speechRecognizerLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toString())
    }
    speechRecognizerLauncher.launch(intent)
}
fun parseScore(input: String): Pair<Int?, Int?> {
    return when {
        input.endsWith("オール") -> {
            val score = input.removeSuffix("オール").toIntOrNull()
            if (score != null) Pair(score, score) else Pair(null, null)
        }
        input.length >= 6 && input.all { it.isDigit() } -> {
            val mid = input.length / 2
            val otherPlayerScore = input.substring(0, mid).toIntOrNull()
            val startPlayerScore = input.substring(mid).toIntOrNull()
            if (startPlayerScore != null && otherPlayerScore != null) Pair(startPlayerScore, otherPlayerScore) else Pair(null, null)
        }
        input.length >= 3 && input.all { it.isDigit() } -> {
            val score = input.toIntOrNull()
            if (score != null) Pair(score, score) else Pair(null, null)
        }
        else -> Pair(null, null)
    }
}

@Composable
fun WearApp(
    mainViewModel: MainViewModel,
    speechRecognizerLauncher: ActivityResultLauncher<Intent>?
) {
    val apiResponse by mainViewModel.apiResponse.observeAsState()
    val isParent by mainViewModel.isParent.observeAsState(false)
    val symbolCount by mainViewModel.symbolCount.observeAsState(0)
    val fanCount by mainViewModel.fanCount.observeAsState(0)
    val isDraw by mainViewModel.isDraw.observeAsState(false)
    val hasAnswer by mainViewModel.hasAnswer.observeAsState(false)

    MahjongQuizAppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column{
                TimeText()
            }
            if (!hasAnswer) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Question(
                        isParent,
                        symbolCount,
                        fanCount,
                        isDraw,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .height(64.dp)
                            .width(64.dp),
                        onClick = {
                            if (speechRecognizerLauncher != null) {
                                displaySpeechRecognizer(speechRecognizerLauncher)
                            }
                        },
                        content = {
                            Text("Answer")
                        },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    if (apiResponse != null) {
                        Text(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = if (apiResponse == true) MaterialTheme.colors.primary else MaterialTheme.colors.error,
                            text = if (apiResponse == true) "Success！" else "False..."
                        )
                    }
                    else {
                        Text(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = "Checking..."
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Question(
    isParent: Boolean,
    symbolCount: Int,
    fanCount: Int,
    isDraw: Boolean
) {
    // とりあえず愚直に書いていく
    Text(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = "${if (isParent) "親" else "子"} ${if (fanCount > 4) "" else "$symbolCount" + "符"}${fanCount}翻 ${if (isDraw) "ツモ" else "ロン"}"
    )
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true, apiLevel = 34)
@Composable
fun DefaultPreview() {
    val mainViewModel = MainViewModel()
    WearApp(mainViewModel, null)
}