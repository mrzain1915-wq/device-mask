package com.device.mask

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * Device Mask settings UI — fresh, professional Material 3 design.
 *
 * Dark navy header with the activation card, clean cards per section
 * (Device, Location, Network / SIM, eSIM, Privacy IDs), switches for
 * every feature, model/country pickers, a Random-all button and a
 * Diagnose screen. Free tier: only the first 10 models and first 10
 * countries are selectable; the rest are shown as locked.
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

private enum class Screen { Home, Diagnose }

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

    /** Random-all: new random free-tier model + country, fresh IDs. */
    fun randomAll() {
        selectedModel = Random.nextInt(Prefs.FREE_MODEL_COUNT)
        selectedCountry = Random.nextInt(Prefs.FREE_COUNTRY_COUNT)
        regenerateIds()
    }

    fun selectModel(index: Int) {
        if (index >= Prefs.FREE_MODEL_COUNT) return
        selectedModel = index
        regenerateIds()
    }

    fun selectCountry(index: Int) {
        if (index >= Prefs.FREE_COUNTRY_COUNT) return
        selectedCountry = index
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

    val state = androidx.compose.runtime.remember { MaskState(context) }
    var screen by mutableStateOf(Screen.Home)
    var refreshTick by mutableIntStateOf(0)
    // Reading refreshTick here subscribes this composable to it, so the
    // activation marker is re-read whenever Refresh is tapped.
    @Suppress("UNUSED_VARIABLE")
    val tick = refreshTick
    val lastActive = state.lastActiveTime()

    Column(modifier = Modifier.fillMaxSize().background(PageBackground)) {
        Header(
            active = isActiveThisBoot(lastActive),
            lastActiveText = lastActiveText(lastActive),
            onRefresh = { refreshTick++ },
            onDiagnose = { screen = Screen.Diagnose }
        )
        when (screen) {
            Screen.Home -> HomeScreen(state)
            Screen.Diagnose -> DiagnoseScreen(state) { screen = Screen.Home }
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
    onDiagnose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Navy)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Device Mask",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
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
    var showModelPicker by mutableStateOf(false)
    var showCountryPicker by mutableStateOf(false)

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
                text = "Free tier: the first ${Prefs.FREE_MODEL_COUNT} models and " +
                    "first ${Prefs.FREE_COUNTRY_COUNT} countries. The rest are " +
                    "locked.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Premium login, trials and redeem codes are not part of " +
                    "this build.",
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
                val locked = index >= Prefs.FREE_MODEL_COUNT
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
                val locked = index >= Prefs.FREE_COUNTRY_COUNT
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
