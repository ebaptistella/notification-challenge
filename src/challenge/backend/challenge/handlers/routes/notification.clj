(ns challenge.handlers.routes.notification
  (:require [challenge.infrastructure.http-server.delivery :as http-server.delivery]
            [challenge.infrastructure.http-server.notification :as http-server.notification]
            [challenge.interceptors.validation :as interceptors.validation]
            [challenge.wire.in.notification :as wire.in.notification]
            [challenge.wire.out.delivery :as wire.out.delivery]
            [challenge.wire.out.error :as wire.out.error]
            [challenge.wire.out.notification :as wire.out.notification]))

(def routes
  #{["/api/v1/deliveries"
     :get
     http-server.delivery/list-deliveries-handler
     :route-name :list-deliveries
     :summary "List delivery history"
     :doc "Returns notification deliveries ordered by created_at descending (newest first). Supports limit and offset."
     :responses {200 {:body wire.out.delivery/DeliveryHistoryResponse
                      :description "List of deliveries (may be empty)"}
                 500 {:body wire.out.error/ErrorResponse
                      :description "Internal server error"}}]

    ["/api/v1/notifications"
     :post
     [interceptors.validation/json-body
      (interceptors.validation/validate-request-body wire.in.notification/NotificationSubmitRequest :notification-wire)
      http-server.notification/create-notification-handler]
     :route-name :create-notification
     :summary "Submit a notification"
     :doc "Submits a notification (category + body). Returns 202 Accepted; delivery is processed asynchronously."
     :request-body {:required true
                    :content {"application/json" {:schema wire.in.notification/NotificationSubmitRequest}}}
     :responses {202 {:body wire.out.notification/NotificationSubmitResponse
                      :description "Notification accepted for delivery"}
                 400 {:body wire.out.error/ErrorResponse
                      :description "Invalid request or missing fields"}
                 404 {:body wire.out.error/ErrorResponse
                      :description "Category or channel not found"}
                 500 {:body wire.out.error/ErrorResponse
                      :description "Internal server error"}}]})
