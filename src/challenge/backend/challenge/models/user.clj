(ns challenge.models.user
  (:require [challenge.schema :as schema]
            [schema.core :as s]))

(def user-skeleton
  {:id         {:schema (s/maybe s/Int)             :required false :doc "User unique identifier (auto-generated)"}
   :name       {:schema s/Str                       :required true  :doc "User name"}
   :email      {:schema s/Str                       :required true  :doc "User email"}
   :phone      {:schema (s/maybe s/Str)             :required false :doc "User phone number"}
   :created-at {:schema (s/maybe java.time.Instant) :required false :doc "Creation timestamp"}
   :updated-at {:schema (s/maybe java.time.Instant) :required false :doc "Last update timestamp"}})

(s/defschema User
  (schema/strict-schema user-skeleton))
