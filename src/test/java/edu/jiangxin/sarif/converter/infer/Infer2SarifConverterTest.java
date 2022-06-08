package edu.jiangxin.sarif.converter.infer;

import edu.jiangxin.sarif.converter.SarifConverter;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class Infer2SarifConverterTest {

    @Test
    public void testConvertInfer() {
        URL pathUrl = Infer2SarifConverterTest.class.getResource("/infer");
        Assert.assertTrue(pathUrl != null);
        File inputFile = new File(pathUrl.getFile(), "infer_report.json");
        File outputFile = new File(pathUrl.getFile(), "infer_report.sarif");
        SarifConverter.convertByType("infer2sarif", inputFile, outputFile);

    }
}
