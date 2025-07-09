// Google Charts laden
google.charts.load("current", {
    packages: ["corechart", "bar", "line"],
    language: "de",
})

// Beispieldaten für Unternehmenskosten
let costData = {
    2024: {
        monthly: [
            ["Monat", "Personalkosten", "Betriebskosten", "Marketing", "IT & Software", "Sonstiges"],
            ["Januar", 45000, 12000, 8000, 5000, 3000],
            ["Februar", 47000, 11500, 9500, 5200, 2800],
            ["März", 46500, 13000, 7500, 4800, 3200],
            ["April", 48000, 12500, 10000, 5500, 3000],
            ["Mai", 49000, 14000, 8500, 5800, 3500],
            ["Juni", 48500, 13500, 11000, 6000, 3200],
            ["Juli", 50000, 15000, 9000, 6200, 3800],
            ["August", 49500, 14500, 12000, 5900, 3600],
            ["September", 51000, 16000, 8800, 6500, 4000],
            ["Oktober", 52000, 15500, 13500, 6800, 4200],
            ["November", 53000, 17000, 10500, 7000, 4500],
            ["Dezember", 54000, 18000, 15000, 7200, 5000],
        ],
        departments: [
            ["Abteilung", "Kosten"],
            ["Vertrieb", 85000],
            ["Marketing", 65000],
            ["IT", 95000],
            ["HR", 45000],
            ["Finanzen", 35000],
            ["Produktion", 120000],
            ["Verwaltung", 55000],
        ],
        budget: [
            ["Kategorie", "Budget", "Ist-Kosten"],
            ["Personalkosten", 580000, 587000],
            ["Betriebskosten", 180000, 175000],
            ["Marketing", 120000, 125000],
            ["IT & Software", 70000, 68000],
            ["Sonstiges", 50000, 45000],
        ],
    },
    2023: {
        monthly: [
            ["Monat", "Personalkosten", "Betriebskosten", "Marketing", "IT & Software", "Sonstiges"],
            ["Januar", 42000, 11000, 7000, 4500, 2500],
            ["Februar", 43500, 10500, 8500, 4700, 2300],
            ["März", 43000, 12000, 6500, 4300, 2700],
            ["April", 44500, 11500, 9000, 5000, 2500],
            ["Mai", 45500, 13000, 7500, 5300, 3000],
            ["Juni", 45000, 12500, 10000, 5500, 2700],
            ["Juli", 46500, 14000, 8000, 5700, 3300],
            ["August", 46000, 13500, 11000, 5400, 3100],
            ["September", 47500, 15000, 7800, 6000, 3500],
            ["Oktober", 48500, 14500, 12500, 6300, 3700],
            ["November", 49500, 16000, 9500, 6500, 4000],
            ["Dezember", 50500, 17000, 14000, 6700, 4500],
        ],
        departments: [
            ["Abteilung", "Kosten"],
            ["Vertrieb", 78000],
            ["Marketing", 58000],
            ["IT", 87000],
            ["HR", 41000],
            ["Finanzen", 32000],
            ["Produktion", 110000],
            ["Verwaltung", 49000],
        ],
        budget: [
            ["Kategorie", "Budget", "Ist-Kosten"],
            ["Personalkosten", 550000, 542000],
            ["Betriebskosten", 170000, 168000],
            ["Marketing", 110000, 115000],
            ["IT & Software", 65000, 63000],
            ["Sonstiges", 45000, 42000],
        ],
    },
}


let currentYear = 2024
//console.log(costData);
costData = firstCompanyData;


// Initialisierung nach dem Laden der Google Charts
google.charts.setOnLoadCallback(initDashboard)

function initDashboard() {
    setupNavigation()
    setupEventListeners()
    updateDashboard()
}

