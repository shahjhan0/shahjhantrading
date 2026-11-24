package com.shahjhan.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF07111F)
private val Panel = Color(0xFF0D1B2D)
private val Panel2 = Color(0xFF122640)
private val Line = Color(0xFF263D59)
private val Muted = Color(0xFF91A7C2)
private val Green = Color(0xFF08BD67)
private val Red = Color(0xFFF0444E)
private val Gold = Color(0xFFF2C44E)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TerminalApp() }
    }
}

@Composable
fun TerminalApp() {
    var tab by remember { mutableStateOf("Chart") }
    var symbol by remember { mutableStateOf("XAUUSD") }
    var timeframe by remember { mutableStateOf("M1") }
    var lot by remember { mutableStateOf("0.01") }
    var sl by remember { mutableStateOf("200") }
    var tp by remember { mutableStateOf("200") }
    var botEnabled by remember { mutableStateOf(false) }
    var notice by remember { mutableStateOf("Paper mode · No broker connected") }

    MaterialTheme(colorScheme = darkColorScheme(
        background = Bg, surface = Panel, primary = Color(0xFF2878F0),
        onBackground = Color(0xFFE8F0FB), onSurface = Color(0xFFE8F0FB)
    )) {
        Column(Modifier.fillMaxSize().background(Bg)) {
            Row(
                Modifier.fillMaxWidth().background(Panel).padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("♛", color = Gold, fontSize = 24.sp)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text("SHAHJHAN", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("MOBILE TRADING TERMINAL", color = Muted, fontSize = 10.sp)
                }
                Text("PAPER DEMO", color = Gold, fontSize = 10.sp,
                    modifier = Modifier.background(Color(0xFF3A321B), RoundedCornerShape(6.dp)).padding(7.dp))
            }
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(8.dp)) {
                listOf("XAUUSD", "EURUSD", "USDJPY", "BTCUSD").forEach {
                    SmallChoice(it, symbol == it) { symbol = it }
                }
                Spacer(Modifier.width(8.dp))
                listOf("M1", "M5", "M15", "H1", "H4", "D1").forEach {
                    SmallChoice(it, timeframe == it) { timeframe = it }
                }
            }
            when (tab) {
                "Chart" -> ChartScreen(symbol, timeframe, lot, { lot = it }, sl, { sl = it }, tp, { tp = it }, notice,
                    { notice = it })
                "Bots" -> BotScreen(botEnabled, { botEnabled = it }, notice, { notice = it })
                "Trade" -> TradeScreen(notice)
                "Account" -> AccountScreen()
                "Settings" -> SettingsScreen()
            }
            Spacer(Modifier.weight(1f))
            NavigationBar(containerColor = Panel) {
                listOf("Chart", "Trade", "Bots", "Account", "Settings").forEach { item ->
                    NavigationBarItem(
                        selected = tab == item, onClick = { tab = item },
                        icon = { Text(when(item) { "Chart" -> "⌁"; "Trade" -> "⇄"; "Bots" -> "⚙"; "Account" -> "◉"; else -> "☰" }) },
                        label = { Text(item, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Gold,
                            selectedTextColor = Gold, indicatorColor = Panel2)
                    )
                }
            }
        }
    }
}

@Composable
fun SmallChoice(label: String, selected: Boolean, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.padding(end = 3.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (selected) Color(0xFF1B5CC5) else Panel,
            contentColor = Color.White
        )) { Text(label, fontSize = 11.sp) }
}

@Composable
fun ChartScreen(
    symbol: String, tf: String, lot: String, setLot: (String)->Unit,
    sl: String, setSl: (String)->Unit, tp: String, setTp: (String)->Unit,
    notice: String, setNotice: (String)->Unit
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 10.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("$symbol · $tf", fontWeight = FontWeight.Bold)
            Text("SIMULATED PRICE", color = Gold, fontSize = 10.sp)
        }
        Box(Modifier.fillMaxWidth().height(250.dp).background(Color(0xFF050B13), RoundedCornerShape(10.dp))) {
            CandleChart(Modifier.fillMaxSize().padding(8.dp))
            Text("Illustrative chart", color = Muted, fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomStart).padding(10.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TradeButton("SELL", "4023.45", Red, Modifier.weight(1f)) { setNotice("Paper SELL ticket preview created") }
            TradeButton("BUY", "4023.70", Green, Modifier.weight(1f)) { setNotice("Paper BUY ticket preview created") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            InputBox("Lot", lot, setLot, Modifier.weight(1f))
            InputBox("SL points", sl, setSl, Modifier.weight(1f))
            InputBox("TP points", tp, setTp, Modifier.weight(1f))
        }
        Text(notice, color = Muted, fontSize = 11.sp)
    }
}

