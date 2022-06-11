package edu.jiangxin.sarif.converter.infer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.jiangxin.sarif.converter.AbstractConverter;
import edu.jiangxin.sarif.converter.infer.pojo.BugTrace;
import edu.jiangxin.sarif.converter.infer.pojo.InferSchemaGenerated;
import edu.jiangxin.sarif.converter.sarif.pojo.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Infer2SarifConverter extends AbstractConverter {
    @Override
    public void convert(File input, File output) {
        boolean isValid = validate(input, output);
        if (!isValid) {
            logger.error("Param is invalid.");
            return;
        }

        List<InferSchemaGenerated> inferReports = getInferReports(input);
        if (inferReports == null) {
            logger.error("InferReports is null");
            return;
        }

        SarifSchema210 sarifSchema210 = new SarifSchema210();

        boolean isConvertImplSuccessful = convertImpl(inferReports, sarifSchema210);
        if (!isConvertImplSuccessful) {
            logger.error("convertImpl failed");
            return;
        }

        boolean isWriteSuccessful = writeSarif(output, sarifSchema210);
        if (!isWriteSuccessful) {
            logger.error("writeSarif failed");
            return;
        }
    }

    private boolean convertImpl(List<InferSchemaGenerated> inferReports, SarifSchema210 sarifSchema210) {
        Set<ReportingDescriptor> reportingDescriptors = new HashSet<>();
        List<Result> results = new ArrayList<>();

        for (InferSchemaGenerated inferReport : inferReports) {
            ReportingDescriptor reportingDescriptor = new ReportingDescriptor()
                    .withId(inferReport.getBugType())
                    .withName(inferReport.getBugTypeHum())
                    .withProperties(new PropertyBag()
                            .withAdditionalProperty("problem.severity", convertSeverity(inferReport.getSeverity())));
            addReportingDescriptorIfNeed(reportingDescriptors, reportingDescriptor);

            List<Location> locations = new ArrayList<>();
            int problemLineNumber = inferReport.getLine();
            int procedureStartLine = inferReport.getProcedureStartLine();
            Location location = new Location().withPhysicalLocation(new PhysicalLocation()
                    .withArtifactLocation(new ArtifactLocation()
                            .withUri(inferReport.getFile()))
                    .withRegion(new Region()
                            .withMessage(new Message()
                                    .withText(inferReport.getQualifier()))
                            .withStartLine(Math.max(procedureStartLine, 1))
                            .withEndLine(problemLineNumber + 2)
                            .withStartColumn(1)
                            .withEndColumn(1)));
            locations.add(location);

            /*for (BugTrace bugTrace : inferReport.getBugTrace()) {
                int lineNumber = bugTrace.getLineNumber();
                Message message = new Message();
                if (problemLineNumber == lineNumber) {
                    message.withText("");
                    message.withMarkdown("_Defect Here_");
                } else {
                    message.withText(bugTrace.getDescription());
                }

                int startLine = Math.max(lineNumber - 2, 1);
                int endLine = Math.max(lineNumber + 2, 1);
                int startColumn = 1;
                int endColumn = 1;
                Location location = new Location()
                        .withPhysicalLocation(new PhysicalLocation()
                        .withArtifactLocation(new ArtifactLocation()
                                .withUri(bugTrace.getFilename()))
                        .withRegion(new Region()
                                .withMessage(message)
                                .withStartLine(startLine)
                                .withEndLine(endLine)
                                .withStartColumn(startColumn)
                                .withEndColumn(endColumn)));
                locations.add(location);
            }*/

            Result result = new Result()
                    .withRuleId(inferReport.getBugType())
                    .withMessage(new Message()
                            .withText(inferReport.getQualifier()))
                    .withLocations(locations);
            results.add(result);
        }

        Run run = new Run()
                .withTool(new Tool().
                        withDriver(new ToolComponent()
                                .withName("Infer")
                                .withVersion("v1.1.0")
                                .withRules(reportingDescriptors)))
                .withResults(results);
        List<Run> runs = new ArrayList<>();
        runs.add(run);

        try {
            sarifSchema210
                    .with$schema(new URI("https://json.schemastore.org/sarif-2.1.0.json"))
                    .withVersion(SarifSchema210.Version._2_1_0).withRuns(runs);
            return true;
        } catch (URISyntaxException e) {
            logger.error("convertImpl failed: URISyntaxException");
            return false;
        }
    }

    private List<InferSchemaGenerated> getInferReports(File input) {
        ObjectMapper inputObjectMapper = new ObjectMapper();
        List<InferSchemaGenerated> inferReports = null;
        try {
            inferReports = inputObjectMapper.readValue(input, new TypeReference<List<InferSchemaGenerated>>() {
            });
        } catch (StreamReadException e) {
            logger.error("getInferReports failed: StreamReadException");
        } catch (DatabindException e) {
            logger.error("getInferReports failed: DatabindException");
        } catch (IOException e) {
            logger.error("getInferReports failed: IOException");
        }
        return inferReports;
    }

    private boolean writeSarif(File output, SarifSchema210 sarifSchema210) {
        ObjectMapper outputObjectMapper = new ObjectMapper();
        try {
            outputObjectMapper.writeValue(output, sarifSchema210);
            return true;
        } catch (JsonMappingException e) {
            logger.error("writeSarif failed: JsonMappingException");
            return false;
        } catch (JsonParseException e) {
            logger.error("writeSarif failed: JsonParseException");
            return false;
        } catch (IOException e) {
            logger.error("writeSarif failed: IOException");
            return false;
        }
    }

    private void addReportingDescriptorIfNeed(Set<ReportingDescriptor> rules, ReportingDescriptor rule) {
        for (ReportingDescriptor tmpRule : rules) {
            if (tmpRule.getId().equals(rule.getId())) {
                return;
            }
        }
        rules.add(rule);
    }

    private String convertSeverity(String severity) {
        if (severity == null) {
            logger.warn("severity is null");
            return null;
        }
        return severity.toLowerCase(Locale.ROOT);
    }
}
