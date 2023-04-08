# SarifConverter

This project is archived because of Infer has supported SARIF format output since Infer <https://github.com/facebook/infer/pull/1487>

[![Release Version](https://img.shields.io/github/v/release/jiangxincode/SarifConverter?include_prereleases&sort=semver)](

`SARIF`, the Static Analysis Results Interchange Format, defines a standard format for the output of static analysis tools.

You can see more for details about `SARIF`:

* SARIF Home: <https://sarifweb.azurewebsites.net/>
* Static Analysis Results Interchange Format (SARIF) Version 2.1.0: <http://docs.oasis-open.org/sarif/sarif/v2.1.0/sarif-v2.1.0.html>
* SARIF Tutorials: <https://github.com/microsoft/sarif-tutorials>
* SARIF support for code scanning: <https://docs.github.com/en/code-security/code-scanning/integrating-with-code-scanning/sarif-support-for-code-scanning>

This project targets to make a universal SARIF format converter, like:

* Convert Facebook Infer check tool output(JSON format) to SARIF format
* Convert Simain check tool output to SARIF format
* Convert Fireline check tool
* ...

# How to Use

Download the latest Release:

[![Release Version](https://img.shields.io/github/v/release/jiangxincode/SarifConverter?include_prereleases&sort=semver)](https://github.com/jiangxincode/SarifConverter/releases/latest)

```shell
mvn clean package
java -jar ./target/SarifConverter-${VERSION}.jar --input "./src/test/resources/infer_report.json" --output "./target/infer_report.sarif" --type infer2sarif
```

There is a example: <https://github.com/jiangxincode/ApkToolBoxGUI/blob/master/.github/workflows/InferReport.yml>