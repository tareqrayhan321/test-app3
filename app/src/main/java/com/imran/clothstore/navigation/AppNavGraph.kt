package com.imran.clothstore.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.imran.clothstore.data.model.Entry
import com.imran.clothstore.data.model.EntryCategory
import com.imran.clothstore.ui.screens.dbconnect.DbConnectModal
import com.imran.clothstore.ui.screens.debtors.DebtorsListScreen
import com.imran.clothstore.ui.screens.detail.DetailScreen
import com.imran.clothstore.ui.screens.fvlist.FvListScreen
import com.imran.clothstore.ui.screens.home.HomeScreen
import com.imran.clothstore.ui.screens.menu.SideMenu
import com.imran.clothstore.ui.screens.notif.NotificationPanel
import com.imran.clothstore.ui.screens.notif.NotificationViewModel
import com.imran.clothstore.ui.screens.reports.AggregateReportOverlay
import com.imran.clothstore.ui.screens.reports.NetProfitBreakdownOverlay
import com.imran.clothstore.ui.screens.reports.PLTrendsOverlay
import com.imran.clothstore.ui.screens.reports.WeeklyReportsViewModel

/**
 * অ্যাপের নেভিগেশন গ্রাফ। ওয়েব অ্যাপে যেভাবে JS দিয়ে screen show/hide (fvOpen, fvOpenDetail,
 * partyOpen, aggOverlayOpen, showNetProfitBreakdown, openPLTrends, toggleMenu, notifOpen, dbOpen
 * ইত্যাদি) হতো, এখানে Jetpack Navigation Compose দিয়ে route-ভিত্তিক নেভিগেশন করা হচ্ছে।
 */
object Routes {
    const val HOME = "home"
    const val DASHBOARD = "dashboard"
    const val FV_LIST = "fv_list/{categoryName}"
    const val DETAIL = "detail/{categoryName}/{entryId}"
    const val DEBTORS_LIST = "debtors_list" // ওয়েব অ্যাপের partyOpen() / dlScreen এর সমতুল্য
    const val AGGREGATE_REPORT = "aggregate_report"
    const val NET_PROFIT_BREAKDOWN = "net_profit_breakdown"
    const val PL_TRENDS = "pl_trends"
    const val PAIKKARI_CALCULATOR = "paikkari_calculator"
    const val FABRIC = "fabric"
    const val DB_CONNECT = "db_connect"
    const val SIDE_MENU = "side_menu"
    const val NOTIFICATION_PANEL = "notification_panel"

    fun fvList(category: EntryCategory) = "fv_list/${category.name}"
    fun detail(category: EntryCategory, entryId: Long) = "detail/${category.name}/$entryId"
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    // Aggregate Report, Net Profit Breakdown, P&L Trends সবাই একই weekly_reports ডেটা পড়ে,
    // তাই NavGraph-স্কোপড শেয়ার্ড ViewModel ব্যবহার করা হচ্ছে (একাধিকবার Firestore listener না খোলার জন্য)।
    val reportsViewModel: WeeklyReportsViewModel = viewModel()
    val notifViewModel: NotificationViewModel = viewModel()
    val homeViewModel: com.imran.clothstore.ui.screens.home.HomeViewModel = viewModel()

    // ডিফল্ট NavHost ট্রানজিশন ~৭০০ms ফেড — প্রতিটা ট্যাপ ধীর মনে হতো, তাই তাৎক্ষণিক করা হলো
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {

        composable(Routes.HOME) {
            HomeScreen(
                onCardClick = { category ->
                    // ওয়েব অ্যাপে "পার্টি" কার্ড partyOpen() দিয়ে Debtors List (Party Ledger) খোলে,
                    // বাকি কার্ডগুলো fvOpen() দিয়ে সাধারণ এন্ট্রি লিস্ট খোলে।
                    if (category == EntryCategory.PARTY) {
                        navController.navigate(Routes.DEBTORS_LIST)
                    } else {
                        navController.navigate(Routes.fvList(category))
                    }
                },
                onNotifClick = { navController.navigate(Routes.NOTIFICATION_PANEL) },
                onMenuClick = { navController.navigate(Routes.SIDE_MENU) },
                onAggregateReportClick = { navController.navigate(Routes.AGGREGATE_REPORT) },
                onPLTrendsClick = { navController.navigate(Routes.PL_TRENDS) },
                onFabricClick = { navController.navigate(Routes.FABRIC) },
                onPartyClick = { navController.navigate(Routes.DEBTORS_LIST) },
                onDbConnectClick = { navController.navigate(Routes.DB_CONNECT) },
                viewModel = homeViewModel,
                notifViewModel = notifViewModel
            )
        }

        composable(Routes.DASHBOARD) {
            com.imran.clothstore.ui.screens.dashboard.DashboardScreen(
                onNotifClick = { navController.navigate(Routes.NOTIFICATION_PANEL) },
                onMenuClick = { navController.navigate(Routes.SIDE_MENU) },
                onNetProfitClick = { navController.navigate(Routes.NET_PROFIT_BREAKDOWN) },
                onAggregateReportClick = { navController.navigate(Routes.AGGREGATE_REPORT) },
                onPaikkariClick = { navController.navigate(Routes.PAIKKARI_CALCULATOR) },
                reportsViewModel = reportsViewModel,
                notifViewModel = notifViewModel
            )
        }

        composable(
            route = Routes.FV_LIST,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: return@composable
            val category = EntryCategory.valueOf(categoryName)
            val viewModel = viewModel<com.imran.clothstore.ui.screens.fvlist.FvListViewModel>(
                factory = FvListViewModelFactory(category)
            )

            FvListScreen(
                category = category,
                onBack = { navController.popBackStack() },
                onEntryClick = { entry ->
                    NavEntryHolder.set(entry)
                    navController.navigate(Routes.detail(category, entry.id))
                },
                viewModel = viewModel
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("categoryName") { type = NavType.StringType },
                navArgument("entryId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: return@composable
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: return@composable
            val category = EntryCategory.valueOf(categoryName)
            val initialEntry = NavEntryHolder.consume() ?: Entry(id = entryId)

            val viewModel = viewModel<com.imran.clothstore.ui.screens.detail.DetailViewModel>(
                factory = DetailViewModelFactory(category, entryId, initialEntry)
            )

            DetailScreen(
                category = category,
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(Routes.DEBTORS_LIST) {
            DebtorsListScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.AGGREGATE_REPORT) {
            AggregateReportOverlay(
                onClose = { navController.popBackStack() },
                viewModel = reportsViewModel
            )
        }

        composable(Routes.NET_PROFIT_BREAKDOWN) {
            NetProfitBreakdownOverlay(
                onClose = { navController.popBackStack() },
                viewModel = reportsViewModel
            )
        }

        composable(Routes.PL_TRENDS) {
            PLTrendsOverlay(
                onClose = { navController.popBackStack() },
                viewModel = reportsViewModel
            )
        }

        composable(Routes.PAIKKARI_CALCULATOR) {
            com.imran.clothstore.ui.screens.paikkari.PaikkariCalculatorScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.FABRIC) {
            com.imran.clothstore.ui.screens.fabric.FabricScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.DB_CONNECT) {
            DbConnectModal(
                onClose = { navController.popBackStack() }
            )
        }

        composable(Routes.SIDE_MENU) {
            SideMenu(
                onClose = { navController.popBackStack() },
                onNavigateHome = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }

        composable(Routes.NOTIFICATION_PANEL) {
            NotificationPanel(
                onClose = { navController.popBackStack() },
                viewModel = notifViewModel
            )
        }
    }
}
