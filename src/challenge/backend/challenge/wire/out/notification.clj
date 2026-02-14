(ns challenge.wire.out.notification
  (:require [challenge.schema :as schema]
            [schema.core :as s]))

;; 202 Accepted response: notification id
(def notification-submit-response-skeleton
  {:id {:schema s/Int :required true :doc "Created notification id"}})

(s/defschema NotificationSubmitResponse
  (schema/strict-schema notification-submit-response-skeleton))
