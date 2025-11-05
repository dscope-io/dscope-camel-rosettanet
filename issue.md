### Summary

Add Apache Karavan metadata for the RosettaNet component and all PIP JAXB models under  
`io.dscope.rosettanet.interchange`, so they appear in the Karavan palette with form-based editing.

This includes:
1. Component metadata (`rosettanet.json`)
2. Model metadata (`*.json`, one per `*RequestType/*ResponseType`)
3. Generator tool to auto-create model metadata from JAXB classes
4. GitHub Action to detect drift and auto-open PRs

---

### Why

- Enables visual RosettaNet editing in Karavan
- No need to hand-maintain dozens of PIP structures
- Metadata always stays in sync with JAXB when models update
- Improves developer UX for B2B/Camel integration

---

### Acceptance Criteria ✅

- [ ] `src/main/resources/karavan/metadata/component/rosettanet.json` added
- [ ] `src/main/resources/karavan/metadata/model/*.json` generated for all `*RequestType/*ResponseType`
- [ ] `src/main/resources/karavan/metadata/model-labels.json` included (optional)
- [ ] `io.dscope.tools.karavan.RosettaNetKaravanModelGenerator` committed
- [ ] Maven profile `-Pkaravan-metadata` added to regenerate metadata
- [ ] GitHub workflow added: `.github/workflows/generate-karavan-metadata.yml`
- [ ] Workflow auto-creates PR if metadata changes

---

### Regenerate locally

```bash
mvn -Pkaravan-metadata -q exec:java \
  -Dexec.mainClass=io.dscope.tools.karavan.RosettaNetKaravanModelGenerator