function setupNavigation() {
    const menuItems = document.querySelectorAll(".menu-item")
    const pages = document.querySelectorAll(".page")

    menuItems.forEach((item) => {
        item.addEventListener("click", function (e) {
            e.preventDefault()

            // Remove active class from all menu items and pages
            menuItems.forEach((mi) => mi.classList.remove("active"))
            pages.forEach((page) => page.classList.remove("active"))

            // Add active class to clicked item
            this.classList.add("active")

            // Show corresponding page
            const pageId = this.dataset.page + "-page"
            const targetPage = document.getElementById(pageId)
            if (targetPage) {
                targetPage.classList.add("active")

                // Load page-specific content
                loadPageContent(this.dataset.page)
            }
        })
    })

    // Sidebar toggle
    const sidebarToggle = document.getElementById("sidebar-toggle")
    const sidebar = document.getElementById("sidebar")

    sidebarToggle.addEventListener("click", () => {
        sidebar.classList.toggle("collapsed")
    })
}


function getCompanyData() {
    // Listen for the custom event
    document.addEventListener('companyDataLoaded', function(event) {
        const data = event.detail.data;
        const year = event.detail.year;
        const message = event.detail.message;
        const yearsOfCompany = event.detail.yearsOfCompany;

        console.log('Received data for year:', year);
        console.log('Data:', data);
        console.log(message)
        console.log(yearsOfCompany)

        costData = data;
        updateDashboard()

        const yearSelect = document.getElementById("year-select");

        // Generate options-Array for HTML
        const optionsHTML = yearsOfCompany.map(year => `<option value="${year}">${year}</option>`).join('\n');

        yearSelect.innerHTML = optionsHTML;

        processData(data);
    });
}


function processData(data) {
    // logic
}


function setupEventListeners() {

    document.getElementById("year-select").addEventListener("change", (e) => {
        currentYear = Number.parseInt(e.target.value)
        updateDashboard()
    })

    document.getElementById("company-select").addEventListener("change", (e) => {
      //  updateDashboard()
        getCompanyData()
    })

    // Export buttons
    document.getElementById("export-pdf").addEventListener("click", exportToPDF)
    document.getElementById("export-excel").addEventListener("click", exportToExcel)
}

function loadPageContent(page) {
    switch (page) {
        case "cost-analysis":
            drawCostStructureChart()
            drawYoYComparisonChart()
            break
        case "budget-tracking":
            drawBudgetVsActualChart()
            updateBudgetCards()
            break
        case "trends":
            drawFinanzEntwicklungChart()
            drawTwelveMonthTrendChart()
            break
        case "cost-centers":
            drawCostCentersChart()
            break
        case "departments":
            drawDepartmentsDetailChart()
            break
        case "monthly-reports":
            loadMonthlyReport()
            break
    }
}

function updateDashboard() {
    updateSummaryCards()
    drawMonthlyTrendChart()
    drawCostBreakdownChart()
    drawQuarterlyComparisonChart()
    drawDepartmentCostsChart()
    updateDataTable()
}

function updateSummaryCards() {
    const data = costData[currentYear].monthly
    const lastMonth = data[data.length - 1]
    const previousMonth = data[data.length - 2]

    // Gesamtkosten berechnen
    const totalCosts = lastMonth.slice(4, 10).reduce((sum, cost) => sum + cost, 0)
    const previousTotalCosts = previousMonth.slice(4, 10).reduce((sum, cost) => sum + cost, 0)
    const totalChange = (((totalCosts - previousTotalCosts) / previousTotalCosts) * 100).toFixed(1)

    document.getElementById("total-costs").textContent = formatCurrency(totalCosts)
    document.getElementById("total-change").textContent = `${totalChange > 0 ? "+" : ""}${totalChange}%`
    document.getElementById("total-change").className = `change ${totalChange > 0 ? "positive" : "negative"}`

    // ProduktionsKosten
    const produktionsKosten = lastMonth[4]
    const previousProduktionsKosten = previousMonth[4]
    const produktionChange = (((produktionsKosten - previousProduktionsKosten) / previousProduktionsKosten) * 100).toFixed(1)

    document.getElementById("operating-costs").textContent = formatCurrency(produktionsKosten)
    document.getElementById("operating-change").textContent = `${produktionChange > 0 ? "+" : ""}${produktionChange}%`
    document.getElementById("operating-change").className = `change ${produktionChange > 0 ? "positive" : "negative"}`

    // Personalkosten
    const personalKosten = lastMonth[5]
    const previousPersonalKosten = previousMonth[5]
    const personalChange = (((personalKosten - previousPersonalKosten) / previousPersonalKosten) * 100).toFixed(1)

    document.getElementById("personnel-costs").textContent = formatCurrency(personalKosten)
    document.getElementById("personnel-change").textContent = `${personalChange > 0 ? "+" : ""}${personalChange}%`
    document.getElementById("personnel-change").className = `change ${personalChange > 0 ? "positive" : "negative"}`

    // Marketingkosten
    const marketingKosten = lastMonth[7]
    const previousMarketingKosten = previousMonth[7]
    const marketingChange = (((marketingKosten - previousMarketingKosten) / previousMarketingKosten) * 100).toFixed(1)

    document.getElementById("marketing-costs").textContent = formatCurrency(marketingKosten)
    document.getElementById("marketing-change").textContent = `${marketingChange > 0 ? "+" : ""}${marketingChange}%`
    document.getElementById("marketing-change").className = `change ${marketingChange > 0 ? "positive" : "negative"}`
}

