(ns challenge.models.notification
  "Notification model. Status enum: pending_delivery | delivered. Tolerant reader/writer for status."
  (:require [challenge.schema :as schema]
            [clojure.string :as str]
            [schema.core :as s]))

;; ---- Status enum ----
(def notification-status-values #{:pending_delivery :delivered})
(s/defschema NotificationStatus (s/enum :pending_delivery :delivered))

(defn notification-status->str [v]
  (when v (str (name (if (keyword? v) v (keyword (str/replace (str v) "-" "_")))))))

(defn str->notification-status
  "Tolerant reader: string or keyword -> keyword enum."
  [v]
  (when v
    (let [k (if (keyword? v) v (keyword (str/replace (str v) "-" "_")))]
      (when (notification-status-values k) k))))

;; ---- Model ----
(def notification-skeleton
  {:id          {:schema (s/maybe s/Int)             :required false :doc "Notification unique identifier (auto-generated)"}
   :category-id {:schema s/Int                       :required true  :doc "Category id the notification belongs to"}
   :body        {:schema s/Str                       :required true  :doc "Notification message body (may contain placeholders)"}
   :status      {:schema (s/maybe NotificationStatus) :required false :doc "pending_delivery | delivered"}
   :created-at  {:schema (s/maybe java.time.Instant) :required false :doc "Creation timestamp"}
   :updated-at  {:schema (s/maybe java.time.Instant) :required false :doc "Last update timestamp"}})

(s/defschema Notification
  (schema/strict-schema notification-skeleton))
