(ns challenge.models.notification-delivery
  "NotificationDelivery model. Status enum: sent | failed. Tolerant reader/writer for status."
  (:require [challenge.schema :as schema]
            [schema.core :as s]))

;; ---- Status enum ----
(def delivery-status-values #{:sent :failed})
(s/defschema DeliveryStatus (s/enum :sent :failed))

(defn delivery-status->str [v]
  (when v (name (if (keyword? v) v (keyword (str v))))))

(defn str->delivery-status
  "Tolerant reader: string or keyword -> keyword enum."
  [v]
  (when v
    (let [k (if (keyword? v) v (keyword (str v)))]
      (when (delivery-status-values k) k))))

;; ---- Model ----
(def notification-delivery-skeleton
  {:id              {:schema (s/maybe s/Int)             :required false :doc "Delivery unique identifier (auto-generated)"}
   :notification-id {:schema s/Int                       :required true  :doc "Notification id"}
   :user-id         {:schema s/Int                       :required true  :doc "User id (recipient)"}
   :channel-id      {:schema s/Int                       :required true  :doc "Channel id (sms, email, push)"}
   :message         {:schema (s/maybe s/Str)             :required false :doc "Resolved message text sent to the user"}
   :status          {:schema (s/maybe DeliveryStatus)    :required false :doc "sent | failed"}
   :created-at      {:schema (s/maybe java.time.Instant) :required false :doc "Creation timestamp"}})

(s/defschema NotificationDelivery
  (schema/strict-schema notification-delivery-skeleton))
