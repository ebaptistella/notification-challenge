(ns challenge.models.channel
  "Channel model. Code enum: sms | email | push_notification. Tolerant reader/writer for code."
  (:require [challenge.schema :as schema]
            [clojure.string :as str]
            [schema.core :as s]))

;; ---- Code enum ----
(def channel-code-values #{:sms :email :push_notification})
(s/defschema ChannelCode (s/enum :sms :email :push_notification))

(defn channel-code->str [v]
  (when v (str/replace (str (name (if (keyword? v) v (keyword (str v))))) "-" "_")))

(defn str->channel-code
  "Tolerant reader: string or keyword -> keyword enum."
  [v]
  (when v
    (let [s (str v)
          normalized (str/replace s "-" "_")
          k (keyword normalized)]
      (when (channel-code-values k) k))))

;; ---- Model ----
(def channel-skeleton
  {:id         {:schema (s/maybe s/Int)             :required false :doc "Channel unique identifier (auto-generated)"}
   :code       {:schema ChannelCode                 :required true  :doc "Channel code: sms, email, push_notification"}
   :created-at {:schema (s/maybe java.time.Instant) :required false :doc "Creation timestamp"}
   :updated-at {:schema (s/maybe java.time.Instant) :required false :doc "Last update timestamp"}})

(s/defschema Channel
  (schema/strict-schema channel-skeleton))