function updateBudgetCards() {
    const data = costData[currentYear].monthly
    const lastMonth = data[data.length - 1]
    const previousMonth = data[data.length - 2]

    // verbleibendes Budget
    const budgetGesamt = lastMonth[1]
    const ausgabenGesamt = lastMonth[10]
    const restBudget = (budgetGesamt - ausgabenGesamt)
    const previousBudgetGesamt = previousMonth[1]
    const previousAusgabenGesamt = previousMonth[10]
    const previousRestBudget = (previousBudgetGesamt - previousAusgabenGesamt)
    // im Vergleich zum Vormonat
    const restBudgetChange = (((restBudget - previousRestBudget) / previousRestBudget) * 100).toFixed(1)
    const restBudgetPercentage = (((budgetGesamt - ausgabenGesamt) / budgetGesamt) * 100).toFixed(1)


    document.getElementById("rest-budget").textContent = formatCurrency(restBudget)
    document.getElementById("restBudget-change").textContent = `${restBudgetChange > 0 ? "+" : ""}${restBudgetChange}% im Vergleich zum Vormonat`
    document.getElementById("restBudget-change").className = `change ${restBudgetChange > 0 ? "negative" : "positive"}`

    document.getElementById("usedBudget").textContent = `${restBudgetPercentage > 0 ? "+" : ""}${restBudgetPercentage}% von ${budgetGesamt}`
    document.getElementById("budget-progress-bar").style.width = `${restBudgetPercentage}%`;
}



function drawMonthlyTrendChart() {
    const data = google.visualization.arrayToDataTable(costData[currentYear].monthly)

    const options = {
        title: `Monatliche Kostenentwicklung ${currentYear}`,
        titleTextStyle: {
            fontSize: 16,
            bold: true,
        },
        curveType: "function",
        legend: { position: "bottom" },
        height: 400,
        colors: ["#667eea", "#533670", "#18ea36", "#f5576c", "#4facfe", "#2E86AB", "#A23B72", "#ece00c", "#C73E1D", "#E76F51"],
        backgroundColor: "transparent",
        chartArea: {
            left: 80,
            top: 60,
            width: "85%",
            height: "75%",
        },
        hAxis: {
            title: "Monat",
            titleTextStyle: { fontSize: 12, bold: true },
        },
        vAxis: {
            title: "Kosten (€)",
            titleTextStyle: { fontSize: 12, bold: true },
            format: "currency",
        },
    }

    const chart = new google.visualization.LineChart(document.getElementById("monthly-trend-chart"))
    chart.draw(data, options)
}

function drawCostBreakdownChart() {
    const monthlyData = costData[currentYear].monthly
    const lastMonth = monthlyData[monthlyData.length - 1]

    const pieData = [
        ["Kategorie", "Kosten"],
        ["Produktionskosten", lastMonth[4]],
        ["Personalkosten", lastMonth[5]],
        ["IT & Software", lastMonth[6]],
        ["Marketing", lastMonth[7]],
        ["Logistik", lastMonth[8]],
        ["Sonstiges", lastMonth[9]],
    ]

    const data = google.visualization.arrayToDataTable(pieData)

    const options = {
        title: `Kostenverteilung - ${monthlyData[monthlyData.length - 1][0]} ${currentYear}`,
        titleTextStyle: {
            fontSize: 16,
            bold: true,
        },
        height: 400,
        colors: ["#2770cd", "#3ea160", "#f093fb", "#f5576c", "#4facfe", "#F4A261"],
        backgroundColor: "transparent",
        chartArea: {
            left: 50,
            top: 60,
            width: "90%",
            height: "75%",
        },
        legend: {
            position: "right",
            textStyle: { fontSize: 12 },
        },
    }

    const chart = new google.visualization.PieChart(document.getElementById("cost-breakdown-chart"))
    chart.draw(data, options)
}

