package com.imran.clothstore.ui.screens.paikkari

import kotlin.math.roundToLong

/** ধাপ ১+২+৩ এর সব ইনপুট ফিল্ড — ওয়েব অ্যাপের step1/step2/step3 ফর্ম ফিল্ডের সাথে হুবহু মিল রেখে */
data class PaikkariInput(
    // ধাপ ১: Fixed Costs
    val yearlyRent: Double = 0.0,
    val monthlyElec: Double = 0.0,
    val monthlyWifi: Double = 0.0,
    val weeklyMath: Double = 0.0,
    val weeklyToilet: Double = 0.0,
    // ধাপ ১: Maintenance Costs
    val salary: Double = 0.0,
    val ownerSalary: Double = 0.0,
    val transport: Double = 0.0,
    val vehicle: Double = 0.0,
    val misc: Double = 0.0,
    // ধাপ ২: Stock
    val stockInYard: Double = 0.0,
    val soldGaj: Double = 0.0,
    val profitGaj: Double = 0.0,
    // ধাপ ৩: Sale
    val cashSale: Double = 0.0,
    val creditSale: Double = 0.0,
    // ধাপ ৩: Purchase
    val cashPurchase: Double = 0.0,
    val creditPurchase: Double = 0.0,
    // ধাপ ৩: আদায় ও দেনা
    val oldCollection: Double = 0.0,
    val oldDebt: Double = 0.0
)

/** weeklyFixed() এর সমতুল্য — বাৎসরিক/মাসিক খরচকে সাপ্তাহিকে রূপান্তর করে */
data class WeeklyFixed(
    val wRent: Double,
    val wElec: Double,
    val wWifi: Double,
    val wMath: Double,
    val wToilet: Double,
    val wZakat: Double
) {
    val total: Double get() = wRent + wElec + wWifi + wMath + wToilet + wZakat
    val totalExcludingZakat: Double get() = wRent + wElec + wWifi + wMath + wToilet
}

fun computeWeeklyFixed(input: PaikkariInput): WeeklyFixed {
    val totalSaleAndCashPurchase = input.cashSale + input.creditSale + input.cashPurchase
    return WeeklyFixed(
        wRent = input.yearlyRent / 52.0,
        wElec = input.monthlyElec / 4.333,
        wWifi = input.monthlyWifi / 4.333,
        wMath = input.weeklyMath,
        wToilet = input.weeklyToilet,
        wZakat = totalSaleAndCashPurchase * 0.025 / 52.0
    )
}

/** ভ্যালিডেশন এরর — কোন ধাপে সমস্যা আছে সেটাসহ (goStep() এর সমতুল্য) */
data class ValidationError(val message: String, val goToStep: Int)

/** calculate() এর পুরো আউটপুট — রেজাল্ট পেজের সব ট্যাবে (P&L/রাজস্ব/ক্যাশফ্লো/স্টক) ব্যবহৃত হয় */
data class PaikkariResult(
    val totalSale: Double,
    val estProfit: Double,
    val cogs: Double,
    val gross: Double,
    val cashSoldGaj: Double,
    val cashGross: Double,
    val opex: Double,
    val fixedExp: Double,
    val zakat: Double,
    val netProfit: Double,
    val cashProfit: Double,
    val cashIn: Double,
    val cashOut: Double,
    val netCashflow: Double,
    val creditRatio: Double,
    val cashPct: Double,
    val creditPct: Double,
    val avgPerGaj: Double,
    val netMarginPct: Double,
    val grossMarginPct: Double,
    val cashRatioPct: Double
)

