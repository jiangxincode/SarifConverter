package edu.jiangxin.sarif.converter;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractConverter implements IConverter {

    protected final Logger logger = LogManager.getLogger(this.getClass().getSimpleName());

    protected boolean validate(File input, File output) {
        if (input == null) {
            logger.warn("Converting failed. input is null");
            return false;
        }
        if (output == null) {
            logger.warn("Converting failed. output is null");
            return false;
        }
        if (!input.isFile()) {
            logger.warn("Converting failed. input is not a file");
            return false;
        }
        if (!input.exists()) {
            logger.warn("Converting failed. input does not exist");
            return false;
        }
        return true;
    }
}
