(ns challenge.wire.in.notification
  (:require [challenge.schema :as schema]
            [schema.core :as s]))

;; POST body: notification (message/body), category (id or code), channel (id(s) or code(s)). Tolerant reader: category/channel accept string or keyword.
(def notification-submit-skeleton
  {:notification {:schema s/Str :required true :doc "Message body (may contain placeholders e.g. {user-name})"}
   :category     {:schema (s/cond-pre s/Int s/Str s/Keyword) :required true :doc "Category id or code (string/keyword)"}
   :channel      {:schema (s/cond-pre s/Int s/Str s/Keyword [s/Int] [s/Str] [s/Keyword]) :required true :doc "Channel id, code, or list of ids/codes"}})

(s/defschema NotificationSubmitRequest
  (schema/loose-schema notification-submit-skeleton))
