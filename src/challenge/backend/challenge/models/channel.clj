(ns challenge.models.channel
  (:require [challenge.common.enums :as enums]
            [challenge.schema :as schema]
            [schema.core :as s]))

(enums/defenum channel-code {:normalize-underscore true} :sms :email :push_notification)

(def channel-skeleton
  {:id         {:schema (s/maybe s/Int)             :required false :doc "Channel unique identifier (auto-generated)"}
   :code       {:schema ChannelCode                 :required true  :doc "Channel code: sms, email, push_notification"}
   :created-at {:schema (s/maybe java.time.Instant) :required false :doc "Creation timestamp"}
   :updated-at {:schema (s/maybe java.time.Instant) :required false :doc "Last update timestamp"}})

(s/defschema Channel
  (schema/strict-schema channel-skeleton))
