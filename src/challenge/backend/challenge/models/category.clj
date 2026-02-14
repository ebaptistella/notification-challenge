(ns challenge.models.category
  "Category model. Code enum: sports | finance | movies. Tolerant reader/writer for code."
  (:require [challenge.schema :as schema]
            [schema.core :as s]))

;; ---- Code enum ----
(def category-code-values #{:sports :finance :movies})
(s/defschema CategoryCode (s/enum :sports :finance :movies))

(defn category-code->str [v]
  (when v (name (if (keyword? v) v (keyword (str v))))))

(defn str->category-code
  "Tolerant reader: string or keyword -> keyword enum."
  [v]
  (when v
    (let [k (if (keyword? v) v (keyword (str v)))]
      (when (category-code-values k) k))))

;; ---- Model ----
(def category-skeleton
  {:id         {:schema (s/maybe s/Int)             :required false :doc "Category unique identifier (auto-generated)"}
   :code       {:schema CategoryCode                :required true  :doc "Category code: sports, finance, movies"}
   :created-at {:schema (s/maybe java.time.Instant) :required false :doc "Creation timestamp"}
   :updated-at {:schema (s/maybe java.time.Instant) :required false :doc "Last update timestamp"}})

(s/defschema Category
  (schema/strict-schema category-skeleton))
