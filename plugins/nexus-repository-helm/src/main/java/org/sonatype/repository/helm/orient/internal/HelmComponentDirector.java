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
package org.sonatype.repository.helm.orient.internal;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.common.ComponentSupport;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.manager.RepositoryManager;
import org.sonatype.nexus.repository.storage.Bucket;
import org.sonatype.nexus.repository.storage.BucketStore;
import org.sonatype.nexus.repository.storage.Component;
import org.sonatype.nexus.repository.storage.ComponentDirector;
import org.sonatype.repository.helm.internal.HelmFormat;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @since 3.28
 */
@Named(HelmFormat.NAME)
@Singleton
public class HelmComponentDirector
    extends ComponentSupport
    implements ComponentDirector
{
  private final BucketStore bucketStore;

  private final RepositoryManager repositoryManager;

  @Inject
  public HelmComponentDirector(
      final BucketStore bucketStore,
      final RepositoryManager repositoryManager)
  {
    this.bucketStore = checkNotNull(bucketStore);
    this.repositoryManager = checkNotNull(repositoryManager);
  }

  @Override
  public boolean allowMoveTo(final Repository destination) {
    return true;
  }

  @Override
  public boolean allowMoveTo(final Component component, final Repository destination) {
    return repositoryFor(component).isPresent();
  }

  @Override
  public boolean allowMoveFrom(final Repository source) {
    return true;
  }

  private Optional<Repository> repositoryFor(final Component component) {
    return Optional.of(component)
        .map(Component::bucketId)
        .map(bucketStore::getById)
        .map(Bucket::getRepositoryName)
        .map(repositoryManager::get);
  }
}