function drawQuarterlyComparisonChart() {
    const monthlyData = costData[currentYear].monthly

    // Quartalsdaten berechnen
    const quarters = [
        ["Quartal", "Q1", "Q2", "Q3", "Q4"],
        [
            "Produktionskosten",
            monthlyData[1][4] + monthlyData[2][4] + monthlyData[3][4],
            monthlyData[4][4] + monthlyData[5][4] + monthlyData[6][4],
            monthlyData[7][4] + monthlyData[8][4] + monthlyData[9][4],
            monthlyData[10][4] + monthlyData[11][4] + monthlyData[12][4],
        ],
        [
            "Personalkosten",
            monthlyData[1][5] + monthlyData[2][5] + monthlyData[3][5],
            monthlyData[4][5] + monthlyData[5][5] + monthlyData[6][5],
            monthlyData[7][5] + monthlyData[8][5] + monthlyData[9][5],
            monthlyData[10][5] + monthlyData[11][5] + monthlyData[12][5],
        ],
        [
            "IT & Software",
            monthlyData[1][6] + monthlyData[2][6] + monthlyData[3][6],
            monthlyData[4][6] + monthlyData[5][6] + monthlyData[6][6],
            monthlyData[7][6] + monthlyData[8][6] + monthlyData[9][6],
            monthlyData[10][6] + monthlyData[11][6] + monthlyData[12][6],
        ],
        [
            "Marketing",
            monthlyData[1][7] + monthlyData[2][7] + monthlyData[3][7],
            monthlyData[4][7] + monthlyData[5][7] + monthlyData[6][7],
            monthlyData[7][7] + monthlyData[8][7] + monthlyData[9][7],
            monthlyData[10][7] + monthlyData[11][7] + monthlyData[12][7],
        ],
    ]

    const data = google.visualization.arrayToDataTable(quarters)

    const options = {
        title: `Quartalsvergleich ${currentYear}`,
        titleTextStyle: {
            fontSize: 16,
            bold: true,
        },
        height: 400,
        colors: ["#205D67", "#639A67", "#D8EBB5", "#D9BF77"],
        backgroundColor: "transparent",
        chartArea: {
            left: 80,
            top: 60,
            width: "85%",
            height: "70%",
        },
        hAxis: {
            title: "Quartal",
            titleTextStyle: { fontSize: 12, bold: true },
        },
        vAxis: {
            title: "Kosten (€)",
            titleTextStyle: { fontSize: 12, bold: true },
            format: "currency",
        },
        legend: { position: "bottom" },
    }

    const chart = new google.visualization.ColumnChart(document.getElementById("quarterly-comparison-chart"))
    chart.draw(data, options)
}

function drawDepartmentCostsChart() {
    const data = google.visualization.arrayToDataTable(costData[currentYear].departments)

    const options = {
        title: `Abteilungskosten ${currentYear}`,
        titleTextStyle: {
            fontSize: 16,
            bold: true,
        },
        height: 400,
        colors: ["#2770cd", "#3ea160", "#f093fb", "#f5576c", "#4facfe"],
        backgroundColor: "transparent",
        chartArea: {
            left: 100,
            top: 60,
            width: "80%",
            height: "70%",
        },
        hAxis: {
            title: "Kosten (€)",
            titleTextStyle: { fontSize: 12, bold: true },
            format: "currency",
        },
        vAxis: {
            title: "Abteilung",
            titleTextStyle: { fontSize: 12, bold: true },
        },
        legend: { position: "none" },
    }

    const chart = new google.visualization.BarChart(document.getElementById("department-costs-chart"))
    chart.draw(data, options)
}

