package com.device.mask

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random
import kotlinx.coroutines.launch

/**
 * Device Mask settings UI — fresh, professional Material 3 design.
 *
 * Dark navy header with the activation card, clean cards per section
 * (Device, Location, Network / SIM, eSIM, Privacy IDs), switches for
 * every feature, model/country pickers, a Random-all button, a
 * Diagnose screen with a diagnostics log, and a side menu (Home,
 * Account, Premium, Diagnose, About).
 *
 * Free tier: only the first 10 models and first 10 countries are
 * selectable; the rest are shown as locked. Premium unlocks all models
 * and countries and is activated with a redeem code on the Premium
 * screen, tied to the logged-in account (AccountStore).
 *
 * The app refuses to open if its signing certificate does not match
 * the pinned debug key (Prefs.EXPECTED_SIGNATURE_SHA256).
 */

private val Navy = Color(0xFF0F1B2D)
private val NavyLight = Color(0xFF1B2E47)
private val Teal = Color(0xFF00BFA6)
private val TealDark = Color(0xFF00897B)
private val Amber = Color(0xFFFFB300)
private val Green = Color(0xFF43A047)
private val Red = Color(0xFFE53935)
private val PageBackground = Color(0xFFF2F4F7)
private val Muted = Color(0xFF5A6577)

private enum class Screen { Home, Diagnose, Account, Premium, About }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DeviceMaskApp()
                }
            }
        }
    }
}

/** Holds all settings state and persists it to SharedPreferences. */
private class MaskState(context: Context) {
    private val prefs =
        context.getSharedPreferences(Prefs.FILE_NAME, Context.MODE_PRIVATE)
    private val appContext = context.applicationContext

    /** Premium is active when the logged-in account redeemed a code. */
    fun isPremium(): Boolean = AccountStore.isPremium(appContext)

    var deviceOn by mutableStateOf(
        prefs.getBoolean(Prefs.KEY_DEVICE_ENABLED, Prefs.DEFAULT_DEVICE_ENABLED)
    )
    var locationOn by mutableStateOf(
        prefs.getBoolean(Prefs.KEY_LOCATION_ENABLED, Prefs.DEFAULT_LOCATION_ENABLED)
    )
    var networkOn by mutableStateOf(
        prefs.getBoolean(Prefs.KEY_NETWORK_ENABLED, Prefs.DEFAULT_NETWORK_ENABLED)
    )
    var eidOn by mutableStateOf(
        prefs.getBoolean(Prefs.KEY_EID_ENABLED, Prefs.DEFAULT_EID_ENABLED)
    )
    var esimOn by mutableStateOf(
        prefs.getBoolean(Prefs.KEY_ESIM_ENABLED, Prefs.DEFAULT_ESIM_ENABLED)
    )
    var imeiOn by mutableStateOf(
        prefs.getBoolean(Prefs.KEY_IMEI_ENABLED, Prefs.DEFAULT_IMEI_ENABLED)
    )
    var macOn by mutableStateOf(
        prefs.getBoolean(Prefs.KEY_MAC_ENABLED, Prefs.DEFAULT_MAC_ENABLED)
    )
    var userAgentOn by mutableStateOf(
        prefs.getBoolean(Prefs.KEY_USER_AGENT_ENABLED, Prefs.DEFAULT_USER_AGENT_ENABLED)
    )
    var selectedModel by mutableIntStateOf(
        prefs.getInt(Prefs.KEY_SELECTED_MODEL, Prefs.DEFAULT_SELECTED_MODEL)
            .coerceIn(DEVICE_MODELS.indices)
    )
    var selectedCountry by mutableIntStateOf(
        prefs.getInt(Prefs.KEY_SELECTED_COUNTRY, Prefs.DEFAULT_SELECTED_COUNTRY)
            .coerceIn(COUNTRIES.indices)
    )
    var imei by mutableStateOf(
        prefs.getString(Prefs.KEY_IMEI, "") ?: ""
    )
    var mac by mutableStateOf(
        prefs.getString(Prefs.KEY_MAC, "") ?: ""
    )
    var eid by mutableStateOf(
        prefs.getString(Prefs.KEY_EID, "") ?: ""
    )
    var userAgent by mutableStateOf(
        prefs.getString(Prefs.KEY_USER_AGENT, "") ?: ""
    )
    var iccid by mutableStateOf(
        prefs.getString(Prefs.KEY_ICCID, "") ?: ""
    )
    var imsi by mutableStateOf(
        prefs.getString(Prefs.KEY_IMSI, "") ?: ""
    )
    var phone by mutableStateOf(
        prefs.getString(Prefs.KEY_PHONE, "") ?: ""
    )

