package com.textureanalysis;

import com.textureanalysis.converter.JpgDcmConverter;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;

public class Main {

    static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");
        File jpgFile = new File("pies.jpg");
        File dcmFile = new File ("dcm.dcm");
        JpgDcmConverter converter = new JpgDcmConverter();
        try {
            long start = System.currentTimeMillis();
            converter.convert(jpgFile, dcmFile);
            long fin = System.currentTimeMillis();
            String info = ("Encapsulated " + jpgFile + " to " + dcmFile + " in "
                    + (fin - start) + "ms.");
            logger.info(info);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
