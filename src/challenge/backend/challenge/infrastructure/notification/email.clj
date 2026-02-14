(ns challenge.infrastructure.notification.email
  (:require [challenge.infrastructure.notification.channel-sender :as channel-sender]
            [challenge.models.user :as models.user])
  (:import [org.slf4j LoggerFactory]))

(defn- log [] (LoggerFactory/getLogger (str *ns*)))

(def email-sender
  (reify channel-sender/ChannelSender
    (send! [_ user resolved-message _channel]
      (try
        (when-let [l (log)]
          (.info l (str "[Email stub] To " (:email user) " (user " (:id user) "): " (subs (str resolved-message) 0 (min 80 (count (str resolved-message)))))))
        :sent
        (catch Exception _ :failed)))))
