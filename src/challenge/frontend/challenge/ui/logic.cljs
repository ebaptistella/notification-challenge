(ns challenge.ui.logic
  "Pure business logic for the frontend."
  (:require [clojure.string :as str]))

(defn today-date
  "Returns today's date in YYYY-MM-DD format.
   Returns:
   - String with current date in YYYY-MM-DD format"
  []
  (let [now (js/Date.)]
    (str (.getFullYear now) "-"
         (-> (.getMonth now) inc str (.padStart 2 "0")) "-"
         (-> (.getDate now) str (.padStart 2 "0")))))
