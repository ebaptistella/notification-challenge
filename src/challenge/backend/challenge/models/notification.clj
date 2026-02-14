(ns challenge.models.notification
  (:require [challenge.common.enums :as enums]
            [challenge.schema :as schema]
            [schema.core :as s]))

(enums/defenum notification-status {:normalize-underscore true} :pending_delivery :delivered)

(def notification-skeleton
  {:id          {:schema (s/maybe s/Int)             :required false :doc "Notification unique identifier (auto-generated)"}
   :category-id {:schema s/Int                       :required true  :doc "Category id the notification belongs to"}
   :body        {:schema s/Str                       :required true  :doc "Notification message body (may contain placeholders)"}
   :status      {:schema (s/maybe NotificationStatus) :required false :doc "pending_delivery | delivered"}
   :created-at  {:schema (s/maybe java.time.Instant) :required false :doc "Creation timestamp"}
   :updated-at  {:schema (s/maybe java.time.Instant) :required false :doc "Last update timestamp"}})

(s/defschema Notification
  (schema/strict-schema notification-skeleton))
