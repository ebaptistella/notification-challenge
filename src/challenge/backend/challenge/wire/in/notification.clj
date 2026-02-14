(ns challenge.wire.in.notification
  (:require [challenge.common.schema :as common.schema]
            [challenge.schema :as schema]
            [schema.core :as s]))

;; POST body: notification (message/body), category (id or code), channel (id(s) or code(s)). Refs via common.schema.
(def notification-submit-skeleton
  {:notification {:schema s/Str :required true :doc "Message body (may contain placeholders e.g. {user-name})"}
   :category     {:schema common.schema/IdOrCode :required true :doc "Category id or code (string/keyword)"}
   :channel      {:schema common.schema/ChannelRefOrList :required true :doc "Channel id, code, or list of ids/codes"}})

(s/defschema NotificationSubmitRequest
  (schema/loose-schema notification-submit-skeleton))
