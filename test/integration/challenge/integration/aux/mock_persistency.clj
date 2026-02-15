(ns challenge.integration.aux.mock-persistency
  (:require [challenge.components.persistency :as components.persistency]
            [com.stuartsierra.component :as component]))

(defprotocol IMockDatasource
  "Mock datasource protocol for in-memory storage"
  (get-storage [this])
  (clear-storage [this]))

(defrecord MockPersistencyComponent [storage]
  component/Lifecycle
  (start [this]
    (if storage
      this
      (assoc this :storage (atom {}))))
  (stop [this]
    (dissoc this :storage))

  components.persistency/IPersistency
  (get-datasource [this]
    this)

  IMockDatasource
  (get-storage [_this]
    storage)
  (clear-storage [_this]
    (reset! storage {})))

(defn new-mock-persistency
  "Creates a new mock persistency component"
  []
  (map->MockPersistencyComponent {}))
