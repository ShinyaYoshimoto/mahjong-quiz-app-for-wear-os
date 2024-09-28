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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
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

    private val _selectableItems = MutableLiveData<Array<SelectableItem>>()
    val selectableItems: LiveData<Array<SelectableItem>> = _selectableItems

    init {
        generateNewQuiz()
    }

    private fun generateNewQuiz() {
        _isParent.value = (0..1).random() == 1
        _symbolCount.value = (2..11).random() * 10
        _fanCount.value = (1..13).random()
        _isDraw.value = (0..1).random() == 1
        _selectableItems.value = getSelectableItems(_isParent.value!!, _isDraw.value!!)
    }

    fun callAnswerApi(
        context: Context,
        payForStartPlayer: Int,
        payForOther: Int
    ) {
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

    private fun getSelectableItems(
        isParent: Boolean,
        isDraw: Boolean
    ): Array<SelectableItem> {
        return if (isDraw) {
            if (isParent) {
                arrayOf(
                    SelectableItem("500all", 0, 500),
                    SelectableItem("700all", 0, 700),
                    SelectableItem("800all", 0, 800),
                    SelectableItem("1000all", 0, 1000),
                    SelectableItem("1200all", 0, 1200),
                    SelectableItem("1300all", 0, 1300),
                    SelectableItem("1500all", 0, 1500),
                    SelectableItem("1600all", 0, 1600),
                    SelectableItem("1800all", 0, 1800),
                    SelectableItem("2000all", 0, 2000),
                    SelectableItem("2300all", 0, 2300),
                    SelectableItem("2600all", 0, 2600),
                    SelectableItem("2900all", 0, 2900),
                    SelectableItem("3200all", 0, 3200),
                    SelectableItem("3600all", 0, 3600),
                    SelectableItem("3900all", 0, 3900),
                    SelectableItem("4000all", 0, 4000),
                    SelectableItem("6000all", 0, 6000),
                    SelectableItem("8000all", 0, 8000),
                    SelectableItem("12000all", 0, 12000),
                    SelectableItem("16000all", 0, 16000)
                )
            } else {
                arrayOf(
                    SelectableItem("300, 500", 500, 300),
                    SelectableItem("400, 700", 700, 400),
                    SelectableItem("400, 800", 800, 400),
                    SelectableItem("500, 1000", 1000, 500),
                    SelectableItem("600, 1200", 1200, 600),
                    SelectableItem("700, 1300", 1300, 700),
                    SelectableItem("800, 1500", 1500, 800),
                    SelectableItem("800, 1600", 1600, 800),
                    SelectableItem("900, 1800", 1800, 900),
                    SelectableItem("1000, 2000", 2000, 1000),
                    SelectableItem("1200, 2300", 2300, 1200),
                    SelectableItem("1300, 2600", 2600, 1300),
                    SelectableItem("1500, 2900", 2900, 1500),
                    SelectableItem("1600, 3200", 3200, 1600),
                    SelectableItem("1800, 3600", 3600, 1800),
                    SelectableItem("2000, 3900", 3900, 2000),
                    SelectableItem("2000, 4000", 4000, 2000),
                    SelectableItem("3000, 6000", 6000, 3000),
                    SelectableItem("4000, 8000", 8000, 4000),
                    SelectableItem("6000, 12000", 12000, 6000),
                    SelectableItem("8000, 16000", 16000, 8000)
                )
            }
        } else {
            if (isParent) {
                arrayOf(
                    SelectableItem("1500", 0, 1500),
                    SelectableItem("2000", 0, 2000),
                    SelectableItem("2400", 0, 2400),
                    SelectableItem("2900", 0, 2900),
                    SelectableItem("3400", 0, 3400),
                    SelectableItem("3900", 0, 3900),
                    SelectableItem("4400", 0, 4400),
                    SelectableItem("4800", 0, 4800),
                    SelectableItem("5300", 0, 5300),
                    SelectableItem("5800", 0, 5800),
                    SelectableItem("6800", 0, 6800),
                    SelectableItem("7200", 0, 7200),
                    SelectableItem("8700", 0, 8700),
                    SelectableItem("9600", 0, 9600),
                    SelectableItem("10600", 0, 10600),
                    SelectableItem("11600", 0, 11600),
                    SelectableItem("12000", 0, 12000),
                    SelectableItem("18000", 0, 18000),
                    SelectableItem("24000", 0, 24000),
                    SelectableItem("36000", 0, 36000),
                    SelectableItem("48000", 0, 48000)
                )
            } else {
                arrayOf(
                    SelectableItem("1000", 1000, 1000),
                    SelectableItem("1300", 1300, 1300),
                    SelectableItem("1600", 1600, 1600),
                    SelectableItem("2000", 2000, 2000),
                    SelectableItem("2300", 2300, 2300),
                    SelectableItem("2600", 2600, 2600),
                    SelectableItem("2900", 2900, 2900),
                    SelectableItem("3200", 3200, 3200),
                    SelectableItem("3600", 3600, 3600),
                    SelectableItem("3900", 3900, 3900),
                    SelectableItem("4500", 4500, 4500),
                    SelectableItem("5200", 5200, 5200),
                    SelectableItem("5800", 5800, 5800),
                    SelectableItem("6400", 6400, 6400),
                    SelectableItem("7100", 7100, 7100),
                    SelectableItem("7700", 7700, 7700),
                    SelectableItem("8000", 8000, 8000),
                    SelectableItem("12000", 12000, 12000),
                    SelectableItem("16000", 16000, 16000),
                    SelectableItem("24000", 24000, 24000),
                    SelectableItem("32000", 32000, 32000)
                )
            }
        }
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
                    Log.d("aaa", "入力値が不正です。再度入力してください。")
                }
            } else {
//                Toast.makeText(this, "音声認識に失敗しました", Toast.LENGTH_SHORT).show()
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
    val context = LocalContext.current
    val apiResponse by mainViewModel.apiResponse.observeAsState()
    val isParent by mainViewModel.isParent.observeAsState(false)
    val symbolCount by mainViewModel.symbolCount.observeAsState(0)
    val fanCount by mainViewModel.fanCount.observeAsState(0)
    val isDraw by mainViewModel.isDraw.observeAsState(false)
    val selectableItems by mainViewModel.selectableItems.observeAsState(emptyArray())

    MahjongQuizAppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column{
                TimeText()
            }
            if (apiResponse == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    if (speechRecognizerLauncher !== null) {
                        Question(
                            isParent,
                            symbolCount,
                            fanCount,
                            isDraw,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                displaySpeechRecognizer(speechRecognizerLauncher)
                            },
                            content = {
                                Text("答える")
                            },
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    } else {
                        ScalingLazyColumn(modifier = Modifier.fillMaxWidth()) {
                            item {
                                ListHeader {
                                    Question(
                                        isParent,
                                        symbolCount,
                                        fanCount,
                                        isDraw
                                    )
                                }
                            }
                            items(selectableItems.size) { index ->
                                Chip(
                                    onClick = {
                                        mainViewModel.callAnswerApi(
                                            context,
                                            payForStartPlayer = selectableItems[index].payForStartPlayer,
                                            payForOther = selectableItems[index].payForOther
                                        )
                                    },
                                    label = { Text(selectableItems[index].label) },
                                    colors = ChipDefaults.secondaryChipColors()
                                )
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = if (apiResponse == true) MaterialTheme.colors.primary else MaterialTheme.colors.error,
                        text = if (apiResponse == true) "正解！" else "不正解"
                    )
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
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = "${if (isParent) "親" else "子"} ${if (fanCount > 4) "" else "$symbolCount" + "符"}${fanCount}翻 ${if (isDraw) "ツモ" else "ロン"}"
    )
}

data class SelectableItem (
    val label: String,
    val payForStartPlayer: Int,
    val payForOther: Int,
)



@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    val mainViewModel = MainViewModel()
    WearApp(mainViewModel, null)
}