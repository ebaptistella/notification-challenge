(ns challenge.models.category
  (:require [challenge.enums :as enums]
            [challenge.schema :as schema]
            [schema.core :as s]))

(enums/defenum category-code :sports :finance :movies)

(def category-skeleton
  {:id         {:schema (s/maybe s/Int)             :required false :doc "Category unique identifier (auto-generated)"}
   :code       {:schema CategoryCode                :required true  :doc "Category code: sports, finance, movies"}
   :created-at {:schema (s/maybe java.time.Instant) :required false :doc "Creation timestamp"}
   :updated-at {:schema (s/maybe java.time.Instant) :required false :doc "Last update timestamp"}})

(s/defschema Category
  (schema/strict-schema category-skeleton))