// Additional chart functions for other pages
function drawBudgetVsActualChart() {
    const data = google.visualization.arrayToDataTable(costData[currentYear].budget)

    const options = {
        title: "Budget vs. Ist-Kosten",
        titleTextStyle: { fontSize: 16, bold: true },
        height: 400,
        colors: ["#27ae60", "#e74c3c"],
        backgroundColor: "transparent",
        chartArea: { left: 100, top: 60, width: "80%", height: "70%" },
        hAxis: { title: "Kosten (€)", format: "currency" },
        vAxis: { title: "Kategorie" },
        legend: { position: "bottom" },
    }

    const chart = new google.visualization.BarChart(document.getElementById("budget-vs-actual-chart"))
    chart.draw(data, options)
}


function drawFinanzEntwicklungChart() {
    const monthlyData = costData[currentYear].monthly
    const lastMonth = monthlyData[monthlyData.length - 1]

    // extract our chartDataArray from monthly data
    const result = [["Kategorie", "Wert",  { role: 'style' }]];  //  {role:'style'} damit jeder balken eine Farbe bekommt
    result.push(["Budget", lastMonth[1], '#F2EFE7']);      // add budget to array (position 1 of monthlyData-subarray (letzter Monat))
    result.push(["Gesamtausgaben", lastMonth[10], '#9ACBD0']);      // add "ausgaben_Gesamt" (position 10)
    result.push(["Umsatz", lastMonth[2], '#48A6A7']);      // add umsatz (position 2)
    result.push(["Gewinn", lastMonth[3], '#2973B2']);      // add gewinn (position 3)
    console.log(result)

    const chartData = google.visualization.arrayToDataTable(result)



    const options = {
        title: `Finanzentwicklung ${lastMonth[0]} ${currentYear}`,
        titleTextStyle: {
            fontSize: 16,
            bold: true,
        },
        height: 400,
        backgroundColor: "transparent",
        chartArea: {
            left: 150,
            top: 60,
            width: "75%",
            height: "70%",
        },
        hAxis: {
            title: "Wert in (€)",
            titleTextStyle: { fontSize: 12, bold: true },
            format: "currency",
        },
        vAxis: {
            title: "Kategorie",
            titleTextStyle: { fontSize: 12, bold: true },
        },
        legend: { position: "none" },
    }

    const chart = new google.visualization.BarChart(document.getElementById("kosten-erloese-chart"))
    chart.draw(chartData, options)
}

function drawTwelveMonthTrendChart() {
    const data = google.visualization.arrayToDataTable(costData[currentYear].monthly)

    const options = {
        title: "12-Monats-Kostentrend",
        titleTextStyle: { fontSize: 16, bold: true },
        curveType: "function",
        height: 400,
        colors: ["#667eea", "#533670", "#18ea36", "#f5576c", "#4facfe", "#2E86AB", "#A23B72", "#ece00c", "#C73E1D", "#E76F51"],
        backgroundColor: "transparent",
        chartArea: { left: 80, top: 60, width: "85%", height: "70%" },
        hAxis: { title: "Monat" },
        vAxis: { title: "Kosten (€)", format: "currency" },
        legend: { position: "bottom" },
    }

    const chart = new google.visualization.LineChart(document.getElementById("twelve-month-trend-chart"))
    chart.draw(data, options)
}

function drawCostCentersChart() {
    const costCentersData = [
        ["Kostenstelle", "Kosten"],
        ["Büroausstattung", 25000],
        ["Fahrzeugflotte", 45000],
        ["Miete & Nebenkosten", 85000],
        ["Versicherungen", 15000],
        ["Schulungen", 20000],
        ["Wartung", 30000],

    ]

    const data = google.visualization.arrayToDataTable(costCentersData)

    const options = {
        title: "Kostenstellen-Übersicht",
        titleTextStyle: { fontSize: 16, bold: true },
        height: 400,
        colors: ["#667eea"],
        backgroundColor: "transparent",
        chartArea: { left: 150, top: 60, width: "75%", height: "70%" },
        hAxis: { title: "Kosten (€)", format: "currency" },
        vAxis: { title: "Kostenstelle" },
        legend: { position: "none" },
    }

    const chart = new google.visualization.BarChart(document.getElementById("cost-centers-chart"))
    chart.draw(data, options)
}

