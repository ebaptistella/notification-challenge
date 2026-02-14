(ns challenge.wire.persistency.notification
  "DB stores status as string. Adapter (tolerant reader) coerces to enum for model."
  (:require [challenge.schema :as schema]
            [schema.core :as s]))

(def notification-persistency-skeleton
  {:notification/id          {:schema (s/maybe s/Int) :required false :doc "Notification unique identifier"}
   :notification/category-id {:schema s/Int :required true :doc "Category id"}
   :notification/body        {:schema s/Str :required true :doc "Message body"}
   :notification/status      {:schema (s/maybe s/Str) :required false :doc "pending_delivery | delivered"}
   :notification/created-at {:schema (s/maybe java.time.Instant) :required false :doc "Creation timestamp"}
   :notification/updated-at  {:schema (s/maybe java.time.Instant) :required false :doc "Last update timestamp"}})

(s/defschema NotificationPersistency
  (schema/strict-schema notification-persistency-skeleton))

(def notification-db-result-skeleton
  {:id          {:schema (s/maybe s/Int) :required false}
   :category_id {:schema (s/maybe s/Int) :required false}
   :body        {:schema (s/maybe s/Str) :required false}
   :status      {:schema (s/maybe s/Str) :required false}
   :created_at  {:schema (s/maybe s/Any) :required false}
   :updated_at  {:schema (s/maybe s/Any) :required false}})

(s/defschema NotificationDbResult
  (schema/loose-schema notification-db-result-skeleton))

(s/defschema NotificationPersistencyInput
  (s/conditional #(contains? % :notification/id) NotificationPersistency
                 :else NotificationDbResult))
