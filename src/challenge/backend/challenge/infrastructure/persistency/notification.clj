(ns challenge.infrastructure.persistency.notification
  (:require [challenge.adapters.notification :as adapters.notification]
            [challenge.components.persistency :as components.persistency]
            [challenge.models.category :as models.category]
            [challenge.models.channel :as models.channel]
            [challenge.models.notification :as models.notification]
            [next.jdbc :as jdbc]
            [schema.core :as s]))

(s/defn find-category-by-id-or-code :- (s/maybe models.category/Category)
  "Tolerant reader: id-or-code may be int, string, or keyword (category code)."
  [id-or-code :- (s/cond-pre s/Int s/Str s/Keyword)
   persistency :- components.persistency/IPersistencySchema]
  (let [ds (components.persistency/get-datasource persistency)
        row (if (number? id-or-code)
              (jdbc/execute-one! ds ["SELECT id, code, created_at, updated_at FROM category WHERE id = ?" (long id-or-code)])
              (let [code-str (if (keyword? id-or-code) (models.category/category-code->str id-or-code) (str id-or-code))]
                (jdbc/execute-one! ds ["SELECT id, code, created_at, updated_at FROM category WHERE code = ?" code-str])))]
    (adapters.notification/category-db->model row)))

(s/defn find-channel-by-id-or-code :- (s/maybe models.channel/Channel)
  "Tolerant reader: id-or-code may be int, string, or keyword (channel code)."
  [id-or-code :- (s/cond-pre s/Int s/Str s/Keyword)
   persistency :- components.persistency/IPersistencySchema]
  (let [ds (components.persistency/get-datasource persistency)
        row (if (number? id-or-code)
              (jdbc/execute-one! ds ["SELECT id, code, created_at, updated_at FROM channel WHERE id = ?" (long id-or-code)])
              (let [code-str (if (keyword? id-or-code) (models.channel/channel-code->str id-or-code) (str id-or-code))]
                (jdbc/execute-one! ds ["SELECT id, code, created_at, updated_at FROM channel WHERE code = ?" code-str])))]
    (adapters.notification/channel-db->model row)))

(s/defn insert-notification! :- models.notification/Notification
  [notification :- models.notification/Notification
   persistency :- components.persistency/IPersistencySchema]
  (let [ds (components.persistency/get-datasource persistency)
        now (java.time.Instant/now)
        notification-with-ts (assoc notification :created-at now :updated-at now)
        db-data (adapters.notification/notification-model->db notification-with-ts)
        row (jdbc/execute-one! ds
                               ["INSERT INTO notification (category_id, body, status, created_at, updated_at)
                                 VALUES (?, ?, ?, ?::timestamptz, ?::timestamptz) RETURNING id, category_id, body, status, created_at, updated_at"
                                (:category_id db-data)
                                (:body db-data)
                                (:status db-data)
                                (:created_at db-data)
                                (:updated_at db-data)])]
    (adapters.notification/notification-persistency->model row)))

(s/defn find-pending-notifications :- [models.notification/Notification]
  [limit :- s/Int
   persistency :- components.persistency/IPersistencySchema]
  (let [ds (components.persistency/get-datasource persistency)
        rows (jdbc/execute! ds ["SELECT id, category_id, body, status, created_at, updated_at FROM notification WHERE status = ? LIMIT ?" "pending_delivery" limit])]
    (mapv adapters.notification/notification-persistency->model rows)))

(s/defn update-notification-status! :- s/Int
  "Tolerant reader: status may be keyword or string; coerced to string for DB."
  [notification-id :- s/Int
   status :- (s/cond-pre s/Str s/Keyword)
   persistency :- components.persistency/IPersistencySchema]
  (let [ds (components.persistency/get-datasource persistency)
        status-str (or (models.notification/notification-status->str status) (str status))
        result (jdbc/execute! ds ["UPDATE notification SET status = ?, updated_at = now() WHERE id = ?" status-str notification-id])]
    (if (sequential? result) (count result) 1)))
