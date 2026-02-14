(ns challenge.common.string
  (:require [clojure.string :as str]
            [schema.core :as s]))

(s/defn kebab->snake :- s/Str
  [s :- s/Str]
  (str/replace s #"-" "_"))

(s/defn kebab->pascal :- s/Str
  [s :- s/Str]
  (-> (str/split s #"-")
      (->> (map str/capitalize) (apply str))))