function drawDepartmentsDetailChart() {
    const data = google.visualization.arrayToDataTable(costData[currentYear].departments)

    const options = {
        title: "Detaillierte Abteilungskosten",
        titleTextStyle: { fontSize: 16, bold: true },
        height: 400,
        colors: ["#667eea", "#764ba2", "#f093fb", "#f5576c", "#4facfe", "#43e97b", "#38f9d7"],
        backgroundColor: "transparent",
        chartArea: { left: 50, top: 60, width: "90%", height: "75%" },
        legend: { position: "bottom" },
    }

    const chart = new google.visualization.PieChart(document.getElementById("departments-detail-chart"))
    chart.draw(data, options)
}


document.getElementById("month-select").addEventListener("change", (e) => {
    loadMonthlyReport()
})

function loadMonthlyReport() {
    const monthSelect = document.getElementById("month-select")
    const reportContent = document.getElementById("monthly-report-content")

    if (monthSelect && reportContent) {
        const selectedMonth = Number.parseInt(monthSelect.value)
        const monthData = costData[currentYear].monthly[selectedMonth]

        if (monthData) {
            const total = monthData.slice(4, 9).reduce((sum, cost) => sum + cost, 0)
            reportContent.innerHTML = `
                <div class="monthly-report">
                    <h2>${monthData[0]} ${currentYear}</h2>
                    <div class="report-grid">
                        <div class="report-item">
                            <strong>Produktionskosten:</strong> ${formatCurrency(monthData[4])}
                        </div>
                        <div class="report-item">
                            <strong>Personalkosten:</strong> ${formatCurrency(monthData[5])}
                        </div> 
                        <div class="report-item">
                            <strong>IT & Software:</strong> ${formatCurrency(monthData[6])}
                        </div>
                        <div class="report-item">
                            <strong>Marketing:</strong> ${formatCurrency(monthData[7])}
                        </div>
                        <div class="report-item">
                            <strong>Logistik:</strong> ${formatCurrency(monthData[8])}
                        </div>
                        <div class="report-item">
                            <strong>Sonstiges:</strong> ${formatCurrency(monthData[9])}
                        </div>
                        <div class="report-item total">
                            <strong>Gesamtkosten:</strong> ${formatCurrency(total)}
                        </div>
                    </div>
                </div>
            `
        }
    }
}

function updateDataTable() {
    const monthlyData = costData[currentYear].monthly
    const tbody = document.getElementById("cost-table-body")
    if (!tbody) return

    tbody.innerHTML = ""

    const yearlyTotals = [0, 0, 0, 0, 0, 0, 0] // Produktion, Personal, IT, Marketing, Logistik, Sonstiges, Gesamt

    // see Data-Array structure (monthly)
    for (let i = 1; i < monthlyData.length; i++) {
        const row = monthlyData[i]
        const tr = document.createElement("tr")

        const total = row.slice(4,10).reduce((sum, cost) => sum + cost, 0)

        tr.innerHTML = `
            <td>${row[0]}</td>
            <td>${formatCurrency(row[4])}</td>
            <td>${formatCurrency(row[5])}</td>
            <td>${formatCurrency(row[6])}</td>
            <td>${formatCurrency(row[7])}</td>
            <td>${formatCurrency(row[8])}</td>
            <td>${formatCurrency(row[9])}</td>
            <td><strong>${formatCurrency(total)}</strong></td>
        `

        tbody.appendChild(tr)

        // Jahrestotale berechnen
        for (let j = 0; j < row.slice(4,10).length; j++) {
            yearlyTotals[j] += row.slice(4,10)[j]  // += .... row[j]
        }
        yearlyTotals[6] += total
        /*
            wenn die zu addierenden beträge hintereinander im Array sind und keine andere elemente gibt (z.b. Strings)
            for (let j = 1; j < row.length; j++) {
                yearlyTotals[j-1] += row[j];
            }
        */
    }

    // Jahrestotal-Zeile hinzufügen
    const totalRow = document.createElement("tr")
    totalRow.className = "total-row"
    totalRow.innerHTML = `
        <td><strong>Jahrestotal</strong></td>
        <td><strong>${formatCurrency(yearlyTotals[0])}</strong></td>
        <td><strong>${formatCurrency(yearlyTotals[1])}</strong></td>
        <td><strong>${formatCurrency(yearlyTotals[2])}</strong></td>
        <td><strong>${formatCurrency(yearlyTotals[3])}</strong></td>
        <td><strong>${formatCurrency(yearlyTotals[4])}</strong></td>
        <td><strong>${formatCurrency(yearlyTotals[5])}</strong></td>
        <td><strong>${formatCurrency(yearlyTotals[6])}</strong></td>
    `
    tbody.appendChild(totalRow)
}

