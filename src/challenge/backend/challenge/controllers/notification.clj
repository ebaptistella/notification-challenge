(ns challenge.controllers.notification
  (:require [challenge.components.persistency :refer [IPersistencySchema]]
            [challenge.infrastructure.persistency.notification :as persistency.notification]
            [challenge.models.notification :as models.notification]
            [schema.core :as s]))

(s/defn ^:private channel-refs->list
  [channel-ref :- (s/cond-pre s/Int s/Str [s/Int] [s/Str])]
  (if (sequential? channel-ref)
    (seq channel-ref)
    [channel-ref]))

(s/defn submit-notification! :- models.notification/Notification
  "Validates category and channel(s) exist, persists notification with status pending_delivery, returns created notification.
   Throws ex-info with message suitable for 404/400 if category or channel not found."
  [body :- s/Str
   category-ref :- (s/cond-pre s/Int s/Str)
   channel-ref :- (s/cond-pre s/Int s/Str [s/Int] [s/Str])
   persistency :- IPersistencySchema]
  (let [category (persistency.notification/find-category-by-id-or-code category-ref persistency)]
    (when-not category
      (throw (ex-info "Category not found" {:category category-ref})))
    (doseq [ch-ref (channel-refs->list channel-ref)]
      (when-not (persistency.notification/find-channel-by-id-or-code ch-ref persistency)
        (throw (ex-info "Channel not found" {:channel ch-ref}))))
    (persistency.notification/insert-notification!
     {:id nil
      :category-id (:id category)
      :body body
      :status :pending_delivery
      :created-at nil
      :updated-at nil}
     persistency)))
