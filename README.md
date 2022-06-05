# SarifConverter

```shell
mvn clean package
java -jar ./target/SarifConverter-0.0.1.jar --input "./src/test/resources/infer_report.json" --output "./target/infer_report.sarif" --type infer2sarif
```