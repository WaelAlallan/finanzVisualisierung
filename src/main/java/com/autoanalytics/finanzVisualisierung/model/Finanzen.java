package com.autoanalytics.finanzVisualisierung.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*

 */
@Data
@NoArgsConstructor
@AllArgsConstructor


public class Finanzen {

    // private Long unternehmen_id;
    private String unternehmen;
    private String monat_;
    private int produktion_Budget;
    private int produktion_Kosten;
    private int personal_Budget;
    private int personal_Kosten;
    private int IT_Budget;
    private int IT_Kosten;
    private int marketing_Budget;
    private int marketing_Kosten;
    private int logistik_Budget;
    private int logistik_Kosten;
    private int sonstiges_Budget;
    private int sonstiges_Kosten;
    private int budget_Gesamt;
    private int ausgaben_Gesamt;
    private int umsatz_;
    private int gewinn;

}
