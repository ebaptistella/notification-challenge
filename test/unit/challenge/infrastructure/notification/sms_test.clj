(ns challenge.infrastructure.notification.sms-test
  (:require [challenge.infrastructure.notification.channel-sender :as channel-sender]
            [challenge.infrastructure.notification.sms :as sms]
            [clojure.test :refer [deftest is testing]]))

(deftest send!-returns-sent
  (testing "sms-sender returns :sent"
    (let [user {:id 1 :name "Alice" :email "a@b.com" :phone "+1" :created-at nil :updated-at nil}
          result (channel-sender/send! sms/sms-sender user "Hello" nil)]
      (is (= :sent result)))))
