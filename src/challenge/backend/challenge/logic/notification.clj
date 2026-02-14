(ns challenge.logic.notification
  "Pure logic for notification: placeholder resolution.
   Placeholder syntax: {placeholder-key} in en_US.
   Supported keys: {user-name}, {user-email} (and optionally {user-phone})."
  (:require [challenge.models.user :as models.user]
            [clojure.string :as str]
            [schema.core :as s]))

(def ^:const placeholder-pattern
  #"\{([^}]+)\}")

(s/defn resolve-message :- s/Str
  "Replaces placeholders in template with user data. Pure function, no I/O.
   Placeholders: {user-name} -> user name, {user-email} -> user email, {user-phone} -> user phone.
   Unknown placeholders are left as-is or replaced with empty string."
  [template :- s/Str
   user :- models.user/User]
  (let [replacements {"user-name"  (str (:name user))
                      "user-email" (str (:email user))
                      "user-phone" (str (or (:phone user) ""))}]
    (str/replace template placeholder-pattern
                 (fn [[_ key]]
                   (get replacements (str/trim (str key)) (str "{" key "}"))))))
