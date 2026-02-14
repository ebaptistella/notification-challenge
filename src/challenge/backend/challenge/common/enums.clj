(ns challenge.common.enums
  "Enum as set + strict/permissive validation and schemas. Shared utility (models, adapters, wire).
   Enum as set + strict validation (only enum values) or permissive validation (enum or any keyword)."
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
      (if normalize-underscore (string/kebab->snake n) n))))

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

;; ---- Macro: define set + schema + coercion in caller namespace ----
(defn- to-pascal-symbol [sym]
  (symbol (string/kebab->pascal (name sym))))

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
        schema-sym (to-pascal-symbol prefix)
        enum-sym (symbol (str (name prefix) "-enum"))
        underscore? (:normalize-underscore opts)
        opts-m (if underscore? {:normalize-underscore true} {})]
    `(do
       (def ~enum-sym ~enum-set)
       (s/defschema ~schema-sym (challenge.common.enums/enum-schema ~enum-sym))
       (defn ~(symbol (str (name prefix) "->str")) [v#] (challenge.common.enums/enum->str v# ~opts-m))
       (defn ~(symbol (str "str->" (name prefix))) [v#] (challenge.common.enums/str->enum v# ~enum-sym ~opts-m)))))