// Export Functions
async function exportToPDF() {
    try {
        const { jsPDF } = window.jspdf
        const pdf = new jsPDF("l", "mm", "a4") // Landscape orientation

        // Add title
        pdf.setFontSize(20)
        pdf.text("Unternehmens-Kosten Dashboard", 20, 20)

        pdf.setFontSize(12)
        pdf.text(`Jahr: ${currentYear}`, 20, 30)
        pdf.text(`Erstellt am: ${new Date().toLocaleDateString("de-DE")}`, 20, 40)

        // Capture charts and add to PDF
        const charts = document.querySelectorAll(".chart-container")
        let yPosition = 60

        for (let i = 0; i < Math.min(charts.length, 2); i++) {
            // Limit to 2 charts for demo
            const chart = charts[i]
            const canvas = await html2canvas(chart, {
                scale: 1,
                useCORS: true,
                backgroundColor: "#ffffff",
            })

            const imgData = canvas.toDataURL("image/png")
            const imgWidth = 120
            const imgHeight = (canvas.height * imgWidth) / canvas.width

            if (yPosition + imgHeight > 180) {
                // Check if we need a new page
                pdf.addPage()
                yPosition = 20
            }

            pdf.addImage(imgData, "PNG", 20, yPosition, imgWidth, imgHeight)
            yPosition += imgHeight + 20
        }

        // Add summary data
        pdf.addPage()
        pdf.setFontSize(16)
        pdf.text("Kostenzusammenfassung", 20, 20)

        const monthlyData = costData[currentYear].monthly
        const lastMonth = monthlyData[monthlyData.length - 1]
        const totalCosts = lastMonth.slice(1).reduce((sum, cost) => sum + cost, 0)

        pdf.setFontSize(12)
        let textY = 40
        pdf.text(`Gesamtkosten ${lastMonth[0]}: ${formatCurrency(totalCosts)}`, 20, textY)
        textY += 10
        pdf.text(`Personalkosten: ${formatCurrency(lastMonth[1])}`, 20, textY)
        textY += 10
        pdf.text(`Betriebskosten: ${formatCurrency(lastMonth[2])}`, 20, textY)
        textY += 10
        pdf.text(`Marketing: ${formatCurrency(lastMonth[3])}`, 20, textY)
        textY += 10
        pdf.text(`IT & Software: ${formatCurrency(lastMonth[4])}`, 20, textY)
        textY += 10
        pdf.text(`Sonstiges: ${formatCurrency(lastMonth[5])}`, 20, textY)

        pdf.save(`Kosten-Dashboard-${currentYear}.pdf`)

        // Show success message
        showNotification("PDF erfolgreich exportiert!", "success")
    } catch (error) {
        console.error("PDF Export Error:", error)
        showNotification("Fehler beim PDF-Export", "error")
    }
}

