(ns challenge.infrastructure.notification.push
  (:require [challenge.infrastructure.notification.channel-sender :as channel-sender]
            [challenge.models.user :as models.user])
  (:import [org.slf4j LoggerFactory]))

(defn- log [] (LoggerFactory/getLogger (str *ns*)))

(def push-sender
  (reify channel-sender/ChannelSender
    (send! [_ user resolved-message _channel]
      (try
        (when-let [l (log)]
          (.info l (str "[Push stub] To user " (:id user) " (" (:name user) "): " (subs (str resolved-message) 0 (min 80 (count (str resolved-message)))))))
        :sent
        (catch Exception _ :failed)))))
