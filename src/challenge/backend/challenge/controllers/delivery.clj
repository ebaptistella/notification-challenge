(ns challenge.controllers.delivery
  "Orchestrator: given a notification, find subscribers, resolve placeholders, call channel strategies, persist deliveries."
  (:require [challenge.components.persistency :refer [IPersistencySchema]]
            [challenge.infrastructure.persistency.delivery :as persistency.delivery]
            [challenge.infrastructure.persistency.notification :as persistency.notification]
            [challenge.infrastructure.notification.channel-sender :as channel-sender]
            [challenge.logic.notification :as logic.notification]
            [challenge.models.channel :as models.channel]
            [challenge.models.notification :as models.notification]
            [schema.core :as s]))

(s/defn process-notification-delivery!
  "Queries: users by category, channels per user. Effects: for each (user, channel) resolve message, send! strategy, insert delivery.
   On strategy failure inserts delivery with status failed and continues. Updates notification status to delivered when done."
  [notification :- models.notification/Notification
   persistency :- IPersistencySchema
   strategies-by-channel :- {s/Str s/Any}]
  (let [users (persistency.delivery/users-subscribed-to-category (:category-id notification) persistency)
        template (:body notification)
        notif-id (:id notification)]
    (doseq [user users]
      (let [channels (persistency.delivery/channels-for-user (:id user) persistency)]
        (doseq [channel channels]
          (try
            (let [resolved-msg (logic.notification/resolve-message template user)
                  channel-code-str (models.channel/channel-code->str (:code channel))
                  sender (get strategies-by-channel channel-code-str)
                  result (if sender
                           (channel-sender/send! sender user resolved-msg channel)
                           :failed)
                  status (if (= result :sent) :sent :failed)]
              (persistency.delivery/insert-delivery!
               {:id nil :notification-id notif-id :user-id (:id user) :channel-id (:id channel)
                :message resolved-msg :status status :created-at nil}
               persistency))
            (catch Exception _e
              (persistency.delivery/insert-delivery!
               {:id nil :notification-id notif-id :user-id (:id user) :channel-id (:id channel)
                :message (logic.notification/resolve-message template user) :status :failed :created-at nil}
               persistency))))))
    (persistency.notification/update-notification-status! notif-id :delivered persistency)
    nil))
