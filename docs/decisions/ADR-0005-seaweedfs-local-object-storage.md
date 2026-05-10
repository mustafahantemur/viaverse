# ADR-0005 - Replace MinIO with SeaweedFS for local S3-compatible storage

## Status

Accepted for local development

## Context

MinIO is AGPLv3 licensed, so it is not accepted as a Viaverse local or production-shaped infrastructure dependency.

Viaverse prefers permissive infrastructure licenses for foundation dependencies, including MIT, Apache-2.0, and BSD. SeaweedFS is Apache-2.0 licensed and provides an S3-compatible API suitable for local development.

## Decision

MinIO is removed from local infrastructure.

Local S3-compatible object storage uses SeaweedFS. The local S3 endpoint is exposed at `http://localhost:8333`, and the local media bucket is `viaverse-media-local`.

Application code uses generic object-storage configuration and abstractions. Domain and application layers must not depend on S3, MinIO, or SeaweedFS concepts directly. Media storage should be accessed through a generic port such as `ObjectStoragePort` or `ObjectStorageClient`; SeaweedFS remains a local/dev adapter detail.

Production object storage will be selected in a separate ADR. Provider-specific APIs must not leak into application code.

## Consequences

Local development keeps an S3-like workflow without an AGPL infrastructure dependency.

The production object storage provider remains an explicit future decision.

The storage adapter stays replaceable because application configuration is provider-neutral and S3-compatible.
