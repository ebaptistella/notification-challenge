(ns challenge.common.schema
  "Shared schemas: ID types (SerialId, Uuid) and tolerant refs (IdOrCode, ChannelRefOrList, StatusRef).
   Used by wire, controllers, persistency."
  (:require [schema.core :as s]))

;; ---- ID types (aligned to DB: bigserial, uuid) ----
(s/defschema SerialId
  "Numeric ID: Integer or Long. Corresponds to bigserial in PostgreSQL (JDBC returns Long)."
  (s/cond-pre s/Int (s/pred #(instance? Long %) 'Long)))

(s/defschema Uuid
  "UUID: java.util.UUID or string. For uuid columns in PostgreSQL or opaque identifiers."
  (s/cond-pre java.util.UUID s/Str))

(s/defschema IdOrCode
  "Ref that can be id (SerialId or Uuid) or code (Str/Keyword). Used in category/channel lookup."
  (s/cond-pre SerialId Uuid s/Str s/Keyword))

(s/defschema ChannelRefOrList
  "A channel (IdOrCode) or a list of channels."
  (s/cond-pre SerialId Uuid s/Str s/Keyword [SerialId] [Uuid] [s/Str] [s/Keyword]))

(s/defschema StatusRef
  "Status as string or keyword. Ex: \"delivered\", :delivered."
  (s/cond-pre s/Str s/Keyword))
