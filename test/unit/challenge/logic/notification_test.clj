(ns challenge.logic.notification-test
  (:require [challenge.logic.notification :as logic.notification]
            [clojure.test :refer [deftest is testing]])
  (:import [java.time Instant]))

(deftest resolve-message-test
  (testing "replaces {user-name} with user name"
    (let [user {:id 1 :name "Alice" :email "alice@example.com" :phone "+1" :created-at nil :updated-at nil}
          template "Hello {user-name}!"]
      (is (= "Hello Alice!" (logic.notification/resolve-message template user)))))

  (testing "replaces {user-email} with user email"
    (let [user {:id 1 :name "Alice" :email "alice@example.com" :phone nil :created-at nil :updated-at nil}
          template "Contact: {user-email}"]
      (is (= "Contact: alice@example.com" (logic.notification/resolve-message template user)))))

  (testing "replaces multiple placeholders"
    (let [user {:id 1 :name "Bob" :email "bob@example.com" :phone "+2" :created-at nil :updated-at nil}
          template "Hi {user-name}, your email is {user-email}."]
      (is (= "Hi Bob, your email is bob@example.com." (logic.notification/resolve-message template user)))))

  (testing "unknown placeholder is left as-is"
    (let [user {:id 1 :name "Alice" :email "a@b.com" :phone nil :created-at nil :updated-at nil}
          template "Hello {user-name} and {unknown}"]
      (is (= "Hello Alice and {unknown}" (logic.notification/resolve-message template user)))))

  (testing "user-phone can be nil"
    (let [user {:id 1 :name "Alice" :email "a@b.com" :phone nil :created-at nil :updated-at nil}
          template "Phone: {user-phone}"]
      (is (= "Phone: " (logic.notification/resolve-message template user))))))
