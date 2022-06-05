package edu.jiangxin.sarif.converter.infer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.jiangxin.sarif.converter.IConvert;
import edu.jiangxin.sarif.converter.infer.pojo.BugTrace;
import edu.jiangxin.sarif.converter.infer.pojo.InferReport;
import edu.jiangxin.sarif.converter.sarif.pojo.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Infer2SarifConvert implements IConvert {
    @Override
    public void convert(File input, File output) {
        ObjectMapper inputObjectMapper = new ObjectMapper();
        ObjectMapper outputObjectMapper = new ObjectMapper();
        try {
            List<InferReport> inferReports = inputObjectMapper.readValue(input, new TypeReference<List<InferReport>>() {
            });

            Set<ReportingDescriptor> rules = new HashSet<>();
            List<Result> results = new ArrayList<>();

            for (InferReport inferReport : inferReports) {
                String bugType = inferReport.getBugType();
                List<BugTrace> bugTraces = inferReport.getBugTrace();
                ReportingDescriptor rule = new ReportingDescriptor()
                        .withId(bugType)
                        .withProperties(new PropertyBag()
                                .withAdditionalProperty("problem.severity", inferReport.getSeverity()));
                addRuleIfNeed(rules, rule);
                List<Location> locations = new ArrayList<>();
                Location location = new Location()
                        .withPhysicalLocation(new PhysicalLocation()
                                .withArtifactLocation(new ArtifactLocation()
                                        .withUri(inferReport.getFile()))
                                .withRegion(new Region()
                                        .withStartLine(bugTraces.get(0).getLineNumber())
                                        .withEndLine(bugTraces.get(1).getLineNumber())
                                        .withStartColumn(bugTraces.get(0).getColumnNumber())
                                        .withEndColumn(bugTraces.get(1).getColumnNumber())));
                locations.add(location);
                Result result = new Result()
                        .withRuleId(bugType)
                        .withMessage(new Message()
                                .withText(inferReport.getQualifier()))
                        .withLocations(locations);
                results.add(result);
            }

            Run run = new Run()
                    .withTool(new Tool().
                            withDriver(new ToolComponent()
                                    .withName("Infer")
                                    .withRules(rules)))
                    .withResults(results);
            List<Run> runs = new ArrayList<>();
            runs.add(run);
            SarifSchema210 sarifSchema210 = new SarifSchema210()
                    .with$schema(new URI("http://json-schema.org/draft-07/schema#"))
                    .withVersion(SarifSchema210.Version._2_1_0).withRuns(runs);
            outputObjectMapper.writeValue(output, sarifSchema210);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void addRuleIfNeed(Set<ReportingDescriptor> rules, ReportingDescriptor rule) {
        for (ReportingDescriptor tmpRule : rules) {
            if (tmpRule.getId().equals(rule.getId())) {
                return;
            }
        }
        rules.add(rule);
    }
}
