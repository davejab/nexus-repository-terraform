/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2022-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.terraform.datastore.internal.store;

import javax.inject.Named;

import org.sonatype.nexus.repository.content.store.FormatStoreModule;
import org.sonatype.nexus.plugins.terraform.internal.TerraformFormat;

/**
 * Configures the content store bindings for the 'terraform' format.
 *
 * @since 0.0.6
 */
@Named(TerraformFormat.NAME)
public class TerraformStoreModule
    extends FormatStoreModule<TerraformContentRepositoryDAO,
        TerraformComponentDAO,
        TerraformAssetDAO,
        TerraformAssetBlobDAO,
        TerraformSearchDAO>
{
  // nothing to add...
}
