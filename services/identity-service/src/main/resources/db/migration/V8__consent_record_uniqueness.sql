DELETE FROM consent_record existing
USING consent_record duplicate
WHERE existing.id > duplicate.id
  AND existing.account_id = duplicate.account_id
  AND existing.consent_type = duplicate.consent_type
  AND existing.version = duplicate.version;

ALTER TABLE consent_record
    ADD CONSTRAINT uq_consent_record_account_type_version
        UNIQUE (account_id, consent_type, version);
