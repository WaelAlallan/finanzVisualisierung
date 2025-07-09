package com.autoanalytics.finanzVisualisierung.processor;


import com.autoanalytics.finanzVisualisierung.model.Finanzen;
import com.autoanalytics.finanzVisualisierung.model.Unternehmen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class FinanzenItemProcessor implements ItemProcessor<Finanzen, Finanzen> {

    public static final Logger logger = LoggerFactory.getLogger(FinanzenItemProcessor.class);

    @Override
    public Finanzen process(Finanzen finanzen) throws Exception {
        //...
        logger.info("Processed record: {}", finanzen);
        return finanzen;
    }
}