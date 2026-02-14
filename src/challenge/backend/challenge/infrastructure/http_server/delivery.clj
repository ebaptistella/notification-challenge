(ns challenge.infrastructure.http-server.delivery
  (:require [challenge.adapters.notification :as adapters.notification]
            [challenge.infrastructure.persistency.delivery :as persistency.delivery]
            [challenge.interface.http.response :as response]
            [schema.core :as s]))

(s/defn list-deliveries-handler
  [{:keys [query-params] {:keys [persistency]} :components}]
  (let [limit (some-> (or (get query-params :limit) (get query-params "limit")) (Long/parseLong))
        offset (some-> (or (get query-params :offset) (get query-params "offset")) (Long/parseLong))
        opts (cond-> {}
               (some? limit) (assoc :limit (max 1 (min 100 (int limit))))
               (some? offset) (assoc :offset (max 0 (int offset))))
        defaults (merge {:limit 20 :offset 0} opts)
        deliveries (persistency.delivery/list-deliveries persistency defaults)
        items (mapv adapters.notification/delivery-model->wire deliveries)
        body {:items items
              :limit (:limit defaults)
              :offset (:offset defaults)}]
    (response/ok body)))
