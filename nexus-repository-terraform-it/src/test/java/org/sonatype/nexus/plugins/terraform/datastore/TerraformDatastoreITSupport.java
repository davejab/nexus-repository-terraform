package org.sonatype.nexus.plugins.terraform.datastore;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.sonatype.nexus.pax.exam.NexusPaxExamSupport;
import org.sonatype.nexus.plugins.terraform.internal.TerraformITSupport;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.content.facet.ContentFacet;
import org.sonatype.nexus.repository.content.fluent.FluentAsset;
import org.sonatype.nexus.testsuite.testsupport.NexusITSupport;
import java.util.Optional;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFileExtend;
import static org.sonatype.nexus.common.app.FeatureFlags.DATASTORE_DEVELOPER;

public class TerraformDatastoreITSupport extends TerraformITSupport {

  public static Optional<FluentAsset> findAsset(Repository repository, String path) {
    return getContent(repository).assets().path(path).find();
  }

  protected static ContentFacet getContent(final Repository repository) {
    return repository.facet(ContentFacet.class);
  }

  @Configuration
  public static Option[] configureNexus() {
    return NexusPaxExamSupport.options(
            NexusITSupport.configureNexusBase(),
            nexusFeature("org.sonatype.nexus.plugins", "nexus-repository-terraform"),
            editConfigurationFileExtend(NEXUS_PROPERTIES_FILE, DATASTORE_DEVELOPER, "true")
    );
  }

}
