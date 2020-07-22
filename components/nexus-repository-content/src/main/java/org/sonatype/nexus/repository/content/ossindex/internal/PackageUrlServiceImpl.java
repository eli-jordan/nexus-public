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
package org.sonatype.nexus.repository.content.ossindex.internal;

import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.common.ComponentSupport;
import org.sonatype.goodies.packageurl.PackageUrl;
import org.sonatype.nexus.common.entity.EntityId;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.content.facet.ContentFacet;
import org.sonatype.nexus.repository.content.fluent.FluentComponent;
import org.sonatype.nexus.repository.ossindex.PackageUrlMapping;
import org.sonatype.nexus.repository.ossindex.PackageUrlService;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static java.util.Optional.empty;

/**
 * @since 3.next
 */
@Named
@Singleton
public class PackageUrlServiceImpl
    extends ComponentSupport
    implements PackageUrlService
{
  private final Map<String, PackageUrlMapping> packageUrlMappings;

  @Inject
  public PackageUrlServiceImpl(final Map<String, PackageUrlMapping> packageUrlMappings) {
    this.packageUrlMappings = checkNotNull(packageUrlMappings);
  }

  @Override
  public Optional<PackageUrl> getPackageUrl(final Repository repository, final EntityId componentId) {
    String format = repository.getFormat().getValue();
    PackageUrlMapping mapping = packageUrlMappings.get(format);
    if (mapping != null) {
      try {
        ContentFacet contentFacet = repository.facet(ContentFacet.class);
        Optional<FluentComponent> result = contentFacet.components().find(componentId);
        return result.flatMap(component -> mapping.buildPackageUrl(
            emptyToNull(component.namespace()),
            component.name(),
            emptyToNull(component.version())));
      }
      catch (Exception e) {
        log.debug("Cannot determine package URL coordinates for {} component {}", format, componentId, e);
      }
    }
    return empty();
  }
}