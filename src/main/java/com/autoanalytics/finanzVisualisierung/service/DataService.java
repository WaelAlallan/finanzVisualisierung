package com.autoanalytics.finanzVisualisierung.service;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class DataService {

    private final JdbcTemplate jdbcTemplate;

    public DataService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public String findUnternehmenById(int id) {
        String sql = "SELECT unternehmen FROM unternehmen WHERE unternehmen_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, String.class);
    }


    // convert given ResultList of sql queries in a structured array
    private Object[][] convertToNestedArray(List<Map<String, Object>> results) {
        if (results.isEmpty()) {
            return new Object[0][0];
        }

        // Get column names from the first row
        Map<String, Object> firstRow = results.get(0);
        //System.out.println(firstRow.toString());
        String[] columnNames = firstRow.keySet().toArray(new String[0]);
        //System.out.println(Arrays.toString(columnNames));

        // Create a nested array: first row is headers, subsequent rows are data
        Object[][] nestedArray = new Object[results.size() + 1][columnNames.length];

        // Set header row
        nestedArray[0] = columnNames;

        // Set data rows
        for (int i = 0; i < results.size(); i++) {
            Map<String, Object> row = results.get(i);
            for (int j = 0; j < columnNames.length; j++) {
                nestedArray[i + 1][j] = row.get(columnNames[j]);
            }
        }
        return nestedArray;
    }

    // get all company names in DB
    public List<String> getAllCompanies() {
        String allCompaniesSQL = """
            select unternehmen from unternehmen order by unternehmen_id""";
        List<Map<String, Object>> companiesResult = jdbcTemplate.queryForList(allCompaniesSQL);
        List<String> companies = new ArrayList<>();

        for (Map<String, Object> company : companiesResult) {
            companies.add(company.get("unternehmen").toString());
        }
        return companies;
    }

    public String getFirstCompany() {
        String firstCompanySQL = """
            select unternehmen from unternehmen order by unternehmen_id limit 1""";
        try {
            String companyName = jdbcTemplate.queryForObject(firstCompanySQL, String.class);
            return companyName;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int getLatestYearOf(String unternehmen) {
        String latestYearSQL = """
                select distinct CAST(substring(monat, 4, 7) AS UNSIGNED) as LatestYear from finanzen f
                    INNER JOIN unternehmen u ON f.unternehmen_id = u.unternehmen_id
                    where u.unternehmen = ?
                    order by LatestYear desc limit 1""";
        try {
            int latestYear = jdbcTemplate.queryForObject(latestYearSQL, Integer.class, unternehmen);
            return latestYear;
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    // get years of Data-Records of given company
    public List<Integer> getAllYearsByUnternehmen(String unternehmen) {
        String yearSQL = """
                SELECT DISTINCT CAST(SUBSTRING(monat, 4, 7) AS UNSIGNED) AS jahr
                        FROM finanzen f join unternehmen u on u.unternehmen_id = f.unternehmen_id
                        where unternehmen = ?
                        ORDER BY jahr desc""";

        List<Map<String, Object>> yearsResult = jdbcTemplate.queryForList(yearSQL, unternehmen);
        List<Integer> years = new ArrayList<>();

        for (Map<String, Object> year : yearsResult) {
            years.add(Integer.parseInt(year.get("jahr").toString()));
        }
        return years;
    }

    // das Jahr aus dem String zu extrahieren
    private int getYear(String date) {
        return Integer.parseInt(date.substring(3, 7));
    }

    //  den Monatsnamen aus dem String zu extrahieren
    private String getMonthName(String date) {
        String monat = date.substring(0, 2);
        return switch (monat) {
            case "01" -> "Januar";
            case "02" -> "Februar";
            case "03" -> "März";
            case "04" -> "April";
            case "05" -> "Mai";
            case "06" -> "Juni";
            case "07" -> "Juli";
            case "08" -> "August";
            case "09" -> "September";
            case "10" -> "Oktober";
            case "11" -> "November";
            case "12" -> "Dezember";
            default -> "Ungültiger Monat";
        };
    }

    // get all Data of given company over years
    public Map<Integer, Map<String, Object[][]>> getDataArray(String unternehmensname){
        // Get company data
        String companySQL = """
            SELECT unternehmen_id, unternehmen, gründung, sitz, mitarbeiterzahl, 
                   fabrikanzahl, produktionskapazität, logistikpartner, 
                   frauenanteil_in_Führung, co2_Bilanz
            FROM unternehmen
            ORDER BY unternehmen_id""";

        // Get financial data
        String financialSQL = """
            SELECT f.monat, f.budget_Gesamt, f.umsatz, f.gewinn,
                   f.produktion_Kosten, f.personal_Kosten, f.IT_Kosten, f.marketing_Kosten, f.logistik_Kosten, f.sonstiges_Kosten, f.ausgaben_Gesamt
            FROM unternehmen u
            INNER JOIN finanzen f ON u.unternehmen_id = f.unternehmen_id
            where u.unternehmen = ? 
            ORDER BY u.unternehmen""";
        // ex. where u.unternehmen_id = 5

        List<Map<String, Object>> companyData = jdbcTemplate.queryForList(companySQL);
        List<Map<String, Object>> financialData = jdbcTemplate.queryForList(financialSQL, unternehmensname);
        List<Integer> years = getAllYearsByUnternehmen(unternehmensname);

        //System.out.println(Arrays.toString(financialData.toArray()));
        //System.out.println(Arrays.toString(years.toArray()));

        Object[][] financialDataArray = convertToNestedArray(financialData);
        //System.out.println(Arrays.deepToString(financialDataArray));

        // Convert to structured format similar JavaScript
        //Object[][] resultDataArray = new Object[years.size()][];
        Map<Integer, Map<String, Object[][]>> resultDataArray =  new LinkedHashMap<>();

        // put the data according to year in map
        for (Integer year : years) {
            resultDataArray.put(year, getYearlyDataByYear(year, unternehmensname));
        }
        return resultDataArray;
    }




    // returns data depends on company and year
    public Map<String, Object[][]> getYearlyDataByYear(int year, String unternehmensname) {
        String monthlyDataByYearSQL = """
            SELECT f.monat, f.budget_Gesamt, f.umsatz, f.gewinn,
                   f.produktion_Kosten, f.personal_Kosten, f.IT_Kosten, f.marketing_Kosten, f.logistik_Kosten, f.sonstiges_Kosten, f.ausgaben_Gesamt
            FROM unternehmen u
            INNER JOIN finanzen f ON u.unternehmen_id = f.unternehmen_id
            where u.unternehmen = ? AND SUBSTRING(f.monat, 4, 7) = ?
            ORDER BY u.unternehmen""";

        List<Map<String, Object>> yearData_tmp = jdbcTemplate.queryForList(monthlyDataByYearSQL, unternehmensname, year);
        Object[][] yearDataArray = convertToNestedArray(yearData_tmp);

        // edit Data-Array: turn month to monthname
        for (int i = 1; i < yearDataArray.length; i++) {
            String monthAsNumber = yearDataArray[i][0].toString();
            yearDataArray[i][0] = getMonthName(monthAsNumber);
        }
       // System.out.println(Arrays.deepToString(yearDataArray));

        // define DataArray to be returned - it will include monthly, department and Budget-Data
        Map<String, Object[][]> yearlyData = new HashMap<>();
        yearlyData.put("monthly", yearDataArray);

        /******** department-Data  **************/
        String departmentsDataByYearSQL = """
            SELECT sum(f.produktion_Kosten) as Produktion, sum(f.personal_Kosten) as Personal, sum(f.IT_Kosten) as IT,
            						sum(f.marketing_Kosten) as Marketing, sum(f.logistik_Kosten) as Logistik
            FROM unternehmen u
            INNER JOIN finanzen f ON u.unternehmen_id = f.unternehmen_id
            where u.unternehmen = ? AND SUBSTRING(f.monat, 4, 7) = ?
            ORDER BY u.unternehmen""";

        Map<String, Object> departmentsData = jdbcTemplate.queryForMap(departmentsDataByYearSQL, unternehmensname, year);
        //System.out.println(departmentsData);

        List<Object[]> departments = new ArrayList<>();
        departments.add(new Object[]{"Abteilung", "Kosten"});

        for (int i = 0; i < departmentsData.size(); i++) {
            String key = departmentsData.keySet().stream().toList().get(i);
            int value = ((BigDecimal) departmentsData.get(key)).intValue();
            departments.add(new Object[]{ key, value});
           // departments.add(new Object[]{ departmentsData.keySet().stream().toList().get(i), departmentsData.values().stream().toList().get(i)});
        }

        Object[][] departments_arr = departments.toArray(new Object[0][]);
        yearlyData.put("departments", departments_arr);

        /****** Budget-Data   ************/
        String budgetDataByYearSQL = """
            SELECT  sum(f.produktion_Budget) as produktion_budget, sum(f.produktion_Kosten) as Produktion, sum(f.personal_Budget) as personal_budget,
                sum(f.personal_Kosten) as Personal, sum(f.IT_Budget) as IT_budget, sum(f.IT_Kosten) as IT, sum(f.marketing_Budget) as marketing_budget,
                sum(f.marketing_Kosten) as Marketing, sum(f.logistik_Budget) as logistik_budget, sum(f.logistik_Kosten) as Logistik,
                sum(f.sonstiges_Budget) as sonstiges_budget, sum(f.sonstiges_Kosten) as Sonstiges
            FROM unternehmen u
            INNER JOIN finanzen f ON u.unternehmen_id = f.unternehmen_id
            where u.unternehmen = ? AND SUBSTRING(f.monat, 4, 7) = ?
            ORDER BY u.unternehmen""";

        Map<String, Object> budgetData = jdbcTemplate.queryForMap(budgetDataByYearSQL, unternehmensname, year);

        List<Object[]> budgets = new ArrayList<>();
        budgets.add(new Object[]{"Kategorie", "Budget", "Ist-Kosten"});

        // Zugriff auf Paare (Budget & Kosten) mit Schrittweite von 2
        for (int i = 0; i < budgetData.size()-1; i+=2) {
            String key = budgetData.keySet().stream().toList().get(i);
            String key_2 = budgetData.keySet().stream().toList().get(i+1);
            int value = Integer.parseInt(budgetData.get(key).toString());
            int value_2 = Integer.parseInt(budgetData.get(key_2).toString());
            budgets.add(new Object[]{key_2, value, value_2});
        }
       // System.out.println(Arrays.deepToString(budgets.toArray()) + "-- budgets-List");
        Object[][] budgets_arr = budgets.toArray(new Object[0][]);
        yearlyData.put("budget", budgets_arr);

        return yearlyData;
    }




}




















