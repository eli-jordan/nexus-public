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
package org.sonatype.nexus.repository.apt.datastore.internal.hosted;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.sonatype.nexus.repository.Format;
import org.sonatype.nexus.repository.RecipeSupport;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.Type;
import org.sonatype.nexus.repository.apt.datastore.AptContentFacet;
import org.sonatype.nexus.repository.apt.internal.AptFormat;
import org.sonatype.nexus.repository.content.browse.BrowseFacet;
import org.sonatype.nexus.repository.types.HostedType;
import org.sonatype.nexus.repository.view.ConfigurableViewFacet;
import org.sonatype.nexus.repository.view.Router;
import org.sonatype.nexus.repository.view.ViewFacet;

import static org.sonatype.nexus.repository.http.HttpHandlers.notFound;

/**
 * Apt hosted repository recipe.
 *
 * @since 3.next
 */
@Named(AptHostedRecipe.NAME)
@Singleton
public class AptHostedRecipe
    extends RecipeSupport
{
  public static final String NAME = "apt-hosted";

  @Inject
  Provider<ConfigurableViewFacet> viewFacet;

  @Inject
  Provider<AptHostedFacet> aptHostedFacet;

  @Inject
  Provider<AptContentFacet> aptContentFacet;

  @Inject
  Provider<BrowseFacet> browseFacet;

  @Inject
  public AptHostedRecipe(
      @Named(HostedType.NAME) final Type type,
      @Named(AptFormat.NAME) final Format format)
  {
    super(type, format);
  }

  @Override
  public void apply(final Repository repository) throws Exception {
    repository.attach(configure(viewFacet.get()));
    repository.attach(aptHostedFacet.get());
    repository.attach(aptContentFacet.get());
    repository.attach(browseFacet.get());
  }

  private ViewFacet configure(final ConfigurableViewFacet facet) {
    Router.Builder builder = new Router.Builder();

    builder.defaultHandlers(notFound());
    facet.configure(builder.create());
    return facet;
  }
}