@Composable
fun CandleChart(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val step = size.width / 34f
        for (i in 0..33) {
            val x = i * step + step / 2
            val base = size.height * .55f + kotlin.math.sin(i * .72).toFloat() * size.height * .17f
            val up = i % 3 != 0
            val color = if (up) Green else Red
            drawLine(color, Offset(x, base - 18), Offset(x, base + 20), 2.dp.toPx())
            drawRect(color, Offset(x - 4.dp.toPx(), base - 10), androidx.compose.ui.geometry.Size(8.dp.toPx(), 19.dp.toPx()))
        }
        for (i in 1..4) {
            val y = size.height * i / 5
            drawLine(Line, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
        }
    }
}

@Composable
fun TradeButton(label: String, quote: String, color: Color, modifier: Modifier, onClick: ()->Unit) {
    Button(onClick = onClick, modifier = modifier.height(58.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color), shape = RoundedCornerShape(9.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontWeight = FontWeight.Bold)
            Text(quote, fontSize = 12.sp)
        }
    }
}

@Composable
fun InputBox(label: String, value: String, onValue: (String)->Unit, modifier: Modifier) {
    Column(modifier.background(Panel, RoundedCornerShape(8.dp)).padding(8.dp)) {
        Text(label, color = Muted, fontSize = 10.sp)
        BasicTextField(value, onValue, singleLine = true, textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 15.sp))
    }
}

@Composable
fun BotScreen(enabled: Boolean, setEnabled: (Boolean)->Unit, notice: String, setNotice: (String)->Unit) {
    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Expert Advisor Manager", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Add bot file", color = Muted)
        Button(onClick = { setNotice("File picker integration is the next development step") }) { Text("＋ Select .mq5 / .ex5 file") }
        Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("Shahjhan EA profile", fontWeight = FontWeight.Bold)
                Text("File: Not selected", color = Muted, fontSize = 12.sp)
                Text("Symbol: Chart symbol · Inputs: To be connected", color = Muted, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Enable bot", modifier = Modifier.weight(1f))
                    Switch(checked = enabled, onCheckedChange = setEnabled)
                }
                Text(if (enabled) "Enabled in UI preview only" else "Bot stopped in UI preview", color = if (enabled) Green else Gold)
            }
        }
        Text("Bot settings", fontWeight = FontWeight.Bold)
        Text("Lot size · Grid gap · Max orders · Basket target · Daily loss limit · SL mode", color = Muted)
        Text(notice, color = Muted, fontSize = 11.sp)
        Text("Important: Android cannot execute desktop MT5 .ex5 files directly. A compatible trading engine and broker API are required.", color = Gold, fontSize = 12.sp)
    }
}

@Composable
fun TradeScreen(notice: String) {
    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Positions & Orders", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("No paper positions yet", color = Muted)
        Text("Trade buttons currently show ticket previews only. Order execution is not connected.", color = Gold, fontSize = 12.sp)
        Text(notice, color = Muted, fontSize = 11.sp)
    }
}
@Composable
fun AccountScreen() {
    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Account overview", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Metric("Balance", "$5,000.00")
        Metric("Equity", "$5,000.00")
        Metric("Free margin", "—")
        Text("Demo placeholders · No broker account connected", color = Gold, fontSize = 12.sp)
    }
}
@Composable
fun Metric(name: String, value: String) {
    Row(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(8.dp)).padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween) { Text(name, color = Muted); Text(value, fontWeight = FontWeight.Bold) }
}
@Composable
fun SettingsScreen() {
    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Terminal settings", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Theme: Dark", color = Muted)
        Text("Connection: Not configured", color = Muted)
        Text("Trading mode: Paper UI prototype", color = Gold)
    }
}
