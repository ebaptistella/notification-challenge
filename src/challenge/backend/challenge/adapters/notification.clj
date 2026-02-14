(ns challenge.adapters.notification
  (:require [challenge.common.date :as date]
            [challenge.common.keyword :as keyword]
            [challenge.models.category :as models.category]
            [challenge.models.channel :as models.channel]
            [challenge.models.notification :as models.notification]
            [challenge.models.notification-delivery :as models.delivery]
            [challenge.wire.in.notification :as wire.in.notification]
            [challenge.wire.out.delivery :as wire.out.delivery]
            [challenge.wire.out.notification :as wire.out.notification]
            [challenge.wire.persistency.notification :as wire.persistency.notification]
            [schema.core :as s]))

(defn- ts->instant [v]
  (when v
    (if (instance? java.sql.Timestamp v)
      (.toInstant ^java.sql.Timestamp v)
      (if (string? v) (java.time.Instant/parse v) v))))

(s/defn notification-persistency->model :- models.notification/Notification
  [db-result :- wire.persistency.notification/NotificationPersistencyInput]
  (let [id (or (:notification/id db-result) (:id db-result))
        category-id (or (:notification/category-id db-result) (:category_id db-result))
        body (or (:notification/body db-result) (:body db-result))
        status-raw (or (:notification/status db-result) (:status db-result))
        status (models.notification/str->notification-status status-raw)
        created-at (or (:notification/created-at db-result) (:created_at db-result))
        updated-at (or (:notification/updated-at db-result) (:updated_at db-result))]
    {:id          id
     :category-id category-id
     :body        body
     :status      status
     :created-at  (ts->instant created-at)
     :updated-at  (ts->instant updated-at)}))

(s/defn notification-model->db
  [notification :- models.notification/Notification]
  (let [wire {:notification/category-id (:category-id notification)
              :notification/body        (:body notification)
              :notification/status      (or (models.notification/notification-status->str (:status notification)) "pending_delivery")
              :notification/created-at  (:created-at notification)
              :notification/updated-at  (:updated-at notification)}
        with-ts (date/convert-instants-to-timestamps wire)
        db (keyword/convert-keys-to-db-format with-ts)]
    db))

(s/defn category-db->model :- (s/maybe models.category/Category)
  [db-result]
  (when db-result
    (let [id (or (:id db-result) (get db-result "id"))
          code-raw (or (:code db-result) (get db-result "code"))
          code (models.category/str->category-code code-raw)
          created-at (or (:created_at db-result) (get db-result "created_at"))
          updated-at (or (:updated_at db-result) (get db-result "updated_at"))]
      (when code
        {:id         id
         :code       code
         :created-at (ts->instant created-at)
         :updated-at (ts->instant updated-at)}))))

(s/defn channel-db->model :- (s/maybe models.channel/Channel)
  [db-result]
  (when db-result
    (let [id (or (:id db-result) (get db-result "id"))
          code-raw (or (:code db-result) (get db-result "code"))
          code (models.channel/str->channel-code code-raw)
          created-at (or (:created_at db-result) (get db-result "created_at"))
          updated-at (or (:updated_at db-result) (get db-result "updated_at"))]
      (when code
        {:id         id
         :code       code
         :created-at (ts->instant created-at)
         :updated-at (ts->instant updated-at)}))))

(s/defn submit-wire->body-and-refs
  "Extracts body and category/channel refs from POST wire. Does not resolve ids."
  [wire :- wire.in.notification/NotificationSubmitRequest]
  {:body    (get wire :notification)
   :category (get wire :category)
   :channel  (get wire :channel)})

(s/defn model->submit-response :- wire.out.notification/NotificationSubmitResponse
  [notification :- models.notification/Notification]
  {:id (:id notification)})

(s/defn delivery-model->wire :- wire.out.delivery/DeliveryRecord
  [delivery :- models.delivery/NotificationDelivery]
  {:user      (:user-id delivery)
   :channel   (:channel-id delivery)
   :message   (:message delivery)
   :created-at (when-let [ca (:created-at delivery)] (str ca))
   :status    (models.delivery/delivery-status->str (:status delivery))})
