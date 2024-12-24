package com.example.splitmategamma.dashboard.bill.model

data class BillResponse(
    val houseBill: HouseBill?,
    val tenantBill: TenantBill?
)

data class HouseBill(
    val houseId: String,
    val billingPeriod: BillingPeriod,
    val electricUsage: List<Usage>,
    val waterUsage: List<Usage>,
    val gasUsage: List<Usage>,
    val totalElectric: Double,
    val totalWater: Double,
    val totalGas: Double,
    val totalHouse: Double,
)

data class TenantBill(
    val billingPeriod: BillingPeriod,
    val totalAmount: Double,
    val utilities: List<Utility>,
    var status: String
)

data class BillingPeriod(
    val start: String,
    val end: String
)

data class Usage(
    val date: String,
    val amount: Double
)

data class Utility(
    val utilityId: String,
    val utilityName: String,
    val totalCost: Double,
    val usageRecords: List<Usage>
)