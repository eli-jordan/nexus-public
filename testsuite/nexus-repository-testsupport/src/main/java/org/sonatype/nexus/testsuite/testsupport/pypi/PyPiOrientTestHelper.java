/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.testsuite.testsupport.pypi;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.common.app.FeatureFlag;
import org.sonatype.nexus.common.entity.EntityId;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.cache.CacheInfo;
import org.sonatype.nexus.repository.storage.Component;
import org.sonatype.nexus.repository.storage.ComponentMaintenance;
import org.sonatype.nexus.repository.storage.StorageFacet;
import org.sonatype.nexus.repository.storage.StorageTx;
import org.sonatype.nexus.testsuite.helpers.ComponentAssetTestHelper;

import org.junit.Assert;

import static java.util.Objects.requireNonNull;
import static org.sonatype.nexus.common.app.FeatureFlags.ORIENT_ENABLED;
import static org.sonatype.nexus.repository.storage.MetadataNodeEntityAdapter.P_NAME;

@FeatureFlag(name = ORIENT_ENABLED)
@Named("orient")
@Singleton
@Priority(Integer.MAX_VALUE)
public class PyPiOrientTestHelper
    implements PyPiTestHelper
{
  @Inject
  private ComponentAssetTestHelper componentAssetTestHelper;

  @Override
  public void deleteComponent(final Repository repository, final String name, final String version)
  {
    ComponentMaintenance maintenanceFacet = repository.facet(ComponentMaintenance.class);

    EntityId componentId;
    try (StorageTx tx = repository.facet(StorageFacet.class).txSupplier().get()) {
      tx.begin();
      Component component = tx.findComponentWithProperty(P_NAME, name, tx.findBucket(repository));
      Assert.assertNotNull(component);
      Assert.assertNotNull(component.getEntityMetadata());
      componentId = component.getEntityMetadata().getId();
    }
    Assert.assertFalse(maintenanceFacet.deleteComponent(componentId).isEmpty());
  }

  @Override
  public boolean isRootIndexExist(final Repository repository) {
   return componentAssetTestHelper.assetExists(repository, "simple/") &&
        !requireNonNull(CacheInfo.extractFromAsset(componentAssetTestHelper.attributes(repository, "simple/")))
            .isInvalidated();
  }
}
