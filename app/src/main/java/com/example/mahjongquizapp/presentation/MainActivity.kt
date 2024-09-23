/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.mahjongquizapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.mahjongquizapp.presentation.theme.MahjongQuizAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    val isParent = (0..1).random() == 1
    val symbolCount = (2..11).random() * 10
    val fanCount = (1..13).random()
    val isDraw = (0..1).random() == 1
    val selectableItems = getSelectableItems(isParent, isDraw)
    MahjongQuizAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                TimeText()
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                ScalingLazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item { ListHeader { Question(
                        isParent,
                        symbolCount,
                        fanCount,
                        isDraw
                    ) } }
                    items(selectableItems.size) { index ->
                        Chip(
                            onClick = {},
                            label = { Text(selectableItems[index].label) },
                            colors = ChipDefaults.secondaryChipColors()
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

fun getSelectableItems(
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
                SelectableItem("1500", 1500, 1500),
                SelectableItem("2000", 2000, 2000),
                SelectableItem("2400", 2400, 2400),
                SelectableItem("2900", 2900, 2900),
                SelectableItem("3400", 3400, 3400),
                SelectableItem("3900", 3900, 3900),
                SelectableItem("4400", 4400, 4400),
                SelectableItem("4800", 4800, 4800),
                SelectableItem("5300", 5300, 5300),
                SelectableItem("5800", 5800, 5800),
                SelectableItem("6800", 6800, 6800),
                SelectableItem("7200", 7200, 7200),
                SelectableItem("8700", 8700, 8700),
                SelectableItem("9600", 9600, 9600),
                SelectableItem("10600", 10600, 10600),
                SelectableItem("11600", 11600, 11600),
                SelectableItem("12000", 12000, 12000),
                SelectableItem("18000", 18000, 18000),
                SelectableItem("24000", 24000, 24000),
                SelectableItem("36000", 36000, 36000),
                SelectableItem("48000", 48000, 48000)
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

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}