    val model: DeviceModel get() = DEVICE_MODELS[selectedModel]
    val country: Country get() = COUNTRIES[selectedCountry]

    fun save() {
        prefs.edit()
            .putBoolean(Prefs.KEY_DEVICE_ENABLED, deviceOn)
            .putBoolean(Prefs.KEY_LOCATION_ENABLED, locationOn)
            .putBoolean(Prefs.KEY_NETWORK_ENABLED, networkOn)
            .putBoolean(Prefs.KEY_EID_ENABLED, eidOn)
            .putBoolean(Prefs.KEY_ESIM_ENABLED, esimOn)
            .putBoolean(Prefs.KEY_IMEI_ENABLED, imeiOn)
            .putBoolean(Prefs.KEY_MAC_ENABLED, macOn)
            .putBoolean(Prefs.KEY_USER_AGENT_ENABLED, userAgentOn)
            .putInt(Prefs.KEY_SELECTED_MODEL, selectedModel)
            .putInt(Prefs.KEY_SELECTED_COUNTRY, selectedCountry)
            .putString(Prefs.KEY_IMEI, imei)
            .putString(Prefs.KEY_MAC, mac)
            .putString(Prefs.KEY_EID, eid)
            .putString(Prefs.KEY_USER_AGENT, userAgent)
            .putString(Prefs.KEY_ICCID, iccid)
            .putString(Prefs.KEY_IMSI, imsi)
            .putString(Prefs.KEY_PHONE, phone)
            .apply()
        DiagLog.log(appContext, "Settings saved")
    }

    /** (Re)generate the identity values for the current selection. */
    fun regenerateIds(random: Random = Random.Default) {
        imei = generateImei(random)
        mac = generateMac(random)
        eid = generateEid(random)
        userAgent = defaultUserAgent(model)
        iccid = generateIccid(country, random)
        imsi = generateImsi(country, random)
        phone = generatePhone(country, random)
        save()
    }

    /** Random-all: new random model + country (all of them on Premium), fresh IDs. */
    fun randomAll() {
        val modelCount = if (isPremium()) DEVICE_MODELS.size else Prefs.FREE_MODEL_COUNT
        val countryCount = if (isPremium()) COUNTRIES.size else Prefs.FREE_COUNTRY_COUNT
        selectedModel = Random.nextInt(modelCount)
        selectedCountry = Random.nextInt(countryCount)
        regenerateIds()
    }

    fun selectModel(index: Int) {
        if (!isPremium() && index >= Prefs.FREE_MODEL_COUNT) return
        selectedModel = index
        DiagLog.log(appContext, "Model changed to ${model.brand} ${model.modelName}")
        regenerateIds()
    }

    fun selectCountry(index: Int) {
        if (!isPremium() && index >= Prefs.FREE_COUNTRY_COUNT) return
        selectedCountry = index
        DiagLog.log(appContext, "Country changed to ${country.name} (${country.iso})")
        regenerateIds()
    }

    /** Last time the hook proved it ran, 0 if it never ran. */
    fun lastActiveTime(): Long =
        prefs.getLong(Prefs.KEY_LAST_ACTIVE_TIME, 0L)
}

