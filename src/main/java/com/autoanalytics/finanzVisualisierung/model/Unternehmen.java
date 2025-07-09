package com.autoanalytics.finanzVisualisierung.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Unternehmen {

   // private Long id;
    private String unternehmen;
    private int gründung;
    private String sitz;
    private int mitarbeiterzahl;
    private int fabrikanzahl;
    private int produktionskapazität;
    private String logistikpartner;
    private int frauenanteil_in_Führung;
    private int co2Bilanz;

}