/** ওয়েব অ্যাপের calculate() এর মধ্যকার input-validation চেকগুলোর Kotlin সংস্করণ */
fun validatePaikkariInput(input: PaikkariInput): ValidationError? {
    val totalSale = input.cashSale + input.creditSale
    if (totalSale <= 0) {
        return ValidationError("⚠️ বিক্রির পরিমাণ শূন্য হতে পারবে না। অনুগ্রহ করে Purchase ও Sale তথ্য পূরণ করুন।", goToStep = 3)
    }
    if (input.soldGaj <= 0 || input.profitGaj <= 0) {
        return ValidationError("⚠️ Stock ধাপে বিক্রিত গজ ও প্রতি গজে লাভ পূরণ করুন।", goToStep = 2)
    }
    val purchaseMoney = input.cashPurchase + input.creditPurchase
    if (purchaseMoney > 0 && input.stockInYard <= 0) {
        return ValidationError("⚠️ ক্রয়ের টাকা দিয়েছ, তাই Stock ধাপে ক্রয়কৃত গজ পূরণ করুন।", goToStep = 2)
    }
    return null
}

/** ওয়েব অ্যাপের calculate() ফাংশনের মূল হিসাব-যুক্তির হুবহু Kotlin পুনর্গঠন */
fun computePaikkariResult(input: PaikkariInput): PaikkariResult {
    val cashSale = input.cashSale
    val creditSale = input.creditSale
    val totalSale = cashSale + creditSale

    // আনুমানিক COGS/Gross (গজ × প্রতি গজে লাভ পদ্ধতি)
    val soldGaj = input.soldGaj
    val profitGaj = input.profitGaj
    val estProfit = soldGaj * profitGaj
    val estCogs = totalSale - estProfit

    val cogs = estCogs
    val gross = totalSale - cogs // = estProfit

    // নগদ বিক্রয়ের গজ (অনুপাত ভিত্তিক) ও তার গ্রস প্রফিট
    val cashSaleRatio = if (totalSale > 0) cashSale / totalSale else 0.0
    val cashSoldGaj = soldGaj * cashSaleRatio
    val cashGross = cashSoldGaj * profitGaj

    val opex = input.salary + input.ownerSalary + input.transport + input.vehicle + input.misc
    val f = computeWeeklyFixed(input)
    val zakat = f.wZakat
    val fixedExp = f.totalExcludingZakat
    val netProfit = gross - opex - fixedExp - zakat

    // Cash Profit/Loss: নিট প্রফিটের সেইম লজিক, শুধু নগদ বিক্রয়ের গ্রস প্রফিট দিয়ে
    val cashProfit = cashGross - opex - fixedExp - zakat

    val cashIn = cashSale + input.oldCollection
    val cashOut = input.cashPurchase + input.oldDebt + opex + fixedExp
    val netCashflow = cashIn - cashOut

    val creditRatio = if (totalSale > 0) creditSale / totalSale else 0.0
    val cashPct = if (totalSale > 0) cashSale / totalSale * 100 else 0.0
    val creditPct = if (totalSale > 0) creditSale / totalSale * 100 else 0.0
    val avgPerGaj = if (soldGaj > 0) gross / soldGaj else 0.0

    // রেজাল্ট পেজের রেডিয়াল প্রোগ্রেস (নিট মার্জিন / গ্রস মার্জিন / নগদ অনুপাত)
    val netMarginPct = if (totalSale > 0) (netProfit / totalSale * 100).coerceIn(0.0, 100.0) else 0.0
    val grossMarginPct = if (totalSale > 0) (gross / totalSale * 100).coerceIn(0.0, 100.0) else 0.0
    val cashRatioPct = if (totalSale > 0) (cashIn / totalSale * 100).coerceIn(0.0, 100.0) else 0.0

    return PaikkariResult(
        totalSale = totalSale,
        estProfit = estProfit,
        cogs = cogs,
        gross = gross,
        cashSoldGaj = cashSoldGaj,
        cashGross = cashGross,
        opex = opex,
        fixedExp = fixedExp,
        zakat = zakat,
        netProfit = netProfit,
        cashProfit = cashProfit,
        cashIn = cashIn,
        cashOut = cashOut,
        netCashflow = netCashflow,
        creditRatio = creditRatio,
        cashPct = cashPct,
        creditPct = creditPct,
        avgPerGaj = avgPerGaj,
        netMarginPct = netMarginPct,
        grossMarginPct = grossMarginPct,
        cashRatioPct = cashRatioPct
    )
}

fun Double.rounded(): Long = this.roundToLong()
