## Overview

Provide a short summary of the change.

## Motivation

Fixes: #

## Changes Included ✅
- [ ] Added Karavan component metadata (`rosettanet.json`)
- [ ] Added generated model metadata (`src/main/resources/karavan/metadata/model/*.json`)
- [ ] Added generator class (`io.dscope.tools.karavan.RosettaNetKaravanModelGenerator`)
- [ ] Added Maven profile `-Pkaravan-metadata`
- [ ] Added CI workflow `.github/workflows/generate-karavan-metadata.yml`
- [ ] Verified local regeneration

## How to Test Locally 🧪
```
mvn -Pkaravan-metadata exec:java -Dexec.mainClass=io.dscope.tools.karavan.RosettaNetKaravanModelGenerator
git diff src/main/resources/karavan
```
