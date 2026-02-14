(ns challenge.infrastructure.http-server.notification
  (:require [challenge.adapters.notification :as adapters.notification]
            [challenge.components.logger :as logger]
            [challenge.controllers.notification :as controllers.notification]
            [challenge.interface.http.response :as response]
            [clojure.string :as str]
            [schema.core :as s]))

(s/defn create-notification-handler
  [{:keys [notification-wire] {:keys [persistency logger]} :components :as request}]
  (let [log (logger/bound logger)]
    (if (nil? notification-wire)
      (response/bad-request "Request body is required")
      (try
        (let [{:keys [body category channel]} (adapters.notification/submit-wire->body-and-refs notification-wire)
              _ (when (str/blank? (str body))
                  (throw (ex-info "Notification body is required" {})))
              result (controllers.notification/submit-notification! body category channel persistency)
              response-wire (adapters.notification/model->submit-response result)]
          (response/accepted response-wire))
        (catch clojure.lang.ExceptionInfo e
          (let [msg (.getMessage e)]
            (if (or (str/includes? (str msg) "not found")
                    (str/includes? (str msg) "Category")
                    (str/includes? (str msg) "Channel"))
              (response/not-found msg)
              (response/bad-request msg))))))))