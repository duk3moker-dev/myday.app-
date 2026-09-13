package com.myday.app

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Pink = Color(0xFFE92F82)
private val PinkDark = Color(0xFF8B164F)
private val Cream = Color(0xFFFFF8FB)
private val Cursive = FontFamily.Cursive

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MyDayApp() }
    }
}

@Composable
fun MyDayApp() {
    var screen by remember { mutableStateOf("home") }
    var month by remember { mutableStateOf(YearMonth.of(2026, 12)) }
    var selected by remember { mutableStateOf(LocalDate.of(2026, 12, 1)) }
    var tasks by remember { mutableStateOf(listOf("Planejar meu dia", "Estudar", "Momento de descanso")) }
    var photo by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("myday", Context.MODE_PRIVATE) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { photo = it.toString(); prefs.edit().putString("photo", it.toString()).apply() }
    }
    LaunchedEffect(Unit) { photo = prefs.getString("photo", null) }

    MaterialTheme(colorScheme = lightColorScheme(primary = Pink, background = Cream)) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFFFD7E7), Cream)))) {
            Scaffold(containerColor = Color.Transparent,
                topBar = { TopBar(onHome = { screen = "home" }, onProfile = { screen = "profile" }, photo = photo) },
                bottomBar = { BottomBar(screen) { screen = it } }
            ) { pad ->
                Box(Modifier.padding(pad).fillMaxSize()) {
                    when (screen) {
                        "home" -> HomeScreen(onCalendar = { screen = "calendar" }, onFinal = { screen = "final" })
                        "calendar" -> CalendarScreen(month, selected, { month = it; selected = it.atDay(1) }, { selected = it })
                        "tasks" -> TasksScreen(tasks, { tasks = tasks + "Nova tarefa" })
                        "profile" -> ProfileScreen(photo, { picker.launch("image/*") }, { screen = "final" })
                        "final" -> FinalScreen()
                        else -> HomeScreen({ screen = "calendar" }, { screen = "final" })
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(onHome: () -> Unit, onProfile: () -> Unit, photo: String?) {
    Surface(color = Color.White.copy(alpha = .88f), tonalElevation = 0.dp) {
        Row(Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = onHome) { Icon(Icons.Default.Menu, "Menu", tint = Pink) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("MyDay♥", fontFamily = Cursive, fontSize = 35.sp, fontWeight = FontWeight.Bold, color = Pink)
                Text("Seu dia, do seu jeito.", fontSize = 9.sp, color = PinkDark)
            }
            IconButton(onClick = onProfile) {
                if (photo != null) AsyncImage(model = Uri.parse(photo), contentDescription = "Perfil", modifier = Modifier.size(40.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                else Icon(Icons.Default.AccountCircle, "Perfil", tint = Pink, modifier = Modifier.size(38.dp))
            }
        }
    }
}

@Composable
private fun BottomBar(screen: String, go: (String) -> Unit) {
    NavigationBar(containerColor = Color.White.copy(.94f)) {
        NavigationBarItem(screen == "home", { go("home") }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Início") })
        NavigationBarItem(screen == "calendar", { go("calendar") }, icon = { Icon(Icons.Default.DateRange, null) }, label = { Text("Calendário") })
        NavigationBarItem(screen == "tasks", { go("tasks") }, icon = { Icon(Icons.Default.CheckCircle, null) }, label = { Text("Tarefas") })
        NavigationBarItem(screen == "profile", { go("profile") }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Ajustes") })
    }
}

@Composable
private fun HomeScreen(onCalendar: () -> Unit, onFinal: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(15.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(shape = RoundedCornerShape(30.dp), colors = CardDefaults.cardColors(containerColor = PinkDark), modifier = Modifier.fillMaxWidth().height(390.dp)) {
                Box {
                    Image(painterResource(R.drawable.zero_two_01), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, PinkDark.copy(.85f)))))
                    Column(Modifier.align(Alignment.BottomStart).padding(22.dp)) {
                        Text("MyDay", fontFamily = Cursive, fontSize = 62.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Seu dia, do seu jeito.", fontFamily = Cursive, fontSize = 24.sp, color = Color.White)
                        Spacer(Modifier.height(10.dp)); Button(onClick = onCalendar) { Text("Começar →", fontFamily = Cursive, fontSize = 20.sp) }
                    }
                }
            }
        }
        item { Text("Bem-vindo(a)! ✨", fontFamily = Cursive, fontSize = 28.sp, color = PinkDark); Text("Organize seu dia com carinho.", fontSize = 12.sp, color = PinkDark) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { QuickCard("▦", "Calendário", onCalendar, Modifier.weight(1f)); QuickCard("✓", "Tarefas", { }, Modifier.weight(1f)) } }
        item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { QuickCard("★", "Metas", { }, Modifier.weight(1f)); QuickCard("✎", "Anotações", { }, Modifier.weight(1f)) } }
        item { OutlinedButton(onClick = onFinal, modifier = Modifier.fillMaxWidth()) { Text("Ver tela final do MyDay", fontFamily = Cursive, fontSize = 19.sp) } }
    }
}

@Composable
private fun QuickCard(icon: String, title: String, click: () -> Unit, modifier: Modifier) {
    Card(onClick = click, modifier = modifier.height(112.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(.94f)), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(14.dp)) { Text(icon, fontSize = 25.sp, color = Pink); Text(title, fontFamily = Cursive, fontSize = 22.sp, color = PinkDark); Text("Organize seu dia", fontSize = 10.sp, color = Color.Gray) }
    }
}

@Composable
private fun CalendarScreen(month: YearMonth, selected: LocalDate, changeMonth: (YearMonth) -> Unit, select: (LocalDate) -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale("pt", "BR"))
    val first = month.atDay(1); val offset = (first.dayOfWeek.value % 7); val total = month.lengthOfMonth()
    LazyColumn(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = { changeMonth(month.minusMonths(1)) }) { Icon(Icons.Default.ChevronLeft, null, tint = Pink) }
                Text(formatter.format(first).replaceFirstChar { it.uppercase() }, fontFamily = Cursive, fontSize = 31.sp, color = Pink)
                IconButton(onClick = { changeMonth(month.plusMonths(1)) }) { Icon(Icons.Default.ChevronRight, null, tint = Pink) }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(.9f)), shape = RoundedCornerShape(25.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) { listOf("Dom","Seg","Ter","Qua","Qui","Sex","Sáb").forEach { Text(it, fontFamily = Cursive, fontSize = 16.sp, color = Pink) } }
                    Spacer(Modifier.height(8.dp))
                    for (row in 0..5) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        for (col in 0..6) { val n = row * 7 + col - offset + 1; if (n in 1..total) {
                            val d = month.atDay(n); val isSel = d == selected
                            Box(Modifier.size(43.dp).clip(RoundedCornerShape(14.dp)).background(if (isSel) Pink else Color.Transparent).clickable { select(d) }, contentAlignment = Alignment.Center) { Text(n.toString(), fontFamily = Cursive, fontSize = 18.sp, color = if (isSel) Color.White else PinkDark) }
                        } else Spacer(Modifier.size(43.dp)) }
                    }
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(.94f)), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.padding(16.dp)) { Text("Hoje, ${selected.dayOfMonth} de ${selected.month.getDisplayName(java.time.format.TextStyle.FULL, Locale("pt","BR"))}", fontFamily = Cursive, fontSize = 25.sp, color = PinkDark); Spacer(Modifier.height(6.dp)); Text("Nenhum compromisso para este dia", fontSize = 13.sp, color = Color.Gray); Spacer(Modifier.height(8.dp)); Button(onClick = {}) { Text("+ Adicionar", fontFamily = Cursive, fontSize = 18.sp) } }
            }
        }
    }
}