@Composable
private fun DeviceMaskApp() {
    val context = LocalContext.current
    val signatureOk = SignatureCheck.isSignatureValid(context)
    if (!signatureOk) {
        SignatureRefusedScreen()
        return
    }

    val state = remember { MaskState(context) }
    // v1.0.27 fix: these MUST be wrapped in remember. Without it, every
    // recomposition re-created them, so tapping Diagnose set the screen
    // to Diagnose and the very next recomposition reset it to Home —
    // the Diagnose button did nothing.
    var screen by remember { mutableStateOf(Screen.Home) }
    var refreshTick by remember { mutableIntStateOf(0) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Log app start once, when the app first composes.
    LaunchedEffect(Unit) {
        DiagLog.log(context, "App started")
    }

    // Re-read the activation marker whenever the app comes back to the
    // foreground, so the card never shows a stale "inactive" after a
    // ticked app ran. Uses Application.ActivityLifecycleCallbacks so no
    // extra lifecycle dependency is needed.
    DisposableEffect(context.applicationContext) {
        val app = context.applicationContext as? Application
        val callbacks = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                refreshTick++
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        }
        app?.registerActivityLifecycleCallbacks(callbacks)
        onDispose { app?.unregisterActivityLifecycleCallbacks(callbacks) }
    }

    // Reading refreshTick here subscribes this composable to it, so the
    // activation marker is re-read whenever Refresh is tapped or the
    // app resumes.
    @Suppress("UNUSED_VARIABLE")
    val tick = refreshTick
    val lastActive = state.lastActiveTime()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Device Mask",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = screen == Screen.Home,
                    onClick = {
                        screen = Screen.Home
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Account") },
                    selected = screen == Screen.Account,
                    onClick = {
                        screen = Screen.Account
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Premium") },
                    selected = screen == Screen.Premium,
                    onClick = {
                        screen = Screen.Premium
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Diagnose") },
                    selected = screen == Screen.Diagnose,
                    onClick = {
                        screen = Screen.Diagnose
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("About") },
                    selected = screen == Screen.About,
                    onClick = {
                        screen = Screen.About
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().background(PageBackground)) {
            Header(
                active = isActiveThisBoot(lastActive),
                lastActiveText = lastActiveText(lastActive),
                onRefresh = {
                    refreshTick++
                    DiagLog.log(context, "Refresh tapped")
                },
                onDiagnose = { screen = Screen.Diagnose },
                onMenu = { scope.launch { drawerState.open() } }
            )
            when (screen) {
                Screen.Home -> HomeScreen(state)
                Screen.Diagnose -> DiagnoseScreen(state) { screen = Screen.Home }
                Screen.Account -> AccountScreen()
                Screen.Premium -> PremiumScreen()
                Screen.About -> AboutScreen()
            }
        }
    }
}

/** True if the hook wrote its marker after this boot started. */
private fun isActiveThisBoot(lastActive: Long): Boolean {
    if (lastActive <= 0L) return false
    val bootWallTime = System.currentTimeMillis() - SystemClock.elapsedRealtime()
    return lastActive >= bootWallTime
}

private fun lastActiveText(lastActive: Long): String {
    if (lastActive <= 0L) return "Never — the module has not run yet."
    val format = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return "Last ran: ${format.format(Date(lastActive))}"
}

/** LSPosed Manager version, if the manager app is installed. */
@Suppress("DEPRECATION")
private fun lsposedManagerVersion(context: Context): String {
    return try {
        val info = context.packageManager.getPackageInfo("org.lsposed.manager", 0)
        info.versionName ?: "installed (unknown version)"
    } catch (e: Throwable) {
        "not installed"
    }
}

/** This app's own version, for the Diagnose screen. */
@Suppress("DEPRECATION")
private fun appVersion(context: Context): String {
    return try {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        info.versionName ?: "unknown"
    } catch (e: Throwable) {
        "unknown"
    }
}

@Composable
private fun SignatureRefusedScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Signature mismatch",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Red
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "This copy of Device Mask was not signed with the " +
                "project key, so it refuses to open. Install the " +
                "release APK instead.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun Header(
    active: Boolean,
    lastActiveText: String,
    onRefresh: () -> Unit,
    onDiagnose: () -> Unit,
    onMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Navy)
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onMenu) {
                    Text(text = "Menu", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "Device Mask",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Text(
                text = "Per-app device identity, location & network — display-only",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFB0BEC5)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (active) Color(0xFF1B5E20) else Color(0xFF7F0000)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (active) "Activated" else "Module is inactive",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (active) {
                                "The module ran in a target app this boot."
                            } else {
                                "Enable Device Mask in LSPosed and reboot."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE0E0E0)
                        )
                        Text(
                            text = lastActiveText,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB0BEC5)
                        )
                    }
                    TextButton(onClick = onRefresh) {
                        Text(text = "Refresh", color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onDiagnose,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Diagnose", color = Color.White)
            }
        }
    }
}

