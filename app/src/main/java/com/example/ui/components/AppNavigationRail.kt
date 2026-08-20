package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.viewmodel.AppScreen

@Composable
fun AppSidebar(
    currentScreen: AppScreen,
    currentUser: UserEntity?,
    onNavigate: (AppScreen) -> Unit,
    onLogout: () -> Unit,
    onSwitchRole: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(260.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // App Branding Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ONLINE TEST SYSTEM",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Milliy Sertifikat",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                }

                // Nav Items List
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .weight(1f, fill = false)
                ) {
                    NavItem(
                        icon = Icons.Default.Home,
                        label = "Bosh sahifa",
                        isSelected = currentScreen == AppScreen.HOME,
                        onClick = { onNavigate(AppScreen.HOME) },
                        testTag = "nav_home"
                    )

                    NavItem(
                        icon = Icons.Default.Assignment,
                        label = "Mening testlarim",
                        isSelected = currentScreen == AppScreen.MY_TESTS,
                        onClick = { onNavigate(AppScreen.MY_TESTS) },
                        testTag = "nav_my_tests"
                    )

                    NavItem(
                        icon = Icons.Default.CheckCircle,
                        label = "Natijalarim",
                        isSelected = currentScreen == AppScreen.MY_RESULTS,
                        onClick = { onNavigate(AppScreen.MY_RESULTS) },
                        testTag = "nav_results"
                    )

                    NavItem(
                        icon = Icons.Default.CardMembership,
                        label = "Sertifikatlarim",
                        isSelected = currentScreen == AppScreen.MY_CERTIFICATES,
                        onClick = { onNavigate(AppScreen.MY_CERTIFICATES) },
                        testTag = "nav_certificates"
                    )

                    NavItem(
                        icon = Icons.Default.BarChart,
                        label = "Statistika",
                        isSelected = currentScreen == AppScreen.STATISTICS,
                        onClick = { onNavigate(AppScreen.STATISTICS) },
                        testTag = "nav_statistics"
                    )

                    NavItem(
                        icon = Icons.Default.Settings,
                        label = "SetTab (Sozlamalar)",
                        isSelected = currentScreen == AppScreen.SETTINGS_SET_TAB,
                        onClick = { onNavigate(AppScreen.SETTINGS_SET_TAB) },
                        testTag = "nav_settings"
                    )

                    NavItem(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        label = "Yordam",
                        isSelected = currentScreen == AppScreen.HELP,
                        onClick = { onNavigate(AppScreen.HELP) },
                        testTag = "nav_help"
                    )

                    NavItem(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        label = "Chiqish",
                        isSelected = false,
                        onClick = onLogout,
                        testTag = "nav_logout"
                    )
                }
            }

            // User Info Card at Bottom
            if (currentUser != null) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(bottom = 12.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BluePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser.fullName.take(1).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentUser.fullName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (currentUser.role == UserRole.TEACHER_CREATOR) "O'qituvchi (Admin)" else "Talabgor (O'quvchi)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BluePrimary,
                                    fontSize = 11.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable { onSwitchRole() }
                                    .testTag("switch_role_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Rolni o'zgartirish",
                                    tint = Slate700,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val bgColor = if (isSelected) BluePrimary else Color.Transparent
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = bgColor,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = contentColor
            )
        }
    }
}

@Composable
fun AppMobileBottomBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.HOME,
            onClick = { onNavigate(AppScreen.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Bosh sahifa") },
            label = { Text("Bosh sahifa", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.MY_TESTS,
            onClick = { onNavigate(AppScreen.MY_TESTS) },
            icon = { Icon(Icons.Default.Assignment, contentDescription = "Testlar") },
            label = { Text("Testlar", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.MY_CERTIFICATES,
            onClick = { onNavigate(AppScreen.MY_CERTIFICATES) },
            icon = { Icon(Icons.Default.CardMembership, contentDescription = "Sertifikat") },
            label = { Text("Sertifikat", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.STATISTICS,
            onClick = { onNavigate(AppScreen.STATISTICS) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Statistika") },
            label = { Text("Statistika", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.SETTINGS_SET_TAB,
            onClick = { onNavigate(AppScreen.SETTINGS_SET_TAB) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "SetTab") },
            label = { Text("SetTab", fontSize = 11.sp) }
        )
    }
}

