package com.example.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.BlueDark
import com.example.ui.theme.BlueLight
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RedContainer
import com.example.ui.theme.RedError
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.AppUiSettings

@Composable
fun SetTabScreen(
    currentUser: UserEntity?,
    settings: AppUiSettings,
    onUpdateProfile: (name: String, email: String, phone: String) -> Unit,
    onUpdateSettings: (isDark: Boolean, showRes: Boolean, allowRev: Boolean, autoSave: Boolean, lang: String) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Umumiy sozlamalar", "Bildirishnomalar", "Maxfiylik", "Til")

    // Profile inputs
    var fullName by remember(currentUser) { mutableStateOf(currentUser?.fullName ?: "Azizbek Abduqodirov") }
    var email by remember(currentUser) { mutableStateOf(currentUser?.email ?: "example@gmail.com") }
    var phone by remember(currentUser) { mutableStateOf(currentUser?.phone ?: "+998 90 123 45 67") }

    // Notifications state
    var notifyEmail by remember { mutableStateOf(true) }
    var notifyNewTests by remember { mutableStateOf(true) }
    var notifyCertReady by remember { mutableStateOf(true) }
    var notifyExamReminder by remember { mutableStateOf(true) }

    // Password inputs
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var twoFactorAuth by remember { mutableStateOf(false) }

    // Language
    var selectedLang by remember(settings.language) { mutableStateOf(settings.language) }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp)
                .padding(16.dp)
                .testTag("set_tab_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Header
        item {
            Column {
                Text(
                    text = "Sozlamalar (SetTab)",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Profilingiz va tizim parametrlarini to'liq boshqaring",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Subtabs (Umumiy sozlamalar, Bildirishnomalar, Maxfiylik, Til)
        item {
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedTab = index }
                            .testTag("settings_tab_$index"),
                        color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                        shadowElevation = if (isSelected) 2.dp else 0.dp
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // TAB 0: Umumiy sozlamalar
                // Section 1: Profil ma'lumotlari
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Foydalanuvchi ma'lumotlari",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Rol: ${if (currentUser?.role == UserRole.TEACHER_CREATOR) "O'qituvchi / Test yaratuvchi" else "Talabgor / O'quvchi"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("To'liq ism (F.I.SH)") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("settings_fullname_input"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email manzil") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("settings_email_input"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Telefon raqami") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("settings_phone_input"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    onUpdateProfile(fullName, email, phone)
                                    Toast.makeText(context, "Profil muvaffaqiyatli saqlandi!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("save_profile_button")
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ma'lumotlarni saqlash")
                            }
                        }
                    }
                }

                // Section 2: Tizim sozlamalari (Dark mode, auto-save, results)
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Tizim va test rejimlari",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                SettingSwitchRow(
                                    title = "Qorong'i mavzu (Dark Mode)",
                                    subtitle = "Tizim ko'rinishini qorong'i rejimga o'tkazish",
                                    checked = settings.isDarkMode,
                                    onCheckedChange = { isDark ->
                                        onUpdateSettings(
                                            isDark,
                                            settings.showResultAfterTest,
                                            settings.allowRevisitQuestions,
                                            settings.autoSaveAnswers,
                                            settings.language
                                        )
                                    }
                                )

                                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                                SettingSwitchRow(
                                    title = "Test tugagach natijani ko'rsatish",
                                    subtitle = "Test yakunlangach darhol to'g'ri/noto'g'ri javoblarni chiqarish",
                                    checked = settings.showResultAfterTest,
                                    onCheckedChange = { showRes ->
                                        onUpdateSettings(
                                            settings.isDarkMode,
                                            showRes,
                                            settings.allowRevisitQuestions,
                                            settings.autoSaveAnswers,
                                            settings.language
                                        )
                                    }
                                )

                                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                                SettingSwitchRow(
                                    title = "Savollarga qaytish imkoniyati",
                                    subtitle = "Oldingi savollarga qaytib javoblarni o'zgartirish",
                                    checked = settings.allowRevisitQuestions,
                                    onCheckedChange = { allowRev ->
                                        onUpdateSettings(
                                            settings.isDarkMode,
                                            settings.showResultAfterTest,
                                            allowRev,
                                            settings.autoSaveAnswers,
                                            settings.language
                                        )
                                    }
                                )

                                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                                SettingSwitchRow(
                                    title = "Avtomatik saqlash",
                                    subtitle = "Har bir javob va vaqt avtomatik tarzda saqlanadi",
                                    checked = settings.autoSaveAnswers,
                                    onCheckedChange = { autoSave ->
                                        onUpdateSettings(
                                            settings.isDarkMode,
                                            settings.showResultAfterTest,
                                            settings.allowRevisitQuestions,
                                            autoSave,
                                            settings.language
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // TAB 1: Bildirishnomalar (Notifications)
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Bildirishnoma sozlamalari",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                SettingSwitchRow(
                                    title = "Email xabarnomalar",
                                    subtitle = "Test natijalari va sertifikatlar emailga yuboriladi",
                                    checked = notifyEmail,
                                    onCheckedChange = { notifyEmail = it }
                                )

                                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                                SettingSwitchRow(
                                    title = "Yangi testlar haqida ogohlantirish",
                                    subtitle = "Yangi fan testlari ochilganda xabar berish",
                                    checked = notifyNewTests,
                                    onCheckedChange = { notifyNewTests = it }
                                )

                                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                                SettingSwitchRow(
                                    title = "Sertifikat tayyor bo'lganda bildirish",
                                    subtitle = "Rasch modeli bo'yicha baholash yakunlangach ogohlantirish",
                                    checked = notifyCertReady,
                                    onCheckedChange = { notifyCertReady = it }
                                )

                                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                                SettingSwitchRow(
                                    title = "Test muddati eslatmasi",
                                    subtitle = "Test yakunlanishiga 15 daqiqa qolganda eslatish",
                                    checked = notifyExamReminder,
                                    onCheckedChange = { notifyExamReminder = it }
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // TAB 2: Maxfiylik va Xavfsizlik
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Parolni o'zgartirish",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = currentPassword,
                                onValueChange = { currentPassword = it },
                                label = { Text("Joriy parol") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("Yangi parol") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Yangi parolni tasdiqlang") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (newPassword.isNotBlank() && newPassword == confirmPassword) {
                                        currentPassword = ""
                                        newPassword = ""
                                        confirmPassword = ""
                                        Toast.makeText(context, "Parol muvaffaqiyatli yangilandi!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Yangi parollar bir-biriga mos kelmadi!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Parolni yangilash")
                            }
                        }
                    }
                }

                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Qo'shimcha xavfsizlik",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            SettingSwitchRow(
                                title = "2 bosqichli autentifikatsiya (2FA)",
                                subtitle = "Har safar kirishda SMS orqali tasdiqlash",
                                checked = twoFactorAuth,
                                onCheckedChange = {
                                    twoFactorAuth = it
                                    Toast.makeText(context, if (it) "2FA faollashtirildi" else "2FA o'chirildi", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }

                // Section 4: Hisobni o'chirish (Danger zone)
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = RedContainer.copy(alpha = 0.3f),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = RedError)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Hisobni boshqarish (Xavfli zona)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = RedError
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Hisobingizni o'chirish barcha ma'lumotlar, yaratilgan testlar va olingan sertifikatlarni butunlay yo'q qiladi.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { showDeleteConfirmDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = RedError),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("delete_account_button")
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Hisobni o'chirish")
                            }
                        }
                    }
                }
            }

            3 -> {
                // TAB 3: Til va Mintaqa
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Tizim tili",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            val languages = listOf(
                                "uz" to "O'zbekcha (Lotin)",
                                "uz_cyrl" to "Ўзбекча (Кирилл)",
                                "ru" to "Русский язык",
                                "en" to "English (US)"
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                languages.forEach { (code, name) ->
                                    val isSelected = selectedLang == code
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                selectedLang = code
                                                onUpdateSettings(
                                                    settings.isDarkMode,
                                                    settings.showResultAfterTest,
                                                    settings.allowRevisitQuestions,
                                                    settings.autoSaveAnswers,
                                                    code
                                                )
                                                Toast.makeText(context, "Til o'zgartirildi: $name", Toast.LENGTH_SHORT).show()
                                            },
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = name,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Divider(color = MaterialTheme.colorScheme.outlineVariant)

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Vaqt mintaqasi: Toshkent (UTC+05:00)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sana formati: DD.MM.YYYY (masalan: 19.08.2026)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Hisobni o'chirishni tasdiqlaysizmi?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Ushbu amal qaytarilmaydi. Barcha testlar va sertifikatlar o'chiriladi.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedError)
                ) {
                    Text("Ha, o'chirish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Bekor qilish")
                }
            }
        )
    }
}
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