@Composable
private fun HomeScreen(state: MaskState) {
    // Same remember bug as DeviceMaskApp had: without remember, a
    // recomposition resets these to false and the picker never opens.
    var showModelPicker by remember { mutableStateOf(false) }
    var showCountryPicker by remember { mutableStateOf(false) }
    val premium = state.isPremium()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Button(
            onClick = { state.randomAll() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Teal,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text(
                text = "Random all — new model, country & IDs",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        SectionCard(title = "Device") {
            ToggleRow(
                title = "Spoof device model",
                subtitle = "Model, manufacturer, brand, fingerprint & codename.",
                checked = state.deviceOn,
                onCheckedChange = { state.deviceOn = it; state.save() }
            )
            PickerRow(
                label = "Model",
                value = "${state.model.brand} ${state.model.modelName}",
                onClick = { showModelPicker = true }
            )
            ValueRow("Codename", state.model.codename)
            ValueRow("Fingerprint", state.model.fingerprint)
        }

        SectionCard(title = "Location") {
            ToggleRow(
                title = "Spoof location & country",
                subtitle = "GPS coordinates, country, timezone & locale follow the country you pick.",
                checked = state.locationOn,
                onCheckedChange = { state.locationOn = it; state.save() }
            )
            PickerRow(
                label = "Country",
                value = "${state.country.name} (${state.country.iso})",
                onClick = { showCountryPicker = true }
            )
            ValueRow("Timezone", state.country.timezone)
            ValueRow(
                "GPS",
                "${state.country.latitude}, ${state.country.longitude}"
            )
        }

        SectionCard(title = "Network / SIM") {
            ToggleRow(
                title = "Spoof network & SIM",
                subtitle = "Operator, MCC/MNC, SIM serial, IMSI & phone number.",
                checked = state.networkOn,
                onCheckedChange = { state.networkOn = it; state.save() }
            )
            ValueRow("Operator", state.country.operator)
            ValueRow("MCC / MNC", "${state.country.mcc} / ${state.country.mnc}")
            ValueRow("SIM serial (ICCID)", state.iccid.ifEmpty { "—" })
            ValueRow("IMSI", state.imsi.ifEmpty { "—" })
            ValueRow("Phone number", state.phone.ifEmpty { "—" })
        }

        SectionCard(title = "eSIM") {
            ToggleRow(
                title = "Enable eSIM support",
                subtitle = "Apps that check eSIM support see it as supported.",
                checked = state.esimOn,
                onCheckedChange = { state.esimOn = it; state.save() }
            )
            ToggleRow(
                title = "Spoof EID",
                subtitle = "The eSIM identifier shown to apps.",
                checked = state.eidOn,
                onCheckedChange = { state.eidOn = it; state.save() }
            )
            ValueRow("EID", state.eid.ifEmpty { "—" })
        }

        SectionCard(title = "Privacy IDs") {
            ToggleRow(
                title = "Spoof IMEI",
                subtitle = "The device identifier shown to apps.",
                checked = state.imeiOn,
                onCheckedChange = { state.imeiOn = it; state.save() }
            )
            ValueRow("IMEI", state.imei.ifEmpty { "—" })
            ToggleRow(
                title = "Spoof MAC address",
                subtitle = "The Wi-Fi hardware address shown to apps.",
                checked = state.macOn,
                onCheckedChange = { state.macOn = it; state.save() }
            )
            ValueRow("MAC", state.mac.ifEmpty { "—" })
            ToggleRow(
                title = "Spoof WebView user-agent",
                subtitle = "The browser string shown by WebViews.",
                checked = state.userAgentOn,
                onCheckedChange = { state.userAgentOn = it; state.save() }
            )
            ValueRow("User-agent", state.userAgent.ifEmpty { "—" })
        }

        SectionCard(title = "Premium") {
            Text(
                text = if (premium) {
                    "Premium active — all ${DEVICE_MODELS.size} models and " +
                        "${COUNTRIES.size} countries unlocked."
                } else {
                    "Free tier: the first ${Prefs.FREE_MODEL_COUNT} models and " +
                        "first ${Prefs.FREE_COUNTRY_COUNT} countries. The rest are " +
                        "locked."
                },
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (premium) {
                    "Premium is active on your account."
                } else {
                    "Open Premium in the side menu to unlock everything " +
                        "with a redeem code."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
        }

        SectionCard(title = "How to use it") {
            Text(
                text = "1. Install this APK.\n" +
                    "2. In LSPosed / Vector Manager, enable Device Mask and tick the apps it should run in.\n" +
                    "3. Reboot your phone.\n" +
                    "4. Open Device Mask, pick a model and a country, then force-stop the target app and re-check it.\n" +
                    "Switch changes take effect without another reboot; a model or country change needs the target app restarted.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = "Device Mask — display-only. The chosen app sees the spoofed " +
                "values; your real device, SIM and eSIM stay exactly as they are. " +
                "Do not use it to misrepresent a device to a service that " +
                "requires a genuine attestation.",
            style = MaterialTheme.typography.bodySmall,
            color = Muted
        )
        Spacer(modifier = Modifier.height(8.dp))
    }

    if (showModelPicker) {
        PickerDialog(
            title = "Choose a model",
            onDismiss = { showModelPicker = false }
        ) {
            itemsIndexed(DEVICE_MODELS) { index, model ->
                val locked = !premium && index >= Prefs.FREE_MODEL_COUNT
                PickerItem(
                    title = "${model.brand} ${model.modelName}",
                    subtitle = model.codename,
                    locked = locked,
                    selected = index == state.selectedModel,
                    onClick = {
                        state.selectModel(index)
                        showModelPicker = false
                    }
                )
            }
        }
    }

    if (showCountryPicker) {
        PickerDialog(
            title = "Choose a country",
            onDismiss = { showCountryPicker = false }
        ) {
            itemsIndexed(COUNTRIES) { index, country ->
                val locked = !premium && index >= Prefs.FREE_COUNTRY_COUNT
                PickerItem(
                    title = "${country.name} (${country.iso})",
                    subtitle = "${country.dialCode} · ${country.operator} · ${country.timezone}",
                    locked = locked,
                    selected = index == state.selectedCountry,
                    onClick = {
                        state.selectCountry(index)
                        showCountryPicker = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DiagnoseScreen(state: MaskState, onBack: () -> Unit) {
    val active = isActiveThisBoot(state.lastActiveTime())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedButton(onClick = onBack) {
            Text(text = "Back")
        }
        Text(
            text = "Diagnose",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        SectionCard(title = "Activation") {
            ValueRow(
                "Module status",
                if (active) "Activated this boot" else "Inactive"
            )
            ValueRow("Last ran", lastActiveText(state.lastActiveTime()))
        }
        SectionCard(title = "Device & framework") {
            val context = LocalContext.current
            ValueRow(
                "Android",
                "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
            )
            ValueRow("LSPosed Manager", lsposedManagerVersion(context))
            ValueRow("App version", appVersion(context))
        }
        SectionCard(title = "Toggles") {
            ValueRow("Device", if (state.deviceOn) "ON" else "OFF")
            ValueRow("Location", if (state.locationOn) "ON" else "OFF")
            ValueRow("Network / SIM", if (state.networkOn) "ON" else "OFF")
            ValueRow("EID", if (state.eidOn) "ON" else "OFF")
            ValueRow("eSIM support", if (state.esimOn) "ON" else "OFF")
            ValueRow("IMEI", if (state.imeiOn) "ON" else "OFF")
            ValueRow("MAC", if (state.macOn) "ON" else "OFF")
            ValueRow("WebView user-agent", if (state.userAgentOn) "ON" else "OFF")
        }
        SectionCard(title = "Values apps will see") {
            ValueRow("Model", "${state.model.brand} ${state.model.modelName}")
            ValueRow("Manufacturer", state.model.manufacturer)
            ValueRow("Codename", state.model.codename)
            ValueRow("Fingerprint", state.model.fingerprint)
            ValueRow("Country", "${state.country.name} (${state.country.iso})")
            ValueRow("Timezone", state.country.timezone)
            ValueRow(
                "GPS",
                "${state.country.latitude}, ${state.country.longitude}"
            )
            ValueRow("Operator", state.country.operator)
            ValueRow("MCC / MNC", "${state.country.mcc} / ${state.country.mnc}")
            ValueRow("SIM serial (ICCID)", state.iccid.ifEmpty { "—" })
            ValueRow("IMSI", state.imsi.ifEmpty { "—" })
            ValueRow("Phone number", state.phone.ifEmpty { "—" })
            ValueRow("EID", state.eid.ifEmpty { "—" })
            ValueRow("eSIM support", if (state.esimOn) "supported" else "real value")
            ValueRow("IMEI", state.imei.ifEmpty { "—" })
            ValueRow("MAC", state.mac.ifEmpty { "—" })
            ValueRow("User-agent", state.userAgent.ifEmpty { "—" })
        }
        SectionCard(title = "Log") {
            val context = LocalContext.current
            var logLines by remember { mutableStateOf(DiagLog.read(context)) }
            OutlinedButton(onClick = {
                DiagLog.clear(context)
                logLines = emptyList()
            }) {
                Text(text = "Clear log")
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (logLines.isEmpty()) {
                Text(
                    text = "No log entries yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
            } else {
                Column {
                    logLines.forEach { line ->
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        Text(
            text = "If an app still shows the real values, force-stop it and " +
                "open it again — hooks apply when the app starts. Also check " +
                "that Device Mask is enabled for that app in LSPosed.",
            style = MaterialTheme.typography.bodySmall,
            color = Muted
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun AccountScreen() {
    val context = LocalContext.current
    var email by remember {
        mutableStateOf(AccountStore.loggedInEmail(context) ?: "")
    }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var loggedIn by remember { mutableStateOf(AccountStore.loggedInEmail(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Account",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        SectionCard(title = "Login") {
            Text(
                text = if (loggedIn != null) {
                    "Logged in as $loggedIn"
                } else {
                    "Not logged in"
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(onClick = {
                    if (!email.contains("@") || password.length < 4) {
                        message = "Enter a valid email and a password of at " +
                            "least 4 characters."
                    } else {
                        AccountStore.login(context, email, password)
                        loggedIn = AccountStore.loggedInEmail(context)
                        password = ""
                        message = "Logged in as $loggedIn."
                        DiagLog.log(context, "Logged in as $loggedIn")
                    }
                }) {
                    Text(text = "Login")
                }
                OutlinedButton(onClick = {
                    AccountStore.logout(context)
                    loggedIn = null
                    password = ""
                    message = "Logged out."
                    DiagLog.log(context, "Logged out")
                }) {
                    Text(text = "Logout")
                }
            }
            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = message, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Text(
            text = "Your account is stored only on this phone. The password " +
                "is stored only as a hash, never as plain text. Logging out " +
                "clears the account and its premium.",
            style = MaterialTheme.typography.bodySmall,
            color = Muted
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun PremiumScreen() {
    val context = LocalContext.current
    var code by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var premium by remember { mutableStateOf(AccountStore.isPremium(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Premium",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        SectionCard(title = "Status") {
            Text(
                text = if (premium) "Premium active" else "Free version",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (premium) {
                    "Premium is active on " +
                        "${AccountStore.premiumEmail(context) ?: "your account"}."
                } else {
                    "Free tier: first ${Prefs.FREE_MODEL_COUNT} models and " +
                        "first ${Prefs.FREE_COUNTRY_COUNT} countries. Premium " +
                        "unlocks all ${DEVICE_MODELS.size} models and " +
                        "${COUNTRIES.size} countries."
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
        SectionCard(title = "Redeem code") {
            Text(
                text = "Enter a redeem code from the owner to unlock Premium " +
                    "on your account.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Redeem code (DM-XXXX-XXXX)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = {
                val email = AccountStore.loggedInEmail(context)
                if (email == null) {
                    message = "Log in first on the Account screen."
                } else if (AccountStore.redeem(context, code)) {
                    premium = true
                    message = "Premium activated for $email."
                    DiagLog.log(context, "Premium activated for $email")
                } else {
                    message = "Invalid code."
                }
            }) {
                Text(text = "Redeem")
            }
            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = message, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Text(
            text = "Redeem codes are given out by the owner. A code can only " +
                "be used while logged in, and premium stays on that account.",
            style = MaterialTheme.typography.bodySmall,
            color = Muted
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun AboutScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "About",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        SectionCard(title = "Device Mask") {
            ValueRow("Version", appVersion(context))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Free version: first ${Prefs.FREE_MODEL_COUNT} models " +
                    "and first ${Prefs.FREE_COUNTRY_COUNT} countries. Premium " +
                    "unlocks all models and countries.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Device Mask is display-only: the apps you choose see " +
                    "the spoofed values; your real device, SIM and eSIM stay " +
                    "exactly as they are.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Navy
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Muted
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun PickerRow(label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Muted)
            Text(text = value, fontWeight = FontWeight.Bold)
        }
        Text(text = "Change", color = TealDark, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ValueRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Muted)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun PickerDialog(
    title: String,
    onDismiss: () -> Unit,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                content()
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(text = "Close") }
        }
    )
}

@Composable
private fun PickerItem(
    title: String,
    subtitle: String,
    locked: Boolean,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !locked, onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (locked) Muted else Color.Black
            )
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Muted)
        }
        when {
            locked -> Text(
                text = "LOCKED",
                color = Amber,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            selected -> Text(text = "Selected", color = TealDark, fontWeight = FontWeight.Bold)
        }
    }
}
