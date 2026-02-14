(ns challenge.models.notification-delivery
  (:require [challenge.common.enums :as enums]
            [challenge.schema :as schema]
            [schema.core :as s]))

(enums/defenum delivery-status :sent :failed)

(def notification-delivery-skeleton
  {:id              {:schema (s/maybe s/Int)             :required false :doc "Delivery unique identifier (auto-generated)"}
   :notification-id {:schema s/Int                       :required true  :doc "Notification id"}
   :user-id         {:schema s/Int                       :required true  :doc "User id (recipient)"}
   :channel-id      {:schema s/Int                       :required true  :doc "Channel id (sms, email, push)"}
   :message         {:schema (s/maybe s/Str)             :required false :doc "Resolved message text sent to the user"}
   :status          {:schema (s/maybe DeliveryStatus)     :required false :doc "sent | failed"}
   :created-at      {:schema (s/maybe java.time.Instant) :required false :doc "Creation timestamp"}})

(s/defschema NotificationDelivery
  (schema/strict-schema notification-delivery-skeleton))
