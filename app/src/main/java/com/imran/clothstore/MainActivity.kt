package com.imran.clothstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.imran.clothstore.navigation.AppNavGraph
import com.imran.clothstore.navigation.Routes
import com.imran.clothstore.ui.components.BottomNavBar
import com.imran.clothstore.ui.components.BottomNavTab
import com.imran.clothstore.ui.screens.menu.SideMenu
import com.imran.clothstore.ui.theme.ImranClothStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImranClothStoreTheme {
                AppRoot()
            }
        }
    }
}

/**
 * রুট Composable — NavHost-এর সাথে নিচে স্থায়ী BottomNavBar (ড্যাশবোর্ড/হোম) বসানো।
 * ওয়েব অ্যাপে এই বার সবসময় দৃশ্যমান থাকে; নেটিভ অ্যাপে আগে অনুপস্থিত ছিল।
 * ড্যাশবোর্ড ও হোম রুটেই শুধু বার দেখানো হয় — সাব-স্ক্রিন (fv_list, detail, ইত্যাদি)
 * খোলা থাকলে ওয়েব অ্যাপের আচরণ অনুসরণ করে বার লুকানো হয়।
 */
@Composable
private fun AppRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var menuOpen by remember { mutableStateOf(false) }

    AppRootContent(
        currentRoute = currentRoute,
        menuOpen = menuOpen,
        onMenuClose = { menuOpen = false },
        onMenuHome = {
            menuOpen = false
            if (currentRoute != Routes.HOME) {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = false }
                    launchSingleTop = true
                }
            }
        },
        onNavigate = { tab ->
            val target = if (tab == BottomNavTab.DASHBOARD) Routes.DASHBOARD else Routes.HOME
            if (target != currentRoute) {
                navController.navigate(target) {
                    popUpTo(Routes.HOME) { inclusive = false }
                    launchSingleTop = true
                }
            }
        },
        content = { modifier ->
            AppNavGraph(
                navController = navController,
                modifier = modifier,
                onMenuClick = { menuOpen = true }
            )
        }
    )
}

@Composable
private fun AppRootContent(
    currentRoute: String?,
    menuOpen: Boolean,
    onMenuClose: () -> Unit,
    onMenuHome: () -> Unit,
    onNavigate: (BottomNavTab) -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    val showBottomBar = currentRoute == Routes.HOME || currentRoute == Routes.DASHBOARD

    // Column-এর বদলে Box — বার কনটেন্টের উপরে ভাসে (overlay), তাই আধা-স্বচ্ছ ব্যাকগ্রাউন্ডের
    // মধ্য দিয়ে নিচের কার্ড দেখা যায়। কনটেন্ট পুরো স্ক্রিন জুড়ে থাকে।
    Box(modifier = Modifier.fillMaxSize()) {
        content(Modifier.fillMaxSize())
        if (showBottomBar) {
            BottomNavBar(
                selected = if (currentRoute == Routes.DASHBOARD) BottomNavTab.DASHBOARD else BottomNavTab.HOME,
                onSelect = onNavigate,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        // সাইড ড্রয়ার — সবার উপরে (বটম বারেরও উপরে), স্ক্রিনের অর্ধেক প্রস্থ
        SideMenu(
            visible = menuOpen,
            onClose = onMenuClose,
            onNavigateHome = onMenuHome
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppRootPreview() {
    ImranClothStoreTheme {
        AppRootContent(
            currentRoute = Routes.HOME,
            menuOpen = false,
            onMenuClose = {},
            onMenuHome = {},
            onNavigate = {},
            content = { modifier ->
                Box(
                    modifier = modifier,
                    contentAlignment = Alignment.Center
                ) {
                    Text("Preview Content")
                }
            }
        )
    }
}

