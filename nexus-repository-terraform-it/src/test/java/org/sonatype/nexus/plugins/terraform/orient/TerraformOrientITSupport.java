package org.sonatype.nexus.plugins.terraform.orient;

import org.sonatype.nexus.pax.exam.NexusPaxExamSupport;
import org.sonatype.nexus.plugins.terraform.internal.TerraformITSupport;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.storage.Asset;
import org.sonatype.nexus.repository.storage.MetadataNodeEntityAdapter;
import org.sonatype.nexus.repository.storage.StorageFacet;
import org.sonatype.nexus.repository.storage.StorageTx;
import org.sonatype.nexus.testsuite.testsupport.NexusITSupport;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;

public class TerraformOrientITSupport
        extends TerraformITSupport
{

  public static Asset findAsset(Repository repository, String path) {
    try (StorageTx tx = getStorageTx(repository)) {
      tx.begin();
      return tx.findAssetWithProperty(MetadataNodeEntityAdapter.P_NAME, path, tx.findBucket(repository));
    }
  }

  protected static StorageTx getStorageTx(final Repository repository) {
    return repository.facet(StorageFacet.class).txSupplier().get();
  }

  @Configuration
  public static Option[] configureNexus() {
    return NexusPaxExamSupport.options(
            NexusITSupport.configureNexusBase(),
            nexusFeature("org.sonatype.nexus.plugins", "nexus-repository-terraform")
    );
  }
}
