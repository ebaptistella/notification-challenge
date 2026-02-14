(ns challenge.adapters.delivery
  (:require [challenge.adapters.common.date :as date]
            [challenge.adapters.common.keyword :as keyword]
            [challenge.models.channel :as models.channel]
            [challenge.models.notification-delivery :as models.delivery]
            [challenge.models.user :as models.user]
            [schema.core :as s]))

(defn- ts->instant [v]
  (when v
    (if (instance? java.sql.Timestamp v)
      (.toInstant ^java.sql.Timestamp v)
      (if (string? v) (java.time.Instant/parse v) v))))

(s/defn user-db->model :- (s/maybe models.user/User)
  [db-result]
  (when db-result
    (let [id (or (:id db-result) (get db-result "id"))
          name (or (:name db-result) (get db-result "name"))
          email (or (:email db-result) (get db-result "email"))
          phone (or (:phone db-result) (get db-result "phone"))
          created-at (or (:created_at db-result) (get db-result "created_at"))
          updated-at (or (:updated_at db-result) (get db-result "updated_at"))]
      {:id         id
       :name       name
       :email      email
       :phone      phone
       :created-at (ts->instant created-at)
       :updated-at (ts->instant updated-at)})))

(s/defn delivery-persistency->model :- models.delivery/NotificationDelivery
  [db-result]
  (let [id (or (:id db-result) (get db-result "id"))
        notification-id (or (:notification_id db-result) (get db-result "notification_id"))
        user-id (or (:user_id db-result) (get db-result "user_id"))
        channel-id (or (:channel_id db-result) (get db-result "channel_id"))
        message (or (:message db-result) (get db-result "message"))
        status-raw (or (:status db-result) (get db-result "status"))
        status (models.delivery/str->delivery-status status-raw)
        created-at (or (:created_at db-result) (get db-result "created_at"))]
    {:id              id
     :notification-id notification-id
     :user-id         user-id
     :channel-id      channel-id
     :message         message
     :status          status
     :created-at      (ts->instant created-at)}))

(s/defn delivery-model->db
  [delivery :- models.delivery/NotificationDelivery]
  (let [wire {:delivery/notification-id (:notification-id delivery)
              :delivery/user-id         (:user-id delivery)
              :delivery/channel-id      (:channel-id delivery)
              :delivery/message         (:message delivery)
              :delivery/status          (or (models.delivery/delivery-status->str (:status delivery)) "sent")}
        with-ts (date/convert-instants-to-timestamps wire)
        db (keyword/convert-keys-to-db-format with-ts)]
    db))
