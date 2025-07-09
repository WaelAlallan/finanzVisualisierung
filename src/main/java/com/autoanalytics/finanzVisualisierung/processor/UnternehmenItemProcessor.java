package com.autoanalytics.finanzVisualisierung.processor;


import com.autoanalytics.finanzVisualisierung.model.Unternehmen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class UnternehmenItemProcessor implements ItemProcessor<Unternehmen, Unternehmen> {

    public static final Logger logger = LoggerFactory.getLogger(UnternehmenItemProcessor.class);

    @Override
    public Unternehmen process(Unternehmen unternehmen) throws Exception {
        //...
        logger.info("Processed record: {}", unternehmen);
        return unternehmen;
    }
}