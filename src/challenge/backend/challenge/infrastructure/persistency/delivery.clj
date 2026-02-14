(ns challenge.infrastructure.persistency.delivery
  (:require [challenge.adapters.delivery :as adapters.delivery]
            [challenge.adapters.notification :as adapters.notification]
            [challenge.components.persistency :as components.persistency]
            [challenge.models.channel :as models.channel]
            [challenge.models.notification-delivery :as models.delivery]
            [challenge.models.user :as models.user]
            [next.jdbc :as jdbc]
            [schema.core :as s]))

(s/defn users-subscribed-to-category :- [models.user/User]
  [category-id :- s/Int
   persistency :- components.persistency/IPersistencySchema]
  (let [ds (components.persistency/get-datasource persistency)
        rows (jdbc/execute! ds
                            ["SELECT u.id, u.name, u.email, u.phone, u.created_at, u.updated_at
                              FROM users u
                              INNER JOIN user_category uc ON uc.user_id = u.id
                              WHERE uc.category_id = ?"
                             category-id])]
    (mapv adapters.delivery/user-db->model rows)))

(s/defn channels-for-user :- [models.channel/Channel]
  [user-id :- s/Int
   persistency :- components.persistency/IPersistencySchema]
  (let [ds (components.persistency/get-datasource persistency)
        rows (jdbc/execute! ds
                            ["SELECT c.id, c.code, c.created_at, c.updated_at
                              FROM channel c
                              INNER JOIN user_channel uc ON uc.channel_id = c.id
                              WHERE uc.user_id = ?"
                             user-id])]
    (mapv adapters.notification/channel-db->model rows)))

(s/defn insert-delivery! :- models.delivery/NotificationDelivery
  [delivery :- models.delivery/NotificationDelivery
   persistency :- components.persistency/IPersistencySchema]
  (let [ds (components.persistency/get-datasource persistency)
        db-data (adapters.delivery/delivery-model->db delivery)
        row (jdbc/execute-one! ds
                              ["INSERT INTO notification_delivery (notification_id, user_id, channel_id, message, status, created_at)
                                VALUES (?, ?, ?, ?, ?, now()) RETURNING id, notification_id, user_id, channel_id, message, status, created_at"
                               (:notification_id db-data)
                               (:user_id db-data)
                               (:channel_id db-data)
                               (:message db-data)
                               (:status db-data)])]
    (adapters.delivery/delivery-persistency->model row)))

(s/defn list-deliveries :- [models.delivery/NotificationDelivery]
  [persistency :- components.persistency/IPersistencySchema
   opts :- {(s/optional-key :limit) s/Int (s/optional-key :offset) s/Int}]
  (let [ds (components.persistency/get-datasource persistency)
        limit (get opts :limit 20)
        offset (get opts :offset 0)
        rows (jdbc/execute! ds
                            ["SELECT id, notification_id, user_id, channel_id, message, status, created_at
                              FROM notification_delivery
                              ORDER BY created_at DESC
                              LIMIT ? OFFSET ?"
                             limit offset])]
    (mapv adapters.delivery/delivery-persistency->model rows)))
