#!/usr/bin/env bash
set -euo pipefail
mvn -q -DskipTests install
mvn -q -Pkaravan-metadata exec:java -Dexec.mainClass=io.dscope.tools.karavan.RosettaNetKaravanModelGenerator
echo "Done. Output -> src/main/resources/karavan/metadata/model" 
