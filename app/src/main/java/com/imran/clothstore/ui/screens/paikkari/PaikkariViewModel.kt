package com.imran.clothstore.ui.screens.paikkari

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imran.clothstore.data.backup.BackupRepository
import com.imran.clothstore.data.backup.toBackupWeeklyReport
import com.imran.clothstore.data.model.WeeklyReport
import com.imran.clothstore.util.formatTaka
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

/** কোন পেজ দেখানো হচ্ছে — page-calc / page-result */
enum class PkPage { CALC, RESULT }

/**
 * পাইকারি ক্যালকুলেটরের ViewModel — ওয়েব অ্যাপের PK অবজেক্ট (window.PK) এর সমতুল্য।
 * ৩-ধাপের ইনপুট ফর্ম, calculate(), এবং সেভ (saveToChart()) সব এখানে।
 * হিসাব শেষে chart_data ও profitStripData_v1 উভয়ই imran_store/backup-এ আপডেট হয়।
 */
class PaikkariViewModel(
    private val repository: BackupRepository = BackupRepository()
) : ViewModel() {

    private val _input = MutableStateFlow(PaikkariInput())
    val input: StateFlow<PaikkariInput> = _input.asStateFlow()

    private val _currentPage = MutableStateFlow(PkPage.CALC)
    val currentPage: StateFlow<PkPage> = _currentPage.asStateFlow()

    private val _result = MutableStateFlow<PaikkariResult?>(null)
    val result: StateFlow<PaikkariResult?> = _result.asStateFlow()

    private val _validationError = MutableStateFlow<ValidationError?>(null)
    val validationError: StateFlow<ValidationError?> = _validationError.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _savedMessage = MutableStateFlow<String?>(null)
    val savedMessage: StateFlow<String?> = _savedMessage.asStateFlow()

    fun updateInput(transform: (PaikkariInput) -> PaikkariInput) {
        _input.value = transform(_input.value)
    }

    val estimatedProfit: Double
        get() = _input.value.soldGaj * _input.value.profitGaj

    val totalSaleAuto: Double
        get() = _input.value.cashSale + _input.value.creditSale

    val weeklyFixedHint: WeeklyFixed
        get() = computeWeeklyFixed(_input.value)

    /** calculate() বাটনের সমতুল্য — validate করে, ঠিক থাকলে হিসাব করে রেজাল্ট পেজে যায়, সাথে সাথে সেভও করে */
    fun calculate() {
        val error = validatePaikkariInput(_input.value)
        if (error != null) {
            _validationError.value = error
            return
        }
        _validationError.value = null
        val computed = computePaikkariResult(_input.value)
        _result.value = computed
        _currentPage.value = PkPage.RESULT
        saveToChart(computed)
    }

    /** saveToChart() এর সমতুল্য — হিসাব করা মাত্র সাথে সাথে chart_data ও profitStripData_v1 আপডেট হয় */
    private fun saveToChart(result: PaikkariResult) {
        _isSaving.value = true
        val cal = Calendar.getInstance(Locale.getDefault())
        val (year, week) = currentYearAndWeek()
        val label = currentWeekLabel()
        val i = _input.value

        val report = WeeklyReport(
            id = System.currentTimeMillis(),
            ts = System.currentTimeMillis(),
            label = label,
            year = year,
            week = week,
            month = cal.get(Calendar.MONTH),
            sales = result.totalSale,
            cogs = result.cogs,
            gross = result.gross,
            opex = result.opex,
            fixed = result.fixedExp,
            zakat = result.zakat,
            net = result.netProfit,
            cashIn = result.cashIn,
            cashOut = result.cashOut,
            cashPurchase = i.cashPurchase,
            oldDebt = i.oldDebt,
            cashProfit = result.cashProfit,
            stockInYard = i.stockInYard,
            stockOutYard = i.soldGaj
        )

        viewModelScope.launch {
            try {
                repository.saveOrUpdateWeeklyReport(report.toBackupWeeklyReport())
                repository.saveProfitStrip(net = result.netProfit, gross = result.gross)
                com.imran.clothstore.ui.screens.notif.NotificationCenter.push(
                    "success", "$label — ${formatTaka(result.netProfit)} সংরক্ষিত"
                )
                _savedMessage.value = "$label — ${formatTaka(result.netProfit)} সংরক্ষিত হয়েছে"
            } catch (e: Exception) {
                com.imran.clothstore.ui.screens.notif.NotificationCenter.push(
                    "error", "সাপ্তাহিক হিসাব সেভ করতে ব্যর্থ: ${e.message}"
                )
                _savedMessage.value = "সংরক্ষণে সমস্যা হয়েছে: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun resetAll() {
        _input.value = PaikkariInput()
        _result.value = null
        _currentPage.value = PkPage.CALC
        _validationError.value = null
        _savedMessage.value = null
    }

    fun goToCalcPage() {
        _currentPage.value = PkPage.CALC
    }

    fun clearValidationError() {
        _validationError.value = null
    }

    fun clearSavedMessage() {
        _savedMessage.value = null
    }
}
