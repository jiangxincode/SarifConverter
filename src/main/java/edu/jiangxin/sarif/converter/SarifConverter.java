package edu.jiangxin.sarif.converter;

import edu.jiangxin.sarif.converter.infer.Infer2SarifConverter;
import org.apache.commons.cli.*;

import java.io.File;

public class SarifConverter {
    public static void main(String[] args) {
        Options options = new Options();

        Option typeOption = Option.builder("t").longOpt("type")
                .argName("type")
                .hasArg()
                .required(true)
                .desc("Set convert type").build();
        options.addOption(typeOption);

        Option inputOption = Option.builder("i").longOpt("input")
                .argName("input")
                .hasArg()
                .required(true)
                .desc("Set input file").build();
        options.addOption(inputOption);

        Option outputOption = Option.builder("o").longOpt("output")
                .argName("output")
                .hasArg()
                .required(true)
                .desc("Set output file").build();
        options.addOption(outputOption);

        CommandLine cmd;
        CommandLineParser parser = new BasicParser();
        try {
            cmd = parser.parse(options, args);
            String type = cmd.getOptionValue("type");
            File inputFile = new File(cmd.getOptionValue("input"));
            File outputFile = new File(cmd.getOptionValue("output"));
            convertByType(type, inputFile, outputFile);
        } catch (ParseException e) {
            System.err.println("ParseException: " + e.getMessage());
        }
    }

    public static void convertByType(String type, File inputFile, File outputFile) {
        switch (type) {
            case "infer2sarif":
                IConverter convert = new Infer2SarifConverter();
                convert.convert(inputFile, outputFile);
                break;
            default:
                System.err.println("Unsupported type: " + type);
        }
    }
}