function exportToExcel() {
    try {
        const wb = XLSX.utils.book_new()

        // Monthly data sheet
        const monthlyData = costData[currentYear].monthly
        const ws1 = XLSX.utils.aoa_to_sheet(monthlyData)
        XLSX.utils.book_append_sheet(wb, ws1, "Monatliche Kosten")

        // Department data sheet
        const departmentData = costData[currentYear].departments
        const ws2 = XLSX.utils.aoa_to_sheet(departmentData)
        XLSX.utils.book_append_sheet(wb, ws2, "Abteilungskosten")

        // Budget data sheet
        if (costData[currentYear].budget) {
            const budgetData = costData[currentYear].budget
            const ws3 = XLSX.utils.aoa_to_sheet(budgetData)
            XLSX.utils.book_append_sheet(wb, ws3, "Budget Vergleich")
        }

        // Summary sheet
        const summaryData = [["Kostenzusammenfassung", currentYear], [""], ["Kategorie", "Jahreskosten"]]

        const yearlyTotals = [0, 0, 0, 0, 0]
        for (let i = 1; i < monthlyData.length; i++) {
            for (let j = 1; j < monthlyData[i].length; j++) {
                yearlyTotals[j - 1] += monthlyData[i][j]
            }
        }

        summaryData.push(["Personalkosten", yearlyTotals[0]])
        summaryData.push(["Betriebskosten", yearlyTotals[1]])
        summaryData.push(["Marketing", yearlyTotals[2]])
        summaryData.push(["IT & Software", yearlyTotals[3]])
        summaryData.push(["Sonstiges", yearlyTotals[4]])
        summaryData.push(["Gesamtkosten", yearlyTotals.reduce((sum, cost) => sum + cost, 0)])

        const ws4 = XLSX.utils.aoa_to_sheet(summaryData)
        XLSX.utils.book_append_sheet(wb, ws4, "Zusammenfassung")

        XLSX.writeFile(wb, `Kosten-Dashboard-${currentYear}.xlsx`)

        // Show success message
        showNotification("Excel-Datei erfolgreich exportiert!", "success")
    } catch (error) {
        console.error("Excel Export Error:", error)
        showNotification("Fehler beim Excel-Export", "error")
    }
}

function showNotification(message, type = "info") {
    // Create notification element
    const notification = document.createElement("div")
    notification.className = `notification ${type}`
    notification.innerHTML = `
        <i class="fas ${type === "success" ? "fa-check-circle" : "fa-exclamation-circle"}"></i>
        <span>${message}</span>
    `

    // Add styles
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${type === "success" ? "#27ae60" : "#e74c3c"};
        color: white;
        padding: 15px 20px;
        border-radius: 8px;
        box-shadow: 0 5px 20px rgba(0,0,0,0.2);
        z-index: 10000;
        display: flex;
        align-items: center;
        gap: 10px;
        font-weight: 500;
        animation: slideIn 0.3s ease;
    `

    document.body.appendChild(notification)

    // Remove after 3 seconds
    setTimeout(() => {
        notification.style.animation = "slideOut 0.3s ease"
        setTimeout(() => {
            document.body.removeChild(notification)
        }, 300)
    }, 3000)
}

// Add CSS animations for notifications
/*
const style = document.createElement("style")
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    @keyframes slideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
    
    .report-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 15px;
        margin-top: 20px;
    }
    
    .report-item {
        padding: 10px;
        background: #f8f9fa;
        border-radius: 8px;
        border-left: 3px solid #667eea;
    }
    
    .report-item.total {
        background: #e8f4fd;
        font-weight: bold;
    }
`
document.head.appendChild(style)
*/


function formatCurrency(amount) {
    return new Intl.NumberFormat("de-DE", {
        style: "currency",
        currency: "EUR",
        minimumFractionDigits: 0,
        maximumFractionDigits: 0,
    }).format(amount)
}

// Responsive Charts beim Fenster-Resize
window.addEventListener("resize", () => {
    if (typeof google !== "undefined" && google.visualization) {
        setTimeout(() => {
            const activePage = document.querySelector(".page.active")
            if (activePage && activePage.id === "overview-page") {
                updateDashboard()
            } else {
                // Redraw charts for active page
                const pageId = activePage.id.replace("-page", "")
                loadPageContent(pageId)
            }
        }, 100)
    }
})

// Mobile responsiveness
window.addEventListener("resize", () => {
    const sidebar = document.getElementById("sidebar")
    if (window.innerWidth <= 480) {
        sidebar.classList.add("mobile")
    } else {
        sidebar.classList.remove("mobile")
    }
})

// Initialize mobile check
if (window.innerWidth <= 480) {
    document.getElementById("sidebar").classList.add("mobile")
}

/*
// Import necessary libraries
const jspdf = window.jspdf
const html2canvas = window.html2canvas
const XLSX = window.XLSX
*/