@Composable
private fun TasksScreen(tasks: List<String>, add: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(15.dp)) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text("Tarefas", fontFamily = Cursive, fontSize = 35.sp, color = PinkDark); Button(onClick = add) { Text("+ Adicionar") } }; Spacer(Modifier.height(12.dp)); tasks.forEach { t -> Card(Modifier.fillMaxWidth().padding(vertical = 5.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(.94f))) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Checkbox(false, {}); Text(t, fontFamily = Cursive, fontSize = 20.sp, color = PinkDark) } } } }
}

@Composable
private fun ProfileScreen(photo: String?, pick: () -> Unit, final: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Meu Perfil", fontFamily = Cursive, fontSize = 36.sp, color = PinkDark); Spacer(Modifier.height(18.dp))
        if (photo != null) AsyncImage(model = Uri.parse(photo), null, Modifier.size(120.dp).clip(CircleShape), contentScale = ContentScale.Crop) else Icon(Icons.Default.AccountCircle, null, tint = Pink, modifier = Modifier.size(120.dp))
        Spacer(Modifier.height(10.dp)); Button(onClick = pick) { Text("Trocar foto", fontFamily = Cursive, fontSize = 18.sp) }
        Spacer(Modifier.height(22.dp)); Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White.copy(.94f))) { Column(Modifier.padding(18.dp)) { Text("Usuário MyDay", fontFamily = Cursive, fontSize = 28.sp, color = PinkDark); Text("Seus dados ficam no próprio aparelho nesta versão.", fontSize = 12.sp, color = Color.Gray) } }
        Spacer(Modifier.height(12.dp)); OutlinedButton(onClick = final) { Text("Tela final") }
    }
}

@Composable
private fun FinalScreen() {
    Column(Modifier.fillMaxSize().background(Color.Black), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Image(painterResource(R.drawable.zero_two_final), "Zero Two", Modifier.fillMaxWidth().weight(1f), contentScale = ContentScale.Crop)
        Text("MyDay", fontFamily = Cursive, fontSize = 38.sp, color = Color.White, modifier = Modifier.padding(10.dp))
    }
}
