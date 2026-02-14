(ns challenge.wire.out.delivery
  (:require [challenge.common.schema :as common.schema]
            [challenge.schema :as schema]
            [schema.core :as s]))

;; API returns strings. Refs via common.schema (id ou code). Adapter normalizes to enum for model.
(def delivery-record-skeleton
  {:user       {:schema common.schema/IdOrCode :required true :doc "User id or identifier"}
   :channel    {:schema common.schema/IdOrCode :required true :doc "Channel id or code"}
   :message    {:schema (s/maybe s/Str) :required false :doc "Resolved message text that was sent"}
   :created-at {:schema (s/maybe s/Str) :required false :doc "Creation timestamp as ISO string"}
   :status     {:schema (s/maybe s/Str) :required false :doc "sent | failed"}})

(s/defschema DeliveryRecord
  (schema/strict-schema delivery-record-skeleton))

;; List response with optional pagination metadata
(def delivery-history-response-skeleton
  {:items   {:schema [DeliveryRecord] :required true :doc "List of delivery records (newest first)"}
   :limit   {:schema (s/maybe s/Int) :required false :doc "Page size"}
   :offset  {:schema (s/maybe s/Int) :required false :doc "Offset for pagination"}
   :total   {:schema (s/maybe s/Int) :required false :doc "Total count (optional)"}})

(s/defschema DeliveryHistoryResponse
  (schema/strict-schema delivery-history-response-skeleton))
