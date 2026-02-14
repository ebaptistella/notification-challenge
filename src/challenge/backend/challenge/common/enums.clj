(ns challenge.enums
  "Enum as set + strict validation (only enum values) or permissive validation (enum or any keyword).
   Usage: (def status-enum #{:active :inactive :pending})
        (enum? status-enum :active)        => true  (strict)
        (enum-or? status-enum :unknown) => true  (permissive: accepts any keyword)
        (enum-or? status-enum :active)     => true
   Schemas: (enum-schema status-enum) strict, (enum-or-schema status-enum) permissive."
  (:require [clojure.string :as str]
            [schema.core :as s]))

;; ---- Validation ----
(defn enum?
  "Restritivo: returns true only if x is one of the enum values."
  [enum-set x]
  (contains? enum-set x))

(defn enum-or?
  "Permissivo: returns true if x is an enum value or any keyword (ex: accepts future values)."
  [enum-set x]
  (or (contains? enum-set x) (keyword? x)))

;; ---- Schemas ----
(defn enum-schema
  "Schema strict: accepts only keywords that belong to the enum-set."
  [enum-set]
  (s/constrained s/Keyword #(contains? enum-set %)))

(defn enum-or-schema
  "Schema permissive: accepts any keyword (enum or other)."
  [_enum-set]
  s/Keyword)

;; ---- String <-> keyword coercion (DB/wire) ----
(defn- normalize-kw [v underscore?]
  (when v
    (let [s (str v)
          normalized (if underscore? (str/replace s "-" "_") s)]
      (keyword normalized))))

(defn enum->str
  "Keyword -> string. opts: {:normalize-underscore true} to display with underscore."
  [k {:keys [normalize-underscore]}]
  (when k
    (let [n (name (if (keyword? k) k (keyword (str k))))]
      (if normalize-underscore (str/replace n "-" "_") n))))

(defn str->enum
  "Strict: string/keyword -> enum keyword or nil if invalid."
  [v enum-set {:keys [normalize-underscore]}]
  (when v
    (let [k (normalize-kw v (boolean normalize-underscore))]
      (when (contains? enum-set k) k))))

(defn str->enum-or
  "Permissive: string/keyword -> keyword (any value accepted)."
  [v {:keys [normalize-underscore]}]
  (when v (normalize-kw v (boolean normalize-underscore))))

;; ---- Macro optional: define set + schema + coercion in caller namespace ----
(defn- to-pascal [sym]
  (-> (name sym)
      (str/split #"-")
      (->> (map str/capitalize) (apply str))
      symbol))

(defmacro defenum
  "Define in current namespace: prefix-enum (set), SchemaName (strict), prefix->str, str->prefix.
   For permissive schema use (enum-or-schema prefix-enum) in skeleton.
   prefix: e.g. notification-status -> generates notification-status-enum, NotificationStatus, etc.
   opts: optional {:normalize-underscore true} for enums with underscore
   Ex: (defenum notification-status {:normalize-underscore true} :pending_delivery :delivered)"
  [prefix & args]
  (let [opts?   (map? (first args))
        opts    (if opts? (first args) {})
        values  (if opts? (rest args) args)
        enum-set (set values)
        schema-sym (to-pascal prefix)
        enum-sym (symbol (str (name prefix) "-enum"))
        underscore? (:normalize-underscore opts)
        opts-m (if underscore? {:normalize-underscore true} {})]
    `(do
       (def ~enum-sym ~enum-set)
       (s/defschema ~schema-sym (challenge.enums/enum-schema ~enum-sym))
       (defn ~(symbol (str (name prefix) "->str")) [v#] (challenge.enums/enum->str v# ~opts-m))
       (defn ~(symbol (str "str->" (name prefix))) [v#] (challenge.enums/str->enum v# ~enum-sym ~opts-m)))))
