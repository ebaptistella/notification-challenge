(ns challenge.components.notification-worker
  (:require [challenge.components.configuration :as components.configuration]
            [challenge.controllers.delivery :as controllers.delivery]
            [challenge.infrastructure.persistency.notification :as persistency.notification]
            [challenge.infrastructure.notification.email :as notification.email]
            [challenge.infrastructure.notification.push :as notification.push]
            [challenge.infrastructure.notification.sms :as notification.sms]
            [com.stuartsierra.component :as component]
            [schema.core :as s])
  (:import [java.util.concurrent Executors TimeUnit]))

(def ^:private default-poll-interval-ms 2000)
(def ^:private default-thread-pool-size 4)
(def ^:private default-batch-limit 10)

(defn- strategies-by-channel []
  {"sms"               notification.sms/sms-sender
   "email"             notification.email/email-sender
   "push_notification" notification.push/push-sender})

(defrecord NotificationWorkerComponent [config persistency logger poll-interval-ms thread-pool-size batch-limit executor thread running?]
  component/Lifecycle
  (start [this]
    (if thread
      this
      (let [config-map (components.configuration/get-config config)
            poll-ms (or poll-interval-ms (get-in config-map [:notification-worker :poll-interval-ms]) default-poll-interval-ms)
            pool-size (or thread-pool-size (get-in config-map [:notification-worker :thread-pool-size]) default-thread-pool-size)
            limit (or batch-limit (get-in config-map [:notification-worker :batch-limit]) default-batch-limit)
            exec (Executors/newFixedThreadPool (int pool-size))
            strategies (strategies-by-channel)
            run? (atom true)
            worker-thread (Thread.
                           (fn []
                             (while @run?
                               (try
                                 (Thread/sleep (long poll-ms))
                                 (when @run?
                                   (let [pending (persistency.notification/find-pending-notifications limit persistency)]
                                     (doseq [notif pending]
                                       (.submit exec ^Runnable
                                                (fn []
                                                  (try
                                                    (controllers.delivery/process-notification-delivery!
                                                     notif persistency strategies)
                                                    (catch Exception _e
                                                      ;; Keep notification as pending_delivery for retry
                                                      nil))))))
                                 (catch InterruptedException _
                                   (reset! run? false))
                                 (catch Exception _e
                                   ;; Log and continue loop
                                   nil)))))]
        (.setName worker-thread "notification-worker")
        (.setDaemon worker-thread true)
        (.start worker-thread)
        (assoc this :executor exec :thread worker-thread :running? run? :poll-interval-ms poll-ms :thread-pool-size pool-size :batch-limit limit))))

  (stop [this]
    (when running?
      (reset! running? false))
    (when thread
      (.interrupt thread)
      (.join thread 5000))
    (when executor
      (.shutdown executor)
      (.awaitTermination executor 10 TimeUnit/SECONDS))
    (dissoc this :executor :thread :running?)))

(s/defn new-notification-worker
  []
  (map->NotificationWorkerComponent {}))
