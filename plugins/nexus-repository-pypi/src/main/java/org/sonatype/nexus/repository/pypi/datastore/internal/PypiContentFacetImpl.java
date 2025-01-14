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
package org.sonatype.nexus.repository.pypi.datastore.internal;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.common.hash.HashAlgorithm;
import org.sonatype.nexus.repository.content.facet.ContentFacetSupport;
import org.sonatype.nexus.repository.content.fluent.FluentAsset;
import org.sonatype.nexus.repository.content.fluent.FluentComponent;
import org.sonatype.nexus.repository.content.fluent.FluentQuery;
import org.sonatype.nexus.repository.content.store.FormatStoreManager;
import org.sonatype.nexus.repository.pypi.PyPiFormat;
import org.sonatype.nexus.repository.pypi.datastore.PypiContentFacet;
import org.sonatype.nexus.repository.view.Payload;
import org.sonatype.nexus.repository.view.payloads.TempBlob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.nexus.common.entity.Continuations.iterableOf;
import static org.sonatype.nexus.common.entity.Continuations.streamOf;
import static org.sonatype.nexus.common.hash.HashAlgorithm.MD5;
import static org.sonatype.nexus.common.hash.HashAlgorithm.SHA1;
import static org.sonatype.nexus.repository.pypi.datastore.PyPiDataUtils.copyFormatAttributes;
import static org.sonatype.nexus.repository.pypi.internal.PyPiStorageUtils.getNameAttributes;

/**
 * @since 3.29
 */
@Named(PyPiFormat.NAME)
public class PypiContentFacetImpl
    extends ContentFacetSupport
    implements PypiContentFacet
{
  private static final Iterable<HashAlgorithm> HASHING = ImmutableList.of(MD5, SHA1);

  @Inject
  public PypiContentFacetImpl(
      @Named(PyPiFormat.NAME) final FormatStoreManager formatStoreManager)
  {
    super(formatStoreManager);
  }

  @Override
  public Iterable<FluentAsset> browseAssets() {
    return iterableOf(assets()::browse);
  }

  @Override
  public Optional<FluentAsset> getAsset(final String path) {
    checkNotNull(path);
    return assets().path(path).find();
  }

  @Override
  public boolean delete(final String path) {
    checkNotNull(path);
    return assets().path(path).find().map(FluentAsset::delete).orElse(false);
  }

  @Override
  public FluentAsset saveAsset(
      final String packagePath,
      final FluentComponent component,
      final String assetKind,
      final TempBlob tempBlob)
  {
    checkNotNull(packagePath);
    checkNotNull(component);
    checkNotNull(assetKind);
    checkNotNull(tempBlob);

    return assets()
        .path(packagePath)
        .kind(assetKind)
        .component(component)
        .blob(tempBlob)
        .save();
  }

  @Override
  public FluentAsset saveAsset(final String packagePath, final String assetKind, final TempBlob tempBlob) {
    checkNotNull(packagePath);
    checkNotNull(assetKind);
    checkNotNull(tempBlob);

    return assets()
        .path(packagePath)
        .kind(assetKind)
        .blob(tempBlob)
        .save();
  }

  @Override
  public boolean isComponentExists(final String name) {
    return componentsByName(name).count() > 0;
  }

  @Override
  public List<FluentAsset> assetsByComponentName(String name) {
    return streamOf(componentsByName(name)::browse)
        .flatMap(component -> component.assets().stream())
        .collect(Collectors.toList());
  }

  @Override
  public FluentComponent findOrCreateComponent(
      final String name,
      final String version,
      final String normalizedName)
  {
    checkNotNull(name);
    checkNotNull(version);
    checkNotNull(normalizedName);

    FluentComponent component = components().name(normalizedName).version(version).getOrCreate();
    copyFormatAttributes(component, getNameAttributes(name));
    return component;
  }

  @Override
  public TempBlob getTempBlob(final Payload payload) {
    checkNotNull(payload);
    return blobs().ingest(payload, HASHING);
  }

  @Override
  public TempBlob getTempBlob(final InputStream content, @Nullable final String contentType) {
    return blobs().ingest(content, contentType, HASHING);
  }

  private FluentQuery<FluentComponent> componentsByName(final String name) {
    String filter = "name = #{filterParams.nameParam}";
    Map<String, Object> params = ImmutableMap.of("nameParam", name);
    return facet(PypiContentFacet.class).components().byFilter(filter, params);
  }
